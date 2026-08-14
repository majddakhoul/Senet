package ui;

import board.Board;
import board.Dice;
import gameLogic.Difficulty;
import gameLogic.GameLogic;
import gameLogic.GameLogicForAI;
import gameLogic.Move;
import gameLogic.State;
import persistence.SaveData;
import persistence.SaveManager;
import player.Piece;
import player.Player;
import player.PlayerType;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import java.awt.Frame;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Owns the live {@link State} and drives the turn flow: rolling, waiting
 * for the human to click a piece, running the computer's search on a
 * background thread, and reacting to pause/save/load/new-game requests
 * from the {@link ControlPanel}. This is the only class that mutates the
 * live game state; {@link BoardPanel}, {@link ConsolePanel} and
 * {@link ControlPanel} only ever display what this class tells them to.
 */
public class GameController {

    private enum Phase {
        NO_GAME,
        WAITING_TO_ROLL,
        WAITING_FOR_SELECTION,
        COMPUTER_TURN,
        PAUSED,
        GAME_OVER
    }

    private static final class AiResult {
        final Move move;
        final int nodesVisited;

        AiResult(Move move, int nodesVisited) {
            this.move = move;
            this.nodesVisited = nodesVisited;
        }
    }

    private final BoardPanel boardPanel;
    private final ConsolePanel consolePanel;
    private final ControlPanel controlPanel;
    private final Frame ownerFrame;

    private State state;
    private GameLogic logic;
    private Difficulty difficulty;
    private int aiDepth;
    private boolean debugMode;

    private Phase phase = Phase.NO_GAME;
    private Phase phaseBeforePause = Phase.NO_GAME;
    private List<Piece> currentMovable = Collections.emptyList();
    private byte currentRoll;

    public GameController(BoardPanel boardPanel, ConsolePanel consolePanel, ControlPanel controlPanel, Frame ownerFrame) {
        this.boardPanel = boardPanel;
        this.consolePanel = consolePanel;
        this.controlPanel = controlPanel;
        this.ownerFrame = ownerFrame;

        boardPanel.setClickHandler(this::onHouseClicked);
        controlPanel.attachController(this);
    }

    // ------------------------------------------------------------------
    // New game
    // ------------------------------------------------------------------

    public void promptNewGame() {
        NewGameDialog.Result result = NewGameDialog.showDialog(ownerFrame);
        if (result == null) {
            return;
        }
        startNewGame(result);
    }

    private void startNewGame(NewGameDialog.Result r) {
        Player human = new Player(r.playerName, 'B', PlayerType.HUMAN);
        Player computer = new Player("Computer", 'W', PlayerType.COMPUTER);
        Board board = new Board(human, computer);
        Dice dice = new Dice();

        this.state = new State(board, human, computer, human, dice);
        this.logic = new GameLogic(board, consolePanel);
        this.difficulty = r.difficulty;
        this.aiDepth = r.searchDepth;
        this.debugMode = r.debugLogging;
        this.phase = Phase.WAITING_TO_ROLL;
        this.currentMovable = Collections.emptyList();

        boardPanel.setBoard(board);
        boardPanel.setHighlightedHouses(null);

        consolePanel.clear();
        consolePanel.logSystem("New game started: " + r.playerName + " (Blue) vs Computer (White).");
        consolePanel.logSystem("Difficulty: " + r.difficulty + " - search depth " + r.searchDepth +
                (r.debugLogging ? ". Verbose AI search logging is on (see the terminal)." : "."));

        controlPanel.setGameInProgress(true);
        controlPanel.setPauseButtonText("Pause");
        controlPanel.setPauseEnabled(true);
        controlPanel.setRollEnabled(true);
        updateStatusLabels();
    }

    // ------------------------------------------------------------------
    // Human turn
    // ------------------------------------------------------------------

    public void rollDice() {
        if (phase != Phase.WAITING_TO_ROLL || state == null) {
            return;
        }

        Player current = state.getCurrentPlayer();
        if (current.isComputer()) {
            return;
        }

        byte roll = state.getLastDice().roll();
        currentRoll = roll;
        consolePanel.logPlayer(current.getName() + " throws the sticks: " + roll + " move point(s).");

        currentMovable = logic.getMovablePieces(current, roll);

        if (currentMovable.isEmpty()) {
            consolePanel.logSystem("No legal moves for that roll. Turn skipped.");
            state.switchTurn();
            advanceIfComputerTurn();
            return;
        }

        Set<Integer> houses = new HashSet<>();
        for (Piece p : currentMovable) {
            houses.add(p.getPosition().toHouseNumber());
        }
        boardPanel.setHighlightedHouses(houses);

        phase = Phase.WAITING_FOR_SELECTION;
        controlPanel.setRollEnabled(false);
        updateStatusLabels();
    }

