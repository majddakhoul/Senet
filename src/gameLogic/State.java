package gameLogic;

import board.Board;
import board.Coordinate;
import board.Dice;
import board.House;
import player.Piece;
import player.Player;

import java.io.Serializable;

/**
 * A complete, self-contained snapshot of a Senet game: the board, both
 * players, whose turn it is, and the last dice roll. States are copied
 * (never mutated in place) whenever the AI explores a hypothetical future
 * move, so the real game state is never disturbed by search.
 */
public class State implements Serializable {

    private static final long serialVersionUID = 1L;

    private Board board;
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private Dice lastDice;

    public State(Board board, Player player1, Player player2, Player currentPlayer, Dice lastDice) {
        this.board = board;
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = currentPlayer;
        this.lastDice = lastDice;
    }

    /**
     * Produces a deep, independent copy of this state: new {@link Player},
     * {@link Piece} and {@link Board}/{@link House} objects that share no
     * mutable state with the original. Safe to mutate freely, e.g. while
     * exploring a move in the AI search tree.
     */
    public State copy() {
        Player newP1 = copyPlayer(player1);
        Player newP2 = copyPlayer(player2);

        Player newCurrent = (currentPlayer.getColor() == player1.getColor()) ? newP1 : newP2;

        Board newBoard = copyBoard(board, newP1, newP2);

        return new State(newBoard, newP1, newP2, newCurrent, lastDice);
    }

    private Player copyPlayer(Player p) {
        Player newPlayer = new Player(p.getName(), p.getColor(), p.getType());

        for (int i = 0; i < Board.PIECES_PER_PLAYER; i++) {
            Piece oldPiece = p.getPieces().get(i);
            Piece newPiece = newPlayer.getPieces().get(i);

            Coordinate oldPos = oldPiece.getPosition();
            newPiece.setPosition(oldPos == null ? null : oldPos.copy());
            newPiece.setOut(oldPiece.isOut());
        }

        return newPlayer;
    }

    /**
     * Rebuilds the house array of {@code oldBoard} so that every house
     * points at the freshly-copied {@link Piece} instances owned by
     * {@code newP1}/{@code newP2}, without re-running the board's initial
     * start-of-game layout (which would overwrite the positions we just
     * copied).
     */
    private Board copyBoard(Board oldBoard, Player newP1, Player newP2) {
        House[] newHouses = new House[Board.SIZE];

        for (int i = 0; i < Board.SIZE; i++) {
            House oldH = oldBoard.getHouse(i + 1);
            House h = new House(oldH.getNumber(), oldH.getType(), oldH.getPosition().copy());

            if (!oldH.isEmpty()) {
                Piece p = oldH.getOccupiedBy();
                Piece newPiece = (p.getColor() == newP1.getColor())
                        ? newP1.getPieceById(p.getId())
                        : newP2.getPieceById(p.getId());
                h.setOccupiedBy(newPiece);
            }

            newHouses[i] = h;
        }

        return new Board(newHouses);
    }

    public Board getBoard() {
        return board;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Dice getLastDice() {
        return lastDice;
    }

    public void switchTurn() {
        currentPlayer = (currentPlayer.getColor() == player1.getColor()) ? player2 : player1;
    }
}
