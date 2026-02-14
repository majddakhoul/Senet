package gameLogic;

import board.*;
import player.*;

import java.util.List;

public class GameLogicForAI {

    private char aiColor;
    private boolean debug;
    private int visitedNodes;

    public GameLogicForAI(char aiColor, boolean debug) {
        this.aiColor = aiColor;
        this.debug = debug;
        this.visitedNodes = 0;
    }

    public GameLogicForAI(char aiColor) {
        this(aiColor, false);
    }

    public int heuristic(State state) {

        int score = 0;
        Board board = state.getBoard();

        for (House h : board.getHouses()) {

            if (!h.isEmpty()) {

                Piece p = h.getOccupiedBy();

                if (p.isOut()) {

                    score += (p.getColor() == aiColor) ? 10 : -10;

                } else {

                    int houseNum =
                            getHouseNumberFromCoords(p.getPosition());

                    int distanceToExit = 30 - houseNum;

                    int pieceScore =
                            (p.getColor() == aiColor ? 5 : -5);

                    pieceScore +=
                            (p.getColor() == aiColor
                                    ? 5 - distanceToExit / 6
                                    : distanceToExit / 6);

                    switch (h.getType()) {

                        case "HAPPINESS":
                            pieceScore +=
                                    (p.getColor() == aiColor ? 3 : -3);
                            break;

                        case "REBIRTH":
                            pieceScore +=
                                    (p.getColor() == aiColor ? 2 : -2);
                            break;

                        case "HORUS":
                            pieceScore +=
                                    (p.getColor() == aiColor ? 4 : -4);
                            break;

                        case "WATER":
                            pieceScore -=
                                    (p.getColor() == aiColor ? 2 : -2);
                            break;

                        case "THREE_TRUTHS":
                            pieceScore +=
                                    (p.getColor() == aiColor ? 2 : -2);
                            break;

                        case "RE_ATOUM":
                            pieceScore +=
                                    (p.getColor() == aiColor ? 2 : -2);
                            break;
                    }

                    int targetHouse = houseNum + 1;

                    if (targetHouse <= 30) {

                        House next =
                                board.getHouse(targetHouse);

                        if (!next.isEmpty() &&
                                next.getOccupiedBy().getColor() != p.getColor()) {

                            pieceScore +=
                                    (p.getColor() == aiColor ? 2 : -2);
                        }
                    }

                    score += pieceScore;
                }
            }
        }

        return score;
    }

    private int getHouseNumberFromCoords(Coordinate pos) {

        byte x = pos.getcX();
        byte y = pos.getcY();

        if (y == 0)
            return x + 1;

        if (y == 1)
            return 20 - x;

        return 21 + x;
    }

    private double maxValue(State state, int depth) {

        visitedNodes++;
        log("MAX node", depth);

        if (depth == 0 || state.getCurrentPlayer().hasWon()) {

            int h = heuristic(state);
            log("Heuristic = " + h, depth);
            return h;
        }

        double value = chanceValue(state, depth, true);

        log("MAX returns " + value, depth);
        return value;
    }

    private double minValue(State state, int depth) {

        visitedNodes++;
        log("MIN node", depth);

        if (depth == 0 || state.getCurrentPlayer().hasWon()) {

            int h = heuristic(state);
            log("Heuristic = " + h, depth);
            return h;
        }

        double value = chanceValue(state, depth, false);

        log("MIN returns " + value, depth);
        return value;
    }

    private double chanceValue(State state,
                               int depth,
                               boolean isMaxTurn) {

        visitedNodes++;
        log("CHANCE node", depth);

        double expectedValue = 0;
        Player current = state.getCurrentPlayer();

        for (byte dice = 1; dice <= 5; dice++) {

            double prob = Dice.getProbability(dice);

            List<Move> moves =
                    GameEngine.getLegalMoves(state, current, dice);

            if (moves.isEmpty())
                continue;

            double best =
                    isMaxTurn
                            ? Double.NEGATIVE_INFINITY
                            : Double.POSITIVE_INFINITY;

            for (Move m : moves) {

                State next =
                        GameEngine.applyMove(state, m);

                double val =
                        isMaxTurn
                                ? minValue(next, depth - 1)
                                : maxValue(next, depth - 1);

                if (isMaxTurn)
                    best = Math.max(best, val);
                else
                    best = Math.min(best, val);
            }

            log("Dice " + dice +
                            " → best = " + best +
                            " (p=" + prob + ")",
                    depth);

            expectedValue += prob * best;
        }

        log("CHANCE returns " + expectedValue, depth);
        return expectedValue;
    }

    public Move getBestMove(State state,
                            int depth,
                            byte diceRoll) {

        visitedNodes = 0;

        Move bestMove = null;
        double bestValue = Double.NEGATIVE_INFINITY;

        List<Move> legalMoves =
                GameEngine.getLegalMoves(
                        state,
                        state.getCurrentPlayer(),
                        diceRoll
                );

        for (Move m : legalMoves) {

            State next =
                    GameEngine.applyMove(state, m);

            double val =
                    minValue(next, depth - 1);

            if (val > bestValue) {

                bestValue = val;
                bestMove = m;
            }
        }

        if (debug) {

            System.out.println("=================================");
            System.out.println("AI selected move: " + bestMove);
            System.out.println("Evaluation value: " + bestValue);
            System.out.println("Visited nodes: " + visitedNodes);
            System.out.println("=================================");
        }

        return bestMove;
    }

    private void log(String msg, int depth) {

        if (!debug)
            return;

        System.out.println("  ".repeat(depth) + msg);
    }
}
