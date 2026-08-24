package player;

import board.Board;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Player implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;
    private final char color;
    private final PlayerType type;
    private final List<Piece> pieces;

    public Player(String name, char color, PlayerType type) {
        this.name = name;
        this.color = color;
        this.type = type;
        this.pieces = new ArrayList<>();

        for (int i = 0; i < Board.PIECES_PER_PLAYER; i++) {
            pieces.add(new Piece(i, color, null));
        }
    }

    public String getName() {
        return name;
    }

    public char getColor() {
        return color;
    }

    public PlayerType getType() {
        return type;
    }

    public boolean isComputer() {
        return type == PlayerType.COMPUTER;
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    public Piece getPieceById(int id) {
        for (Piece p : pieces) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public long countPiecesOut() {
        return pieces.stream().filter(Piece::isOut).count();
    }

    public boolean hasWon() {
        for (Piece p : pieces) {
            if (!p.isOut()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Player: ").append(name)
                .append(" (").append(color).append(", ").append(type).append(")\n")
                .append("Pieces:\n");

        for (Piece p : pieces) {
            sb.append("  ").append(p).append("\n");
        }
        return sb.toString();
    }
}
