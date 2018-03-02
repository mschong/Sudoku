package edu.utep.cs.cs4330.sudoku.model;

/**
 * Created by mscho on 2/27/2018.
 */

public class Square {
    private int xCoord;
    private int yCoord;
    private boolean prefilled;
    private int value;


    public Square(int xCoord, int yCoord){
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.value = 0;
        this.prefilled = false;
    }

    public void insertValue(int n){
        value = n;
    }

    public int getXCoord(){
        return this.xCoord;
    }
    public int getYCoord(){
        return this.yCoord;
    }

    public int getValue(){
        return this.value;
    }

    public boolean getPrefilled(){
        return this.prefilled;
    }

    public void setValue(int value){
        this.value = value;
    }

    public void setPrefilled(boolean prefilled){
        this.prefilled = prefilled;
    }

}
