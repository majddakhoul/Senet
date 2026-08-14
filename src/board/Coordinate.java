package board;

import java.io.Serializable;
import java.util.Objects;

/**
 * A position on the Senet board's S-shaped path, expressed as a grid
 * coordinate (column, row) rather than a raw house number.
 * <p>
 * The board is three rows of ten columns each. Row 0 runs left to right
 * (houses 1-10), row 1 runs right to left (houses 11-20), and row 2 runs
 * left to right again (houses 21-30), forming the classic boustrophedon
 * ("S") path.
 */
public final class Coordinate implements Serializable {

    private static final long serialVersionUID = 1L;

    private byte cX;
    private byte cY;

    public Coordinate() {
        this.cX = 0;
        this.cY = 0;
    }

    public Coordinate(byte cX, byte cY) {
        this.cX = cX;
        this.cY = cY;
    }

    /**
     * Builds the {@link Coordinate} for a 1-based house number (1-30) on
     * the S-shaped path.
     */
    public static Coordinate fromHouseNumber(int houseNumber) {
        if (houseNumber < 1 || houseNumber > 30) {
            throw new IllegalArgumentException("House number must be between 1 and 30, got " + houseNumber);
        }

        int index = houseNumber - 1;
        byte y = (byte) (index / 10);
        byte x;

        if (y == 1) {
            x = (byte) (9 - (index % 10));
        } else {
            x = (byte) (index % 10);
        }

        return new Coordinate(x, y);
    }

    /**
     * Converts this coordinate back into its 1-based house number (1-30).
     */
    public int toHouseNumber() {
        if (cY == 0) {
            return cX + 1;
        }
        if (cY == 1) {
            return 20 - cX;
        }
        return 21 + cX;
    }

    public byte getcX() {
        return cX;
    }

    public void setcX(byte cX) {
        this.cX = cX;
    }

    public byte getcY() {
        return cY;
    }

    public void setcY(byte cY) {
        this.cY = cY;
    }

    public Coordinate copy() {
        return new Coordinate(this.cX, this.cY);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Coordinate)) {
            return false;
        }
        Coordinate that = (Coordinate) other;
        return this.cX == that.cX && this.cY == that.cY;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cX, cY);
    }

    @Override
    public String toString() {
        return "(" + cX + "," + cY + ")";
    }
}
