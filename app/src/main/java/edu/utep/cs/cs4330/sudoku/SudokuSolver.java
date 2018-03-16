package edu.utep.cs.cs4330.sudoku;

import edu.utep.cs.cs4330.sudoku.model.Board;

/**
 * Created by mscho on 3/15/2018.
 */

public class SudokuSolver extends SudokuSolverAbstraction {

    public SudokuSolver(Board board) {
        super(board);
    }

    @Override
    public void solve() {
        for(int i = 0; i < board.size; i++){
            for(int j = 0; j < board.size; j++){
                board.getSquare(i,j).insertUserValue(board.getSquare(i,j).getValue());
                board.getSquare(i,j).setDraw(true);
            }
        }
    }
}
