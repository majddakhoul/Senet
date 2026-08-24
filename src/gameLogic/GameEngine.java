package gameLogic;

import common.GameLog;
import player.Piece;
import player.Player;

import java.util.ArrayList;
import java.util.List;

public final class GameEngine {

    private GameEngine() {
    }

    public static State applyMove(State currentState, Move move) {
        State newState = currentState.copy();
        GameLogic logic = new GameLogic(newState.getBoard(), GameLog.SILENT);

        Player currentPlayer = newState.getCurrentPlayer();
        Piece piece = currentPlayer.getPieceById(move.getPieceId());

        if (piece == null || piece.isOut()) {
            return newState;
        }

        logic.movePiece(piece, move.getSteps());
        logic.applyForcedHouseRule(currentPlayer, piece);
        newState.switchTurn();

        return newState;
    }

    public static List<Move> getLegalMoves(State state, Player player, byte diceRoll) {
        GameLogic logic = new GameLogic(state.getBoard(), GameLog.SILENT);
        List<Move> moves = new ArrayList<>();

        for (Piece p : player.getPieces()) {
            if (p.isOut() || p.getPosition() == null) {
                continue;
            }

            if (logic.canMovePiece(p, diceRoll)) {
                moves.add(new Move(p.getId(), diceRoll));
            }
        }

        return moves;
    }
}
