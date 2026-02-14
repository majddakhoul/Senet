package gameLogic;

import board.*;
import player.*;

public class State {

    private Board board;
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private Dice lastDice;
    private State previousState;

    public State(Board board,
                 Player player1,
                 Player player2,
                 Player currentPlayer,
                 Dice lastDice,
                 State previousState) {

        this.board = board;
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = currentPlayer;
        this.lastDice = lastDice;
        this.previousState = previousState;
    }

    public State copy() {

        Player newP1 = copyPlayer(player1);
        Player newP2 = copyPlayer(player2);

        Player newCurrent =
                (currentPlayer.getColor() == player1.getColor())
                        ? newP1
                        : newP2;

        Board newBoard =
                copyBoard(board, newP1, newP2);

        return new State(
                newBoard,
                newP1,
                newP2,
                newCurrent,
                lastDice,
                this
        );
    }

    private Player copyPlayer(Player p) {

        Player newPlayer =
                new Player(p.getName(), p.getColor());

        for (int i = 0; i < 7; i++) {

            Piece oldPiece =
                    p.getPieces().get(i);

            Piece newPiece =
                    newPlayer.getPieces().get(i);

            newPiece.setPosition(
                    oldPiece.getPosition() == null
                            ? null
                            : new Coordinate(
                            oldPiece.getPosition().getcX(),
                            oldPiece.getPosition().getcY()
                    )
            );

            newPiece.setOut(oldPiece.isOut());
            newPiece.setMustMoveNextTurn(
                    oldPiece.mustMoveNextTurn()
            );
            newPiece.setMovedThisTurn(
                    oldPiece.hasMovedThisTurn()
            );
        }

        return newPlayer;
    }

    private Board copyBoard(Board oldBoard,
                            Player newP1,
                            Player newP2) {

        House[] newHouses = new House[30];

        for (int i = 0; i < 30; i++) {

            House oldH =
                    oldBoard.getHouse(i + 1);

            Coordinate pos =
                    new Coordinate(
                            oldH.getPosition().getcX(),
                            oldH.getPosition().getcY()
                    );

            House h =
                    new House(
                            (byte) oldH.getNumber(),
                            oldH.getType(),
                            pos
                    );

            if (!oldH.isEmpty()) {

                Piece p =
                        oldH.getOccupiedBy();

                Piece newPiece;

                if (p.getColor() == newP1.getColor())
                    newPiece =
                            newP1.getPieceById(p.getId());
                else
                    newPiece =
                            newP2.getPieceById(p.getId());

                h.setOccupiedBy(newPiece);
            }

            newHouses[i] = h;
        }

        Board newBoard =
                new Board(newP1, newP2);

        newBoard.setHouses(newHouses);

        return newBoard;
    }

    public Board getBoard() {
        return board;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Dice getLastDice() {
        return lastDice;
    }

    public void switchTurn() {

        currentPlayer =
                (currentPlayer.getColor() == player1.getColor())
                        ? player2
                        : player1;
    }
}
