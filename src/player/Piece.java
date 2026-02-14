package player;

import board.Coordinate;

public class Piece {

    private int id;
    private char color;
    private Coordinate position;
    private boolean isOut;
    private boolean failedExitSpecialHouse;
    private boolean isWaitingSpecialExit = false;
    private boolean mustMoveNextTurn = false;
    private boolean movedThisTurn = false;
    private boolean pendingForcedExit;

    public Piece(int id, char color, Coordinate position) {
        this.id = id;
        this.color = color;
        this.position = position;
        this.isOut = false;
    }

    public Piece() { }

    public boolean isWaitingSpecialExit() {
        return isWaitingSpecialExit;
    }

    public void setWaitingSpecialExit(boolean val) {
        this.isWaitingSpecialExit = val;
    }

    public boolean isPendingForcedExit() {
        return pendingForcedExit;
    }

    public void setPendingForcedExit(boolean value) {
        this.pendingForcedExit = value;
    }

    public void resetTurnFlags() {
        this.movedThisTurn = false;
        this.failedExitSpecialHouse = false;
        this.pendingForcedExit = false;
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

    public boolean isOut() {
        return isOut;
    }

    public void setPosition(Coordinate position) {
        this.position = position;
    }

    public void setOut(boolean out) {
        this.isOut = out;
    }

    public boolean mustMoveNextTurn() {
        return mustMoveNextTurn;
    }

    public void setMustMoveNextTurn(boolean val) {
        this.mustMoveNextTurn = val;
    }

    public boolean hasMovedThisTurn() {
        return movedThisTurn;
    }

    public void setMovedThisTurn(boolean val) {
        this.movedThisTurn = val;
    }

    public boolean hasFailedExitSpecialHouse() {
        return failedExitSpecialHouse;
    }

    public void setFailedExitSpecialHouse(boolean failedExitSpecialHouse) {
        this.failedExitSpecialHouse = failedExitSpecialHouse;
    }

    @Override
    public String toString() {
        if (isOut) {
            return color + "-Piece" + id + " (OUT)";
        }
        return color + "-Piece" + id + " @ " + position;
    }
}
