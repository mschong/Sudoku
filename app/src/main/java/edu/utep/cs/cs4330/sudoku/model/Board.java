package edu.utep.cs.cs4330.sudoku.model;


import java.util.ArrayList;
import java.util.Random;

/** An abstraction of Sudoku puzzle. */
public class Board {

    /** Size of this board (number of columns/rows). */
    public final int size;
    public  int difficulty; //easy; default
    public ArrayList<Square> grid = new ArrayList<>();
    public boolean win, inSq, inCol, inRw, check=false;
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
        randomDraw();
    }

    private void clearRow(int y) {
        for (int i = 0; i < size; i++) {
            getSquare(i, y).setValue(0);
        }
    }

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

    public Square getSquare(int x, int y) {
        for (int i = 0; i < grid.size(); i++) {
            if (grid.get(i).getXCoord() == x && grid.get(i).getYCoord() == y)
                return grid.get(i);
        }
        return null;
    }

    public boolean isValidNumber(int x, int y, int val) {
//        if (getSquare(x, y).getValue() != 0) {
//            return false;
//        }

        if (!inColumn(x, y, val)) {
            System.out.println("Number in Column");
            return false;
        }

        // check row
        if (!inRow(x, y, val)) {
            System.out.println("Number in Row");
            return false;
        }

        // check square
        if (!inSquare(x, y, val)) {
            System.out.println("Number in 3x3");
            return false;
        }

        return true;
    }

    private boolean inSquare(int x, int y, int n) {
        if(size==9) {
            int row = (int) (Math.floor((y / 3))) * 3;
            int col = (int) (Math.floor((x / 3))) * 3;

            for (int i = row; i < row + 3; i++) {
                for (int j = col; j < col + 3; j++) {
                    if(getSquare(j,i).getPrefilled()) {
                        if (getSquare(j, i).getValue() == n) {
                            System.out.println("InSq is true");
                            inSq = true;
                            return false;
                        }
                    }else{
                        if(getSquare(j, i).getUserValue() == n) {
                            System.out.println("InSq is true");
                            inSq = true;
                            return false;
                        }
                    }
                }
            }
        } else if(size == 4){
            int row = (int) (Math.floor((y / 2))) * 2;
            int col = (int) (Math.floor((x / 2))) * 2;

            for (int i = row; i < row + 2; i++) {
                for (int j = col; j < col + 2; j++) {
                    if(getSquare(j,i).getPrefilled()) {
                        if (getSquare(j, i).getValue() == n)
                            return false;
                    }else{
                        System.out.println("Not prefilled");
                        if(getSquare(j, i).getUserValue() == n) {
                            System.out.println("InSq is true");
                            inSq = true;
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    // Checks if number is in row
    private boolean inColumn(int x, int y, int n) {
        for (int row = 0; row < size; row++) {
            if(getSquare(x,row).getPrefilled()) {
                if (getSquare(x, row).getValue() == n) {
                    inCol = true;
                    return false;
                }
            }else{
                if(getSquare(x , row).getUserValue() == n) {
                    System.out.println("InCol is true");
                    inCol = true;
                    return false;
                }
            }
        }
        return true;
    }

    // Checks if number is in column
    private boolean inRow(int x, int y, int n) {
        for (int col = 0; col < size; col++) {
            if(getSquare(col,y).getPrefilled()) {
                if (getSquare(col, y).getValue() == n) {
                    inRw = true;
                    return false;
                }
            }else{
                if(getSquare(col, y).getUserValue() == n) {
                    inRw = true;
                    return false;
                }
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

    private void transpose(){
        for(int i = 0 ; i < size ; i++){
            for( int j = i; j < size; j++){
               int temp =  getSquare(i,j).getValue();
               getSquare(i,j).setValue(getSquare(j,i).getValue());
               getSquare(j,i).setValue(temp);
            }
        }
    }

    //Inserts zero to delete a number
    public void insertZero(int x, int y){
        if(!getSquare(x,y).getPrefilled()) {
            getSquare(x,y).setDraw(false);
            getSquare(x,y).insertUserValue(0);
        }
    }

    public void isWin(){
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                if(!getSquare(i,j).getPrefilled() && (getSquare(i,j).getUserValue() != getSquare(i,j).getValue())) {
                    win = false;
                    return;
                }
            }
        }
        win = true;
    }

    public void insertNumber(int x, int y, int n) {
        // check if valid number
        if (getSquare(x,y).getUserValue()==0 && !getSquare(x, y).getPrefilled() && isValidNumber(x, y, n)) {
            System.out.println("Valid number, inserting " + n);
            getSquare(x, y).insertUserValue(n);
            getSquare(x, y).setDraw(true);
            isWin();
        }
    }

    //Get random squares to draw
    private void randomDraw(){
        Random rand = new Random();

        for(int i = 0; i < numberToFill; i++){
            int x = rand.nextInt(size);
            int y = rand.nextInt(size);
            if(getSquare(x, y).getDraw()){
                getSquare(x, y).setDraw(false);
                getSquare(x,y).setPrefilled(false);
            }
            else{
                i--;
            }
        }
    }

    //Checking if user value is correct
    public boolean checkNum(int x , int y) {
        if (getSquare(x, y).getUserValue() != getSquare(x, y).getValue() && !getSquare(x, y).getPrefilled()){
            return false;
    }
        return true;
    }


    public ArrayList<Integer> permittedNums(int x, int y){
        ArrayList<Integer> nums = new ArrayList<>();
        for(int i = 1; i <= size; i++){
            if(isValidNumber(x,y,i)&& !getSquare(x,y).getPrefilled()){
                nums.add(i);
            }
        }
        System.out.println("Permitted numbers:");
        for(int i: nums){
            System.out.println(i);
        }
        return nums;
    }


    /** Return the size of this board. */
    public int size() {
    	return size;
    }
}
