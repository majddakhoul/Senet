package gameLogic;

import player.*;

import java.util.ArrayList;
import java.util.List;

public class GameEngine {

    public static State applyMove(State currentState, Move move) {

        State newState = currentState.copy();
        GameLogic logic = new GameLogic(newState.getBoard());

        Player currentPlayer = newState.getCurrentPlayer();
        Piece piece = currentPlayer.getPieceById(move.getPieceId());

        if (piece == null || piece.isOut())
            return newState;

        logic.movePiece(piece, move.getSteps());

        newState.switchTurn();

        return newState;
    }

    public static List<Move> getLegalMoves(State state, Player player, byte diceRoll) {

        GameLogic logic = new GameLogic(state.getBoard());
        List<Move> moves = new ArrayList<>();

        for (Piece p : player.getPieces()) {

            if (p.isOut() || p.getPosition() == null)
                continue;

            if (logic.canMovePiece(p, diceRoll)) {
                moves.add(new Move(p.getId(), diceRoll));
            }
        }

        return moves;
    }
}
