package gameLogic;

public class Move {

    private int pieceId;
    private byte steps;

    public Move(int pieceId, byte steps) {
        this.pieceId = pieceId;
        this.steps = steps;
    }

    public int getPieceId() {
        return pieceId;
    }

    public byte getSteps() {
        return steps;
    }

    @Override
    public String toString() {
        return "Move(Piece ID: " +
                pieceId +
                ", Steps: " +
                steps +
                ")";
    }
}
