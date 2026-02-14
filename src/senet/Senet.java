package senet;

import board.*;
import gameLogic.*;
import player.*;

import java.util.List;
import java.util.Scanner;

public class Senet {

    private Board board;
    private Player player1;
    private Player player2;
    private Dice dice;
    private Scanner scanner;
    private GameLogic gameLogic;
    private int aiDepth;
    private boolean debugMode;

    public Senet() {
        scanner = new Scanner(System.in);

        System.out.print("Enter AI search depth: ");
        aiDepth = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enable debug mode? (y/n): ");
        String debugInput = scanner.nextLine();
        debugMode = debugInput.equalsIgnoreCase("y");

        System.out.println("\nPress Enter to start the game...");
        scanner.nextLine();

        player1 = new Player("AbdMK7", 'B');
        player2 = new Player("ComputerAi", 'W');
        board = new Board(player1, player2);
        dice = new Dice();
        gameLogic = new GameLogic(board);
    }

    public void play() {
        Player current = player1;

        while (true) {
            board.printBoard();

            long player1Out = player1.getPieces().stream().filter(Piece::isOut).count();
            long player2Out = player2.getPieces().stream().filter(Piece::isOut).count();

            System.out.println(player1.getName() + " has " + player1Out + " pieces out of 7.");
            System.out.println(player2.getName() + " has " + player2Out + " pieces out of 7.");

            Piece forcedPiece = null;
            for (Piece p : current.getPieces()) {
                if (p.isOut() || p.getPosition() == null) continue;
                int house = gameLogic.getHouseNumberFromCoords(p.getPosition());
                if (house == 28 || house == 29 || house == 30) {
                    forcedPiece = p;
                    break;
                }
            }

            if (current == player1) {

                byte steps = dice.roll();
                System.out.println("\n" + current.getName() + " rolled: " + steps);

                List<Piece> movable = gameLogic.getMovablePieces(current, steps);
                if (movable.isEmpty()) {
                    System.out.println("No movable pieces available. Turn skipped.");
                    current = player2;
                    continue;
                }

                System.out.println("Choose piece to move:");
                for (Piece p : movable) {
                    System.out.println("ID: " + p.getId() + " -> " + p);
                }

                int choice = scanner.nextInt();
                scanner.nextLine();
                Piece selected = current.getPieceById(choice);

                if (selected != null && movable.contains(selected)) {
                    gameLogic.movePiece(selected, steps);

                    if (forcedPiece != null && forcedPiece != selected && !forcedPiece.isOut()) {
                        int forcedHouse = gameLogic.getHouseNumberFromCoords(forcedPiece.getPosition());
                        if (forcedHouse == 28 || forcedHouse == 29 || forcedHouse == 30) {
                            gameLogic.returnToRebirthArea(forcedPiece, forcedHouse);
                        }
                    }
                } else {
                    System.out.println("Invalid choice, turn skipped.");
                }

            } else {

                System.out.println("\nComputer's turn...");
                byte steps = dice.roll();
                System.out.println("Computer rolled: " + steps);

                List<Piece> movable = gameLogic.getMovablePieces(current, steps);
                if (movable.isEmpty()) {
                    System.out.println("Computer has no movable pieces. Turn skipped.");
                    current = player1;
                    continue;
                }

                State aiState = new State(board, current, player1, player2, dice, null);
                GameLogicForAI aiLogic = new GameLogicForAI(current.getColor(), debugMode);

                Move bestMove = aiLogic.getBestMove(aiState, aiDepth, steps);

                if (bestMove != null) {
                    Piece selected = current.getPieceById(bestMove.getPieceId());
                    gameLogic.movePiece(selected, bestMove.getSteps());

                    System.out.println("Computer moved piece ID " +
                            selected.getId() + " for " + bestMove.getSteps() + " steps.");

                    if (forcedPiece != null && forcedPiece != selected && !forcedPiece.isOut()) {
                        int forcedHouse = gameLogic.getHouseNumberFromCoords(forcedPiece.getPosition());
                        if (forcedHouse == 28 || forcedHouse == 29 || forcedHouse == 30) {
                            gameLogic.returnToRebirthArea(forcedPiece, forcedHouse);
                        }
                    }
                }
            }

            if (current.hasWon()) {
                System.out.println(current.getName() + " has won!");
                board.printBoard();
                break;
            }

            current = (current == player1) ? player2 : player1;
        }
    }

    public static void main(String[] args) {
        new Senet().play();
    }
}
