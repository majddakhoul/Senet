package gameLogic;

import common.GameLog;
import player.Piece;
import player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Stateless helpers for exploring hypothetical futures: given a
 * {@link State}, produce a new state after a move, or list the moves
 * available to a player for a given dice roll. Used exclusively by the
 * Expectiminimax search in {@link GameLogicForAI}, so move application here
 * always uses {@link GameLog#SILENT}, since these are moves the AI is
 * merely considering, not moves that actually happened.
 */
public final class GameEngine {

    private GameEngine() {
    }

    /**
     * Returns a new {@link State}, independent of {@code currentState},
     * with {@code move} applied and the turn advanced.
     */
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

    /**
     * Lists every legal move available to {@code player} for the given
     * dice roll, in {@code state}.
     */
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
