package edu.utep.cs.cs4330.sudoku.model;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

/** An abstraction of Sudoku puzzle. */
public class Board {

    /** Size of this board (number of columns/rows). */
    public final int size;
    public  int difficulty; //easy; default
    public ArrayList<Square> grid = new ArrayList<>();
    public boolean win, solvable=true;
    public int numberToFill;

    /** Create a new board of the given size. */
    public Board(int size, int difficulty) {
        this.size = size;
        this.difficulty = difficulty;
        this.numberToFill = computeNumberOfPreFills();
        initializeGrid();
        fillBoard();
        printGrid();

    }


    /**Get number of prefilled numbers depending on the level**/
    private int computeNumberOfPreFills() {
        if(this.size == 9){ //9x9 puzzle
            switch(this.difficulty){
                case 1: //easy
                    return (size*size)-30; // pre fill 17 squares
                case 2: //medium
                    return (size*size)-25;
                case 3://hard
                    return (size*size)-20;
            }
        }
        if(this.size == 4){ //4x4 puzzle
            switch(this.difficulty){
                case 1: //easy
                    return (size*size)-6; //pre fill 6 squares
                case 2: //medium
                    return (size*size)-5;
                case 3://hard
                    return (size*size)-4;
            }
        }
        return 1;
    }

    /**Create an empty grid**/
    private void initializeGrid() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid.add(new Square(i, j));
            }
        }
    }

    /**Create a solvable game**/
    private void fillBoard() {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int i = 1; i <= size; i++) {
                    if (!isSolvable()) {
                        if (isValidNumber(x, y, i)) {
                            getSquare(x, y).setValue(i);
                        } else {
                            clearRow(y);
                            y--;
                            x = x == (size - 1) ? 0 : x + 1;
                            break;

                        }
                        x = x == (size - 1) ? 0 : x + 1;
                    }
                }
            }
        }
        for (int i = 0; i <= 40; i++) {
            switchRows();
            switchColumns();
        }
        transpose();
        printGrid();
        randomDraw();
    }

    private void clearRow(int y) {
        for (int i = 0; i < size; i++) {
            getSquare(i, y).setValue(0);
        }
    }

    public Board copyBoard(Board newBoard){
        for (int i = 0; i < size; i++){
            for (int j = 0 ; j < size; j++){
                newBoard.getSquare(i,j).setValue(getSquare(i,j).getValue());
            }
        }
        return newBoard;
    }

    /**Check if the created game can be won**/
    private boolean isSolvable() {
        int sum = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                sum += getSquare(i, j).getValue();
                // if there are still empty squares
                if (getSquare(i, j).getValue() == 0) {
                    return false;
                }
            }
        }
        // double check if sum of numbers is correct
        if (sum == 405 && size == 9) {
            return true;
        } else if(sum == 40 && size == 4){
            return true;
        }
        return false;
    }

    /**Method to access squares inside grid**/
    public Square getSquare(int x, int y) {
        for (int i = 0; i < grid.size(); i++) {
            if (grid.get(i).getXCoord() == x && grid.get(i).getYCoord() == y)
                return grid.get(i);
        }
        return null;
    }

    /**Check if the number selected by the user is valid**/
    public boolean isValidNumber(int x, int y, int val) {

        if (!inColumn(x, y, val)) {
            return false;
        }

        // check row
        if (!inRow(x, y, val)) {
            return false;
        }

        // check square
        if (!inSquare(x, y, val)) {
            return false;
        }

        return true;
    }

    /**Checks if number is inside the 3x3 grid**/
    private boolean inSquare(int x, int y, int n) {
        int boundary = (int)Math.sqrt(size);
        int row = (int) (Math.floor((y / boundary))) * boundary;
        int col = (int) (Math.floor((x / boundary))) * boundary;
            for (int i = row; i < row + boundary; i++) {
                for (int j = col; j < col + boundary; j++) {
                        if (getSquare(j, i).getValue() == n) {
                            return false;
                        }
                }
            }
        return true;
    }

    /**Checks if number is in row**/
    private boolean inColumn(int x, int y, int n) {
        for (int row = 0; row < size; row++) {
                if (getSquare(x, row).getValue() == n) {
                    return false;
                }

        }
        return true;
    }

    /** Checks if number is in column**/
    private boolean inRow(int x, int y, int n) {
        for (int col = 0; col < size; col++) {
                if (getSquare(col, y).getValue() == n) {
                    return false;
                }

        }
        return true;
    }

    public  void printGrid() {
        System.out.println("\n+===+===+===+===+===+===+===+===+===+");
        for (int i = 0; i < size; i++) {
            System.out.print("|");
            for (int j = 0; j < size; j++) {
                if (j % 3 == 2) {
                    System.out
                            .print(" " + ((getSquare(j, i).getValue() == 0) ? " " : getSquare(j, i).getValue()) + " !");
                } else {
                    System.out
                            .print(" " + ((getSquare(j, i).getValue() == 0) ? " " : getSquare(j, i).getValue()) + " |");
                }
            }
            if (i % 3 == 2) {
                System.out.println("\n+===+===+===+===+===+===+===+===+===+");

            } else {
                System.out.println("\n+---+---+---+---+---+---+---+---+---+");
            }
        }
    }


    /**Get the 3x3 depending on the row or column**/
    private int getSubgrid(int n) {
        int coord = (int) (Math.floor((n / ((int)Math.sqrt(size))))) * ((int)Math.sqrt(size));
        int[] coords = new int[(int)Math.sqrt(size)-1];
        for (int i = coord, j = 0; i <= coord + (Math.sqrt(size)-1); i++) {
            if (i != n) {
                coords[j] = i;
                j++;
            }
        }
        Random rand = new Random();
        return coords[rand.nextInt((int)Math.sqrt(size)-1)];

    }

    /**Switch random rows within the 3x3 square to create a new solvable game**/
    private void switchRows() {
        Random rand = new Random();
        int row = rand.nextInt(size);
        int newRow = getSubgrid(row);

        int[] temp = new int[size];
        for (int i = 0; i < temp.length; i++) {
            temp[i] = getSquare(i, row).getValue();
        }
        for (int i = 0; i < size; i++) {
            getSquare(i, row).setValue(getSquare(i, newRow).getValue());
            getSquare(i, newRow).setValue(temp[i]);
        }
    }

    /**Switch random columns within the 3x3 square to create a new solvable game**/
    private void switchColumns(){
        Random rand = new Random();
        int col = rand.nextInt(size);
        int newCol = getSubgrid(col);

        int[] temp = new int[size];
        for (int i = 0; i < temp.length; i++) {
            temp[i] = getSquare(col, i).getValue();
        }
        for (int i = 0; i < size; i++) {
            getSquare(col, i).setValue(getSquare(newCol, i).getValue());
            getSquare(newCol, i).setValue(temp[i]);
        }
    }

    /**Transpose grid to always create a different game**/
    private void transpose(){
        for(int i = 0 ; i < size ; i++){
            for( int j = i; j < size; j++){
               int temp =  getSquare(i,j).getValue();
               getSquare(i,j).setValue(getSquare(j,i).getValue());
               getSquare(j,i).setValue(temp);
            }
        }
    }


    /**Inserts zero to delete a number**/
    public void insertZero(int x, int y){
        if(!getSquare(x,y).getPrefilled()) {
            getSquare(x,y).setValue(0);
        }
    }

    /**Check if the game is won**/
    public void isWin(){
        if(isSolvable())
            win = true;
        else
            win = false;
    }


    /**Insert number in square**/
    public void insertNumber(int x, int y, int n) {
        // check if valid number
        if (getSquare(x,y).getValue()==0 && !getSquare(x, y).getPrefilled() && isValidNumber(x, y, n)) {
            getSquare(x, y).setValue(n);
            isWin();
        }
    }


    /**Get random squares to draw**/
    private void randomDraw(){
        Random rand = new Random();

        for(int i = 0; i < numberToFill; i++){
            int x = rand.nextInt(size);
            int y = rand.nextInt(size);
            if(getSquare(x, y).getValue()!=0){
                getSquare(x, y).setValue(0);
                getSquare(x,y).setPrefilled(false);
            }
            else{
                i--;
            }
        }
    }


    /**Checks if user value is correct**/
//    public boolean checkNum(int x , int y) {
//        if (getSquare(x, y).getUserValue() != getSquare(x, y).getValue() && !getSquare(x, y).getPrefilled()) {
//            return false;
//        }
//        return true;
//    }



    /**Gets the permitted numbers per square**/
    public ArrayList<Integer> permittedNums(int x, int y){
        ArrayList<Integer> nums = new ArrayList<>();
        for(int i = 1; i <= size; i++){
            if(isValidNumber(x,y,i)&& !getSquare(x,y).getPrefilled()){
                nums.add(i);
            }
        }

        return nums;
    }


    /** Return the size of this board. */
    public int size() {
    	return size;
    }
}
