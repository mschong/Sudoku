package edu.utep.cs.cs4330.sudoku;

import edu.utep.cs.cs4330.sudoku.model.Board;

/**
 * Created by mscho on 3/15/2018.
 */

public abstract class SudokuSolverAbstraction {
    Board board;
    public SudokuSolverAbstraction(Board board){
        this.board = board;
    }

    public abstract void solve();
}
