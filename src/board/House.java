package board;

import player.Piece;

public class House {

    private byte number;
    private String type;
    private Coordinate position;
    private Piece occupiedBy;

    public House(byte number, String type, Coordinate position) {
        this.number = number;
        this.type = type;
        this.position = position;
        this.occupiedBy = null;
    }

    public byte getNumber() {
        return number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Coordinate getPosition() {
        return position;
    }

    public Piece getOccupiedBy() {
        return occupiedBy;
    }

    public void setOccupiedBy(Piece piece) {
        this.occupiedBy = piece;
    }

    public boolean isEmpty() {
        return occupiedBy == null;
    }

    @Override
    public String toString() {
        String occ = (occupiedBy == null)
                ? "Empty"
                : occupiedBy.getColor() + "-Piece" + occupiedBy.getId();

        return "House " + number +
                " [" + type + "] at " + position +
                " -> " + occ;
    }
}
