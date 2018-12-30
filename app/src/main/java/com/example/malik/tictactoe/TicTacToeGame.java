package com.example.malik.tictactoe;

public class TicTacToeGame {

    private final int NUM_ROWS;
    private final int MAX_TURNS;
    private boolean gameInProgress;
    private boolean winFound = false;
    private String[][] ticTacToeBoard;
    private int totalTurnsTaken = 0;

    public TicTacToeGame (int rows) {
        NUM_ROWS = rows;
        MAX_TURNS = rows*rows;
        ticTacToeBoard = new String[NUM_ROWS][NUM_ROWS];
    }

    public void isGameOn(boolean status) {
        gameInProgress = status;
        if (gameInProgress == false) {
            resetGame();
        }
    }

    public void updateBoard(int i, int j, String mark) {
        ticTacToeBoard[i][j] = mark;
        ++totalTurnsTaken;
        findWin();
    }

    public boolean spotTaken(int i, int j) {
        return ticTacToeBoard[i][j].equals("");
    }

    public boolean gameEndWon() {
        return winFound;
    }

    public boolean gameEndDraw() {
        return (totalTurnsTaken == 9);
    }

    public void resetGame() {
        for (int i = 0; i < NUM_ROWS; ++i) {
            for (int j = 0; j < NUM_ROWS; ++j) {
                ticTacToeBoard[i][j] = "";
            }
        }
    }

    public boolean findWin(){ // put this method in thread file
        if (gameInProgress) { // check for 3 X's or 3 O's in a row
            for (int i = 0; i < NUM_ROWS; ++i) {
                for (int j = 1; j < NUM_ROWS; ++j){
                    if (!ticTacToeBoard[i][0].equals(ticTacToeBoard[i][j])){
                        winFound = false;
                        break;
                    }
                    else {
                        winFound = true;
                    }
                }
                if (winFound){
                    return true;
                }
            }

            for (int i = 1; i < NUM_ROWS; ++i) { // check for 3 X's or 3 O's in a column
                for (int j = 0; j < NUM_ROWS; ++j){
                    if (!ticTacToeBoard[j][0].equals(ticTacToeBoard[j][i])){
                        winFound = false;
                        break;
                    }
                    else {
                        winFound = true;
                    }
                }
                if (winFound){
                    return true;
                }
            }

            for (int i = 0; i < NUM_ROWS-1; ++i){ // check for 3 X's or 3 O's along one diagonal
                if (!ticTacToeBoard[i][i].equals(ticTacToeBoard[i+1][i+1])){
                    winFound = false;
                    break;
                }
                else {
                    winFound = true;
                }
            }
            for (int i = NUM_ROWS-1; i > 1; --i){ // check for 3 X's or 3 O's along the other diagonal
                if (!ticTacToeBoard[i][i].equals(ticTacToeBoard[i-1][i-1])){
                    winFound = false;
                    break;
                }
                else {
                    winFound = true;
                }
            }
            //if win is found, send winner message on Toast and call setAllButtons("_")
        }
        return winFound;
    }

}
