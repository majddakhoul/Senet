package gameLogic;

import board.Board;
import board.House;
import board.HouseType;
import common.GameLog;
import player.Piece;
import player.Player;

import java.util.ArrayList;
import java.util.List;

public class GameLogic {

    private final Board board;
    private final GameLog log;

    public GameLogic(Board board, GameLog log) {
        this.board = board;
        this.log = log;
    }

    private void narrate(String message) {
        log.log(message);
    }

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

    public List<Piece> getMovablePieces(Player player, byte steps) {
        List<Piece> movable = new ArrayList<>();

        for (Piece p : player.getPieces()) {
            if (canMovePiece(p, steps)) {
                movable.add(p);
            }
        }

        return movable;
    }

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
