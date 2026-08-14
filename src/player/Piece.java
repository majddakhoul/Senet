package player;

import board.Coordinate;

import java.io.Serializable;

/**
 * A single playing piece ("pawn"). Each player owns seven of these.
 * A piece is either on the board at a given {@link Coordinate}, or has
 * already exited ({@link #isOut()}), in which case its position is
 * {@code null}.
 */
public class Piece implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int id;
    private final char color;
    private Coordinate position;
    private boolean isOut;

    public Piece(int id, char color, Coordinate position) {
        this.id = id;
        this.color = color;
        this.position = position;
        this.isOut = false;
    }

    public int getId() {
        return id;
    }

    public char getColor() {
        return color;
    }

    public Coordinate getPosition() {
        return position;
    }

    public void setPosition(Coordinate position) {
        this.position = position;
    }

    public boolean isOut() {
        return isOut;
    }

    public void setOut(boolean out) {
        this.isOut = out;
    }

    @Override
    public String toString() {
        if (isOut) {
            return color + "-Piece" + id + " (OUT)";
        }
        if (position == null) {
            return color + "-Piece" + id + " (unplaced)";
        }
        return color + "-Piece" + id + " @ House " + position.toHouseNumber();
    }
}
