package gameLogic;

import board.Board;
import board.Dice;
import board.House;
import board.HouseType;
import player.Piece;
import player.Player;

import java.util.List;

/**
 * Chooses moves for the computer player using Expectiminimax: a variant of
 * minimax that adds a "chance" layer for the random dice roll between each
 * pair of MAX/MIN turns, weighting each outcome by its true probability
 * (see {@link Dice#getProbability(byte)}).
 */
public class GameLogicForAI {

    private final char aiColor;
    private final boolean debug;
    private int visitedNodes;

    public GameLogicForAI(char aiColor, boolean debug) {
        this.aiColor = aiColor;
        this.debug = debug;
        this.visitedNodes = 0;
    }

    public GameLogicForAI(char aiColor) {
        this(aiColor, false);
    }

    /**
     * Static evaluation of a state from the AI's point of view: higher is
     * better for {@link #aiColor}. Rewards pieces that have exited,
     * progress toward the exit, and landing on favourable special houses;
     * penalises the opponent's equivalent progress.
     */
    public int heuristic(State state) {
        int score = 0;
        Board board = state.getBoard();

        for (House h : board.getHouses()) {
            if (h.isEmpty()) {
                continue;
            }

            Piece p = h.getOccupiedBy();

            if (p.isOut()) {
                score += (p.getColor() == aiColor) ? 10 : -10;
                continue;
            }

            int houseNum = p.getPosition().toHouseNumber();
            int distanceToExit = Board.SIZE - houseNum;
            boolean isAiPiece = (p.getColor() == aiColor);
            int sign = isAiPiece ? 1 : -1;

            int pieceScore = isAiPiece ? 5 : -5;
            pieceScore += isAiPiece ? (5 - distanceToExit / 6) : (distanceToExit / 6);
            pieceScore += sign * specialHouseBonus(h.getType());

            int targetHouse = houseNum + 1;

            if (targetHouse <= Board.SIZE) {
                House next = board.getHouse(targetHouse);

                if (!next.isEmpty() && next.getOccupiedBy().getColor() != p.getColor()) {
                    pieceScore += sign * 2;
                }
            }

            score += pieceScore;
        }

        return score;
    }

    private int specialHouseBonus(HouseType type) {
        switch (type) {
            case HAPPINESS:
                return 3;
            case REBIRTH:
                return 2;
            case HORUS:
                return 4;
            case WATER:
                return -2;
            case THREE_TRUTHS:
                return 2;
            case RE_ATOUM:
                return 2;
            default:
                return 0;
        }
    }

    private double maxValue(State state, int depth) {
        visitedNodes++;
        log("MAX node", depth);

        if (depth == 0 || state.getCurrentPlayer().hasWon()) {
            int h = heuristic(state);
            log("Heuristic = " + h, depth);
            return h;
        }

        double value = chanceValue(state, depth, true);
        log("MAX returns " + value, depth);
        return value;
    }

    private double minValue(State state, int depth) {
        visitedNodes++;
        log("MIN node", depth);

        if (depth == 0 || state.getCurrentPlayer().hasWon()) {
            int h = heuristic(state);
            log("Heuristic = " + h, depth);
            return h;
        }

        double value = chanceValue(state, depth, false);
        log("MIN returns " + value, depth);
        return value;
    }

    private double chanceValue(State state, int depth, boolean isMaxTurn) {
        visitedNodes++;
        log("CHANCE node", depth);

        double expectedValue = 0;
        Player current = state.getCurrentPlayer();

        for (byte dice = 1; dice <= 5; dice++) {
            double prob = Dice.getProbability(dice);
            List<Move> moves = GameEngine.getLegalMoves(state, current, dice);

            if (moves.isEmpty()) {
                continue;
            }

            double best = isMaxTurn ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;

            for (Move m : moves) {
                State next = GameEngine.applyMove(state, m);
                double val = isMaxTurn ? minValue(next, depth - 1) : maxValue(next, depth - 1);
                best = isMaxTurn ? Math.max(best, val) : Math.min(best, val);
            }

            log("Dice " + dice + " -> best = " + best + " (p=" + prob + ")", depth);
            expectedValue += prob * best;
        }

        log("CHANCE returns " + expectedValue, depth);
        return expectedValue;
    }

    /**
     * Runs Expectiminimax to {@code depth} half-moves and returns the best
     * move for {@code state.getCurrentPlayer()} given that they just rolled
     * {@code diceRoll}, or {@code null} if no legal move exists.
     */
    public Move getBestMove(State state, int depth, byte diceRoll) {
        visitedNodes = 0;

        Move bestMove = null;
        double bestValue = Double.NEGATIVE_INFINITY;

        List<Move> legalMoves = GameEngine.getLegalMoves(state, state.getCurrentPlayer(), diceRoll);

        for (Move m : legalMoves) {
            State next = GameEngine.applyMove(state, m);
            double val = minValue(next, depth - 1);

            if (val > bestValue) {
                bestValue = val;
                bestMove = m;
            }
        }

        if (debug) {
            System.out.println("=================================");
            System.out.println("AI selected move: " + bestMove);
            System.out.println("Evaluation value: " + bestValue);
            System.out.println("Visited nodes: " + visitedNodes);
            System.out.println("=================================");
        }

        return bestMove;
    }

    public int getVisitedNodes() {
        return visitedNodes;
    }

    private void log(String msg, int depth) {
        if (!debug) {
            return;
        }
        System.out.println("  ".repeat(Math.max(depth, 0)) + msg);
    }
}
