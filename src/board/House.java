package board;

import player.Piece;

import java.io.Serializable;

public class House implements Serializable {

    private static final long serialVersionUID = 1L;

    private final byte number;
    private final HouseType type;
    private final Coordinate position;
    private Piece occupiedBy;

    public House(byte number, HouseType type, Coordinate position) {
        this.number = number;
        this.type = type;
        this.position = position;
        this.occupiedBy = null;
    }

    public byte getNumber() {
        return number;
    }

    public HouseType getType() {
        return type;
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
        String occupant = isEmpty()
                ? "Empty"
                : occupiedBy.getColor() + "-Piece" + occupiedBy.getId();

        return "House " + number +
                " [" + type.getDisplayName() + "] at " + position +
                " -> " + occupant;
    }
}
