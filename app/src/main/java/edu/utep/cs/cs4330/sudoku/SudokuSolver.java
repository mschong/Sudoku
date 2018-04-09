package edu.utep.cs.cs4330.sudoku;

import edu.utep.cs.cs4330.sudoku.model.Board;
import edu.utep.cs.cs4330.sudoku.model.Square;

/**
 * Created by mscho on 3/15/2018.
 */

public class SudokuSolver extends SudokuSolverAbstraction {

    public SudokuSolver(Board board) {
        super(board);
    }

    public Square getNextSquare(Square square) {

        int x = square.getXCoord();
        int y = square.getYCoord();
        y++;

        if(y == board.size) {
            y = 0;
            x++;
        }
        if(x  == board.size) {
            return null;
        }

        return board.getSquare(x, y);
    }

    public  boolean solve(Square square) {
        if(square == null) {
            return true;
        }
        if(square.getValue()!=0) {
            return solve(getNextSquare(square));
        }

        for(int i = 1 ; i <= board.size; i++) {
            boolean validNumber = board.isValidNumber(square.getXCoord(), square.getYCoord(), i);

            if(!validNumber) {
                continue;
            }

            square.setValue(i);
            boolean solved = solve(getNextSquare(square));
            if(solved) {
                return true;
            }
            else
                square.setValue(0);
        }
        return false;
    }



    @Override
    public void solve() {
        if(!solve(board.getSquare(0,0)))
            board.solvable =false;
        else
            board.solvable = true;
    }
}
