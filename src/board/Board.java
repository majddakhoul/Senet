package board;

import player.Piece;
import player.Player;

import java.io.Serializable;
import java.util.List;

/**
 * The 30-house Senet board. Owns house layout/typing and renders itself to
 * the console.
 */
public class Board implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final int SIZE = 30;
    public static final int PIECES_PER_PLAYER = 7;
    public static final int INITIAL_ROWS_FILLED = 14;

    private House[] houses;

    public Board(Player player1, Player player2) {
        houses = new House[SIZE];
        initBoard();
        initPieces(player1, player2);
    }

    /**
     * Reconstructs a board directly from an already-built array of houses,
     * without running the standard start-of-game layout. Used when copying
     * or restoring a game state, so that piece positions already computed
     * elsewhere are not overwritten by the initial layout.
     */
    public Board(House[] houses) {
        this.houses = houses;
    }

    public House[] getHouses() {
        return houses;
    }

    public void setHouses(House[] houses) {
        this.houses = houses;
    }

    private void initBoard() {
        for (int i = 0; i < SIZE; i++) {
            int houseNumber = i + 1;
            houses[i] = new House(
                    (byte) houseNumber,
                    HouseType.forHouseNumber(houseNumber),
                    Coordinate.fromHouseNumber(houseNumber)
            );
        }
    }

    private void initPieces(Player p1, Player p2) {
        List<Piece> p1Pieces = p1.getPieces();
        List<Piece> p2Pieces = p2.getPieces();

        for (int i = 0; i < INITIAL_ROWS_FILLED; i++) {
            House h = houses[i];

            if (i % 2 == 0) {
                Piece piece = p1Pieces.get(i / 2);
                h.setOccupiedBy(piece);
                piece.setPosition(h.getPosition());
            } else {
                Piece piece = p2Pieces.get(i / 2);
                h.setOccupiedBy(piece);
                piece.setPosition(h.getPosition());
            }
        }
    }

    /**
     * Returns the house with the given 1-based number, or {@code null} if
     * the number is out of range.
     */
    public House getHouse(int number) {
        if (number < 1 || number > SIZE) {
            return null;
        }
        return houses[number - 1];
    }

    private String getSymbol(House h) {
        if (!h.isEmpty()) {
            return "" + h.getOccupiedBy().getColor() + h.getOccupiedBy().getId();
        }
        return String.valueOf(h.getType().getSymbol());
    }

    /**
     * Prints an ASCII rendering of the board's S-shaped path to the console,
     * one row for each of the three rows of ten houses.
     */
    public void printBoard() {
        System.out.println();
        System.out.println("================= Board (S-path, houses 1-30) =================");

        for (int row = 0; row < 3; row++) {
            StringBuilder line = new StringBuilder();

            for (int col = 0; col < 10; col++) {
                int index = (row == 1) ? (10 + (9 - col)) : ((row * 10) + col);
                line.append(String.format("%4s", getSymbol(houses[index])));
            }

            System.out.println(line);
        }

        System.out.println("Legend: <Color><PieceId> = occupied | - normal | R rebirth | H happiness");
        System.out.println("        W water | T three truths | A re-atoum | O horus");
        System.out.println("=================================================================");
    }
}
