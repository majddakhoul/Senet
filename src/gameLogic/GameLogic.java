package gameLogic;

import board.*;
import player.*;

import java.util.ArrayList;
import java.util.List;

public class GameLogic {

    private Board board;

    public GameLogic(Board board) {
        this.board = board;
    }

    private void exitBoard(Piece piece, int startHouse) {

        board.getHouse(startHouse).setOccupiedBy(null);
        piece.setOut(true);
        piece.setPosition(null);

        System.out.println(
                piece.getColor() + " Piece " +
                        piece.getId() + " has exited!"
        );
    }

    public boolean canMovePiece(Piece piece, byte steps) {

        if (piece.isOut() || piece.getPosition() == null)
            return false;

        int start = getHouseNumberFromCoords(piece.getPosition());
        int target = start + steps;

        if (start < 26 && target > 26)
            return false;

        switch (start) {
            case 28:
                return steps == 3;
            case 29:
                return steps == 2;
            case 30:
                return true;
        }

        if (target <= 30) {

            House h = board.getHouse(target);

            if (!h.isEmpty() &&
                    h.getOccupiedBy().getColor() == piece.getColor()) {

                return false;
            }
        }

        return true;
    }

    public List<Piece> getMovablePieces(Player player, byte steps) {

        List<Piece> movable = new ArrayList<>();

        for (Piece p : player.getPieces()) {

            if (canMovePiece(p, steps))
                movable.add(p);
        }

        return movable;
    }

    public void movePiece(Piece piece, byte steps) {

        if (piece.getPosition() == null || piece.isOut())
            return;

        int start = getHouseNumberFromCoords(piece.getPosition());
        int target = start + steps;

        if (target == 27) {
            returnToRebirthArea(piece, target);
            return;
        }

        if (start < 26 && target > 26)
            return;

        if (start == 30) {
            exitBoard(piece, start);
            return;
        }

        House from = board.getHouse(start);
        House to;

        if (target > 30) {
            exitBoard(piece, start);
            return;
        } else {
            to = board.getHouse(target);
        }

        if (!to.isEmpty() &&
                to.getOccupiedBy().getColor() != piece.getColor()) {

            Piece opponent = to.getOccupiedBy();

            from.setOccupiedBy(opponent);
            opponent.setPosition(from.getPosition());

        } else {

            from.setOccupiedBy(null);
        }

        to.setOccupiedBy(piece);
        piece.setPosition(to.getPosition());

        if (to.getType().equals("WATER")) {
            returnToRebirthArea(piece, target);
        }
    }

    public void returnToRebirthArea(Piece piece, int currentHouseIdx) {

        if (piece.getPosition() != null) {

            int oldHouseNumber =
                    getHouseNumberFromCoords(piece.getPosition());

            House oldHouse =
                    board.getHouse(oldHouseNumber);

            if (oldHouse.getOccupiedBy() == piece)
                oldHouse.setOccupiedBy(null);
        }

        int targetHome = 15;

        while (targetHome >= 1) {

            House check = board.getHouse(targetHome);

            if (check.isEmpty()) {

                check.setOccupiedBy(piece);
                piece.setPosition(check.getPosition());
                piece.setOut(false);

                System.out.println(
                        "Relocated " +
                                piece.getColor() +
                                "Piece " +
                                piece.getId() +
                                " to House " +
                                targetHome
                );

                return;
            }

            targetHome--;
        }
    }

    public int getHouseNumberFromCoords(Coordinate pos) {

        byte x = pos.getcX();
        byte y = pos.getcY();

        if (y == 0)
            return x + 1;

        if (y == 1)
            return 20 - x;

        return 21 + x;
    }
}
