package player;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private String name;
    private char color;
    private List<Piece> pieces;

    public Player(String name, char color) {
        this.name = name;
        this.color = color;
        this.pieces = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            pieces.add(new Piece(i, color, null));
        }
    }

    public String getName() {
        return name;
    }

    public char getColor() {
        return color;
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    public Piece getPieceById(int id) {
        for (Piece p : pieces) {
            if (p.getId() == id)
                return p;
        }
        return null;
    }

    public boolean hasWon() {
        for (Piece p : pieces) {
            if (!p.isOut())
                return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Player: ").append(name)
                .append(" (").append(color).append(")\n")
                .append("Pieces:\n");

        for (Piece p : pieces) {
            sb.append("  ").append(p).append("\n");
        }
        return sb.toString();
    }
}
