package gameLogic;

import java.io.Serializable;

public class Move implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int pieceId;
    private final byte steps;

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
        return "Move(Piece ID: " + pieceId + ", Steps: " + steps + ")";
    }
}
