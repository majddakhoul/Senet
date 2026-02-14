package board;

import player.*;

import java.util.List;

public class Board {

    private House[] houses;

    public Board(Player player1, Player player2) {
        houses = new House[30];
        initBoard();
        initPieces(player1, player2);
    }

    public House[] getHouses() {
        return houses;
    }

    public void setHouses(House[] houses) {
        this.houses = houses;
    }

    private void initBoard() {
        for (byte i = 0; i < 30; i++) {

            String type = "NORMAL";
            int houseNum = i + 1;

            switch (houseNum) {
                case 15: type = "REBIRTH"; break;
                case 26: type = "HAPPINESS"; break;
                case 27: type = "WATER"; break;
                case 28: type = "THREE_TRUTHS"; break;
                case 29: type = "RE_ATOUM"; break;
                case 30: type = "HORUS"; break;
            }

            byte y = (byte) (i / 10);
            byte x;

            if (y == 1) {
                x = (byte) (9 - (i % 10));
            } else {
                x = (byte) (i % 10);
            }

            houses[i] = new House(
                    (byte) houseNum,
                    type,
                    new Coordinate(x, y)
            );
        }
    }

    private void initPieces(Player p1, Player p2) {

        List<Piece> p1Pieces = p1.getPieces();
        List<Piece> p2Pieces = p2.getPieces();

        for (int i = 0; i < 14; i++) {

            House h = houses[i];

            if (i % 2 == 0) {
                h.setOccupiedBy(p1Pieces.get(i / 2));
                p1Pieces.get(i / 2).setPosition(h.getPosition());
            } else {
                h.setOccupiedBy(p2Pieces.get(i / 2));
                p2Pieces.get(i / 2).setPosition(h.getPosition());
            }
        }
    }

    public House getHouse(int number) {
        if (number < 1 || number > 30) return null;
        return houses[number - 1];
    }

    private String getSymbol(House h) {

        if (!h.isEmpty())
            return h.getOccupiedBy().getColor() + "*";

        switch (h.getType()) {
            case "NORMAL": return "-";
            case "REBIRTH": return "R";
            case "HAPPINESS": return "H";
            case "WATER": return "W";
            case "THREE_TRUTHS": return "T";
            case "RE_ATOUM": return "A";
            case "HORUS": return "O";
            default: return "?";
        }
    }

    public void printBoard() {

        System.out.println("=== Board Grid (S-Path) ===");

        for (int row = 0; row < 3; row++) {

            for (int col = 0; col < 10; col++) {

                int index;

                if (row == 1) {
                    index = 10 + (9 - col);
                } else {
                    index = (row * 10) + col;
                }

                System.out.printf("%3s ", getSymbol(houses[index]));
            }

            System.out.println();
        }
    }
}
