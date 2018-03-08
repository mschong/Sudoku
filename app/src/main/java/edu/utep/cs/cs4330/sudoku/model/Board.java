package edu.utep.cs.cs4330.sudoku.model;


import java.util.ArrayList;
import java.util.Random;

/** An abstraction of Sudoku puzzle. */
public class Board {

    /** Size of this board (number of columns/rows). */
    public final int size;
    public ArrayList<Square> grid = new ArrayList<>();
    public boolean win;
    public boolean isPrefilled;

    /** Create a new board of the given size. */
    public Board(int size) {
        this.size = size;

        initializeGrid();
        fillBoard();
        printGrid();
    }

    private void initializeGrid() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid.add(new Square(i, j));
            }
        }
    }

    private void fillBoard() {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int i = 1; i <= size; i++) {
                    if (!isWin()) {
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
        for (int i = 0; i <= 15; i++) {
            switchRows();
            switchColumns();
        }
        randomDraw();
    }

    private void clearRow(int y) {
        for (int i = 0; i < size; i++) {
            getSquare(i, y).setValue(0);
        }
    }

    private boolean isWin() {
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
        if (sum == 405) {
            win = true;
            return true;
        }
        return false;
    }

    public Square getSquare(int x, int y) {
        for (int i = 0; i < grid.size(); i++) {
            if (grid.get(i).getXCoord() == x && grid.get(i).getYCoord() == y)
                return grid.get(i);
        }
        return null;
    }

    private boolean isValidNumber(int x, int y, int val) {
        if (getSquare(x, y).getValue() != 0) {
            return false;
        }

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

    private boolean inSquare(int x, int y, int n) {
        int row = (int) (Math.floor((y / 3))) * 3;
        int col = (int) (Math.floor((x / 3))) * 3;

        for (int i = row; i < row + 3; i++) {
            for (int j = col; j < col + 3; j++) {
                if (getSquare(j, i).getValue() == n)
                    return false;
            }
        }
        return true;
    }

    // Checks if number is in row
    private boolean inColumn(int x, int y, int n) {
        for (int row = 0; row < size; row++) {
            if (getSquare(x, row).getValue() == n) {
                return false;
            }
        }
        return true;
    }

    // Checks if number is in column
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

    private int getSubgrid(int n) {
        int coord = (int) (Math.floor((n / 3))) * 3;
        int[] coords = new int[2];
        for (int i = coord, j = 0; i <= coord + 2; i++) {
            if (i != n) {
                coords[j] = i;
                j++;
            }
        }
        Random rand = new Random();
        return coords[rand.nextInt(2)];

    }

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
    //Inserts zero to delete a number
    public void insertZero(int x, int y){
        if(!getSquare(x,y).getPrefilled()) {
            getSquare(x,y).setValue(0);
        } else{
            isPrefilled = true;
        }
    }

    public boolean insertNumber(int x, int y, int n) {
        // check if valid number
        if (n!=getSquare(x,y).getValue()) {
            System.out.println("Not a valid number");
            return false;
        }
        System.out.println("Valid number, inserting " + n);
        isWin();
        return true;

    }

    private void randomDraw(){
        Random rand = new Random();

        for(int i = 0; i < 50; i++){
            int x = rand.nextInt(9);
            int y = rand.nextInt(9);
            if(getSquare(x, y).getDraw()){
                getSquare(x, y).setDraw(false);
                getSquare(x,y).setPrefilled(false);
            }
            else{
                i--;
            }
        }
    }

    /** Return the size of this board. */
    public int size() {
    	return size;
    }
}