    public void onHouseClicked(int houseNumber) {
        if (phase != Phase.WAITING_FOR_SELECTION) {
            return;
        }

        for (Piece p : currentMovable) {
            if (p.getPosition() != null && p.getPosition().toHouseNumber() == houseNumber) {
                performHumanMove(p);
                return;
            }
        }
    }

    private void performHumanMove(Piece piece) {
        Player current = state.getCurrentPlayer();
        consolePanel.logPlayer(current.getName() + " moves " + describe(piece) + " forward " + currentRoll + " house(s).");

        logic.movePiece(piece, currentRoll);
        logic.applyForcedHouseRule(current, piece);

        boardPanel.setHighlightedHouses(null);
        boardPanel.repaint();

        if (current.hasWon()) {
            onGameOver(current);
            return;
        }

        state.switchTurn();
        advanceIfComputerTurn();
    }

    // ------------------------------------------------------------------
    // Computer turn
    // ------------------------------------------------------------------

    private void advanceIfComputerTurn() {
        if (phase == Phase.PAUSED || phase == Phase.GAME_OVER || phase == Phase.NO_GAME) {
            return;
        }

        Player current = state.getCurrentPlayer();

        if (current.isComputer()) {
            phase = Phase.COMPUTER_TURN;
            controlPanel.setRollEnabled(false);
            controlPanel.setPauseEnabled(false);
            updateStatusLabels();

            Timer timer = new Timer(400, e -> runComputerTurn());
            timer.setRepeats(false);
            timer.start();
        } else {
            phase = Phase.WAITING_TO_ROLL;
            controlPanel.setRollEnabled(true);
            controlPanel.setPauseEnabled(true);
            updateStatusLabels();
        }
    }

    private void runComputerTurn() {
        final Player current = state.getCurrentPlayer();
        final byte roll = state.getLastDice().roll();
        consolePanel.logComputer(current.getName() + " throws the sticks: " + roll + " move point(s).");

        List<Piece> movable = logic.getMovablePieces(current, roll);

        if (movable.isEmpty()) {
            consolePanel.logSystem("No legal moves for that roll. Turn skipped.");
            state.switchTurn();
            advanceIfComputerTurn();
            return;
        }

        consolePanel.logComputer(current.getName() + " is thinking (difficulty: " + difficulty +
                ", search depth: " + aiDepth + ")...");

        SwingWorker<AiResult, Void> worker = new SwingWorker<AiResult, Void>() {
            @Override
            protected AiResult doInBackground() {
                GameLogicForAI ai = new GameLogicForAI(current.getColor(), debugMode);
                Move best = ai.getBestMove(state, aiDepth, roll);
                return new AiResult(best, ai.getVisitedNodes());
            }

            @Override
            protected void done() {
                try {
                    completeComputerTurn(current, get());
                } catch (Exception ex) {
                    consolePanel.logWarning("The computer encountered an error while thinking: " + ex.getMessage());
                }
            }
        };

        worker.execute();
    }

    private void completeComputerTurn(Player current, AiResult result) {
        if (result.move == null) {
            consolePanel.logSystem("The computer could not find a legal move. Turn skipped.");
            state.switchTurn();
            advanceIfComputerTurn();
            return;
        }

        Piece selected = current.getPieceById(result.move.getPieceId());
        consolePanel.logComputer(current.getName() + " moves " + describe(selected) + " forward " +
                result.move.getSteps() + " house(s). (explored " + result.nodesVisited + " search nodes)");

        logic.movePiece(selected, result.move.getSteps());
        logic.applyForcedHouseRule(current, selected);
        boardPanel.repaint();

        if (current.hasWon()) {
            onGameOver(current);
            return;
        }

        state.switchTurn();
        advanceIfComputerTurn();
    }

    // ------------------------------------------------------------------
    // Pause / save / load / rules / game over
    // ------------------------------------------------------------------

    public void togglePause() {
        if (state == null) {
            return;
        }

        if (phase == Phase.PAUSED) {
            phase = phaseBeforePause;
            controlPanel.setPauseButtonText("Pause");
            consolePanel.logSystem("Game resumed.");
            controlPanel.setRollEnabled(phase == Phase.WAITING_TO_ROLL);
        } else if (phase == Phase.WAITING_TO_ROLL || phase == Phase.WAITING_FOR_SELECTION) {
            phaseBeforePause = phase;
            phase = Phase.PAUSED;
            controlPanel.setPauseButtonText("Resume");
            controlPanel.setRollEnabled(false);
            consolePanel.logSystem("Game paused. Your board position is kept - press Resume to continue.");
        }

        updateStatusLabels();
    }

