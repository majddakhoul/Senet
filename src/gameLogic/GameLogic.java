package gameLogic;

import board.Board;
import board.House;
import board.HouseType;
import common.GameLog;
import player.Piece;
import player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements Kendall's Rules for Senet: normal movement, captures
 * (position swaps), and the special behaviour of the five houses at the
 * end of the path (26-30).
 */
public class GameLogic {

    private final Board board;
    private final GameLog log;

    /**
     * Creates a rules engine bound to {@code board}. Every narrated event
     * (moves, captures, special-house effects) is sent to {@code log},
     * which the caller supplies. Pass {@link GameLog#SILENT} for
     * hypothetical states, such as the ones the AI explores during search,
     * so the on-screen console only ever shows what actually happened in
     * the real game.
     */
    public GameLogic(Board board, GameLog log) {
        this.board = board;
        this.log = log;
    }

    private void narrate(String message) {
        log.log(message);
    }

    /**
     * Whether the given piece may legally move the given number of steps.
     */
    public boolean canMovePiece(Piece piece, byte steps) {
        if (piece.isOut() || piece.getPosition() == null) {
            return false;
        }

        int start = piece.getPosition().toHouseNumber();
        int target = start + steps;

        if (start < 26 && target > 26) {
            return false;
        }

        switch (start) {
            case 28:
                return steps == 3;
            case 29:
                return steps == 2;
            case 30:
                return true;
        }

        if (target <= Board.SIZE) {
            House h = board.getHouse(target);

            if (!h.isEmpty() && h.getOccupiedBy().getColor() == piece.getColor()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns every piece of {@code player} that can legally move by
     * {@code steps} squares.
     */
    public List<Piece> getMovablePieces(Player player, byte steps) {
        List<Piece> movable = new ArrayList<>();

        for (Piece p : player.getPieces()) {
            if (canMovePiece(p, steps)) {
                movable.add(p);
            }
        }

        return movable;
    }

    /**
     * Applies a move to the board: relocates {@code piece} by {@code steps}
     * squares, resolving captures (position swaps) and the special effect
     * of the House of Water. Assumes the move has already been validated
     * with {@link #canMovePiece}.
     */
    public void movePiece(Piece piece, byte steps) {
        if (piece == null || piece.getPosition() == null || piece.isOut()) {
            return;
        }

        int start = piece.getPosition().toHouseNumber();
        int target = start + steps;

        if (start < 26 && target > 26) {
            return;
        }

        if (target > Board.SIZE) {
            exitBoard(piece, start);
            return;
        }

        House from = board.getHouse(start);
        House to = board.getHouse(target);

        if (!to.isEmpty() && to.getOccupiedBy().getColor() != piece.getColor()) {
            Piece opponent = to.getOccupiedBy();
            from.setOccupiedBy(opponent);
            opponent.setPosition(from.getPosition());

            narrate("  -> Capture! " + opponent.getColor() + "-Piece" + opponent.getId() +
                    " is bumped back to House " + from.getNumber() + ".");
        } else {
            from.setOccupiedBy(null);
        }

        to.setOccupiedBy(piece);
        piece.setPosition(to.getPosition());

        if (to.getType() == HouseType.WATER) {
            narrate("  -> " + piece.getColor() + "-Piece" + piece.getId() +
                    " fell into the House of Water and is swept back to the House of Rebirth.");
            returnToRebirthArea(piece);
        }
    }

    private void exitBoard(Piece piece, int startHouse) {
        board.getHouse(startHouse).setOccupiedBy(null);
        piece.setOut(true);
        piece.setPosition(null);

        narrate("  -> " + piece.getColor() + "-Piece" + piece.getId() + " has exited the board!");
    }

    /**
     * Sends {@code piece} back to the nearest unoccupied house at or before
     * the House of Rebirth (house 15), searching downward from 15 to 1.
     * With at most 14 pieces ever on the board at once and 15 candidate
     * houses, an empty house is always found.
     */
    public void returnToRebirthArea(Piece piece) {
        if (piece.getPosition() != null) {
            int oldHouseNumber = piece.getPosition().toHouseNumber();
            House oldHouse = board.getHouse(oldHouseNumber);

            if (oldHouse.getOccupiedBy() == piece) {
                oldHouse.setOccupiedBy(null);
            }
        }

        for (int targetHome = 15; targetHome >= 1; targetHome--) {
            House candidate = board.getHouse(targetHome);

            if (candidate.isEmpty()) {
                candidate.setOccupiedBy(piece);
                piece.setPosition(candidate.getPosition());
                piece.setOut(false);

                narrate("  -> " + piece.getColor() + "-Piece" + piece.getId() +
                        " is relocated to House " + targetHome + " (House of Rebirth).");
                return;
            }
        }
    }

    /**
     * Enforces the "one chance" rule of the last three houses before exit:
     * any piece of {@code player} still sitting on House 28 (Three Truths),
     * 29 (Re-Atoum) or 30 (Horus) that did not move on this turn is sent
     * back to the House of Rebirth, since it failed to use its required
     * roll in time. Call this once, after a player's move (or skipped
     * turn) has been finalised.
     */
    public void applyForcedHouseRule(Player player, Piece movedPiece) {
        for (Piece p : player.getPieces()) {
            if (p.isOut() || p.getPosition() == null || p == movedPiece) {
                continue;
            }

            int house = p.getPosition().toHouseNumber();

            if (house == 28 || house == 29 || house == 30) {
                narrate(p.getColor() + "-Piece" + p.getId() +
                        " missed its chance to leave House " + house + " and is sent back.");
                returnToRebirthArea(p);
            }
        }
    }
}
