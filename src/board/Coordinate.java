package board;

public class Coordinate {

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

    public boolean equals(Coordinate other) {
        if (other == null)
            return false;

        return this.cX == other.cX && this.cY == other.cY;
    }

    public Coordinate copy() {
        return new Coordinate(this.cX, this.cY);
    }

    @Override
    public String toString() {
        return "(" + cX + "," + cY + ")";
    }
}