    public void promptSave() {
        if (state == null) {
            return;
        }

        String name = JOptionPane.showInputDialog(ownerFrame, "Save slot name:", "quicksave");
        if (name == null) {
            return;
        }
        if (name.trim().isEmpty()) {
            name = "quicksave";
        }

        try {
            SaveManager.save(new SaveData(state, difficulty, aiDepth, debugMode), name);
            consolePanel.logSystem("Game saved to slot '" + name + "'.");
        } catch (IOException e) {
            consolePanel.logWarning("Could not save the game: " + e.getMessage());
        }
    }

    public void promptLoad() {
        List<String> saves = SaveManager.listSaves();

        if (saves.isEmpty()) {
            JOptionPane.showMessageDialog(ownerFrame, "There are no saved games yet.",
                    "Load Game", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Object choice = JOptionPane.showInputDialog(ownerFrame, "Choose a save to load:", "Load Game",
                JOptionPane.PLAIN_MESSAGE, null, saves.toArray(), saves.get(0));

        if (choice == null) {
            return;
        }

        String slotName = (String) choice;

        try {
            SaveData data = SaveManager.load(slotName);

            this.state = data.getState();
            this.difficulty = data.getDifficulty();
            this.aiDepth = data.getAiDepth();
            this.debugMode = data.isDebugMode();
            this.logic = new GameLogic(state.getBoard(), consolePanel);
            this.currentMovable = Collections.emptyList();
            this.phase = Phase.WAITING_TO_ROLL;

            boardPanel.setBoard(state.getBoard());
            boardPanel.setHighlightedHouses(null);

            consolePanel.clear();
            consolePanel.logSystem("Loaded save '" + slotName + "' (saved on " + data.getSavedAt() + ").");

            controlPanel.setGameInProgress(true);
            controlPanel.setPauseButtonText("Pause");
            controlPanel.setPauseEnabled(true);
            controlPanel.setRollEnabled(true);

            advanceIfComputerTurn();
            updateStatusLabels();
        } catch (IOException | ClassNotFoundException e) {
            consolePanel.logWarning("Could not load that save: " + e.getMessage());
        }
    }

    public void showRules() {
        RulesDialog.show(ownerFrame);
    }

    private void onGameOver(Player winner) {
        boardPanel.setHighlightedHouses(null);
        boardPanel.repaint();
        consolePanel.logWin(winner.getName() + " has brought all seven pieces home and WINS!");

        phase = Phase.GAME_OVER;
        controlPanel.setRollEnabled(false);
        controlPanel.setPauseEnabled(false);
        updateStatusLabels();

        JOptionPane.showMessageDialog(ownerFrame, winner.getName() + " wins the game!",
                "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

    // ------------------------------------------------------------------
    // Status display
    // ------------------------------------------------------------------

    private void updateStatusLabels() {
        if (state == null) {
            controlPanel.setGameInProgress(false);
            return;
        }

        Player current = state.getCurrentPlayer();
        String color = current.getColor() == 'B' ? "Blue" : "White";

        String phaseNote;
        switch (phase) {
            case WAITING_TO_ROLL:
                phaseNote = "Ready to roll.";
                break;
            case WAITING_FOR_SELECTION:
                phaseNote = "Roll: " + currentRoll + " - choose a highlighted piece.";
                break;
            case COMPUTER_TURN:
                phaseNote = "Thinking...";
                break;
            case PAUSED:
                phaseNote = "Paused.";
                break;
            case GAME_OVER:
                phaseNote = "Game over.";
                break;
            default:
                phaseNote = "";
        }

        controlPanel.setTurnText("Turn: " + current.getName() + " (" + color + ")");
        controlPanel.setRollText(phaseNote);

        Player p1 = state.getPlayer1();
        Player p2 = state.getPlayer2();
        controlPanel.setCountsText(
                p1.getName() + " (Blue): " + p1.countPiecesOut() + "/" + Board.PIECES_PER_PLAYER + " home<br>" +
                p2.getName() + " (White): " + p2.countPiecesOut() + "/" + Board.PIECES_PER_PLAYER + " home");
        controlPanel.setDifficultyText("Difficulty: " + difficulty + "<br>Search depth: " + aiDepth);
    }

    private String describe(Piece p) {
        return p.getColor() + "-Piece" + p.getId() + " (House " + p.getPosition().toHouseNumber() + ")";
    }
}
