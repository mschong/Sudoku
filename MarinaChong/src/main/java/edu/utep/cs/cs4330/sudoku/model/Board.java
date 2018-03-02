package edu.utep.cs.cs4330.sudoku.model;

import android.util.Log;

import java.util.Random;

/** An abstraction of Sudoku puzzle. */
public class Board {

    /** Size of this board (number of columns/rows). */
    public final int size;


    public int[][] grid;
    public boolean[][] prefilled;
    public boolean win;
    public boolean isPrefilled;

    /** Create a new board of the given size. */
    public Board(int size) {
        this.size = size;

        initializeGrid();
        addRandomNumbers(17);
    }


    //Initialize grid to all 0s
    public void initializeGrid() {
        grid = new int[size][size];
        prefilled = new boolean[size][size];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                grid[i][j] = 0;
                prefilled[i][j] = false;
            }
        }

    }
    //Inserts numbers in given coordinates
    public void insertNumber(int x, int y, int n) {
            // check if valid number
            if (!checkNum(x, y, n)) {
                System.out.println("Not a valid number");
            }
            System.out.println("Valid number, inserting " + n);
            checkWin();

    }
    //Checks if it's a valid number and it can be added to the game
    private boolean checkNum(int x, int y, int n) {
        // check if is outside grid or if is not between 1 and 9
        if (n > 9 || x < 0 || y < 0 || x > 8 || y > 8) {
            return false;
        }
        //Can't add number if there is already a number in that square
        if(grid[y][x] != 0)
            return false;

        // check column
        if (!checkColumn(x, n)) {
            System.out.println("Number already in column");
            return false;
        }

        // check row
        if (!checkRow(y, n)) {
            System.out.println("Number already in row");
            return false;
        }

        // check square
        if (!checkSquare(y, x, n)) {
            System.out.println("Number already in square");
            return false;
        }

        // if number passes every test add to grid
        grid[y][x] = n;
        return true;
    }

    //Checks 3x3 subgrid
    private boolean checkSquare(int x, int y, int n) {
        int row = (int) (Math.floor((x/3))) * 3;
        int col = (int) (Math.floor((y/3))) * 3;

        for(int i = row; i < row+3; i++){
            for(int j = col; j < col+3; j++){
                if(grid[i][j] == n)
                    return false;
            }
        }
        return true;
    }

    //Checks if number is in row
    private boolean checkRow(int y, int n) {
        for (int row = 0; row < grid.length; row++) {
            if (grid[y][row] == n) {
                return false;
            }
        }
        return true;
    }
    //Checks if number is in column
    private boolean checkColumn(int x, int n) {
        for (int col = 0; col < grid.length; col++) {
            if (grid[col][x] == n) {
                return false;
            }
        }
        return true;
    }
    //Checks if game is won
    private void checkWin() {
        int sum = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                sum += grid[i][j];
                // if there are still empty squares
                if (grid[i][j] == 0) {
                    return;
                }
            }
        }
        // double check if sum of numbers is correct
        if (sum == 405) {
            win = true;
            System.out.println("YOU WIN!");
        }
    }
    //True when a prefilled value is added
    private void insertPrefilled(int x, int y){
        prefilled[y][x] = true;
    }
    //Create prefilled values
    private void addRandomNumbers(int numbers) {
        Random rand = new Random();

        while (numbers > 0) {
            // nextInt is normally exclusive of the top value,
            // so add 1 to make it inclusive
            int randomX = rand.nextInt(10);
            int randomY = rand.nextInt(10);
            int randomN = rand.nextInt(9) + 1;
            if (checkNum(randomX, randomY, randomN)) {
                insertPrefilled(randomX, randomY);
                numbers --;
            }

        }

    }
    //Inserts zero to delete a number
    public void insertZero(int x, int y){
        if(!prefilled[y][x]) {
            grid[y][x] = 0;
        } else{
            isPrefilled = true;
        }
    }

    /** Return the size of this board. */
    public int size() {
    	return size;
    }
}
