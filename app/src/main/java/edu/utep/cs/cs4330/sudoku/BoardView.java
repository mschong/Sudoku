package edu.utep.cs.cs4330.sudoku;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.ArrayList;
import java.util.List;

import edu.utep.cs.cs4330.sudoku.model.Board;

/**
 * A special view class to display a Sudoku board modeled by the
 * {@link edu.utep.cs.cs4330.sudoku.model.Board} class. You need to write code for
 * the <code>onDraw()</code> method.
 *
 * @see edu.utep.cs.cs4330.sudoku.model.Board
 * @author Dr. Yoonsik Cheon, Marina Chong, Ana Garcia
 */
public class BoardView extends View {

    /** To notify a square selection. */
    public interface SelectionListener {

        /** Called when a square of the board is selected by tapping.
         * @param x 0-based column index of the selected square.
         * @param y 0-based row index of the selected square. */
        void onSelection(int x, int y);
    }

    /** Listeners to be notified when a square is selected. */
    private final List<SelectionListener> listeners = new ArrayList<>();

    /** Number of squares in rows and columns.*/
    private int boardSize = 9;

    /** Board to be displayed by this view. */
    private Board board;

    /** Width and height of each square. This is automatically calculated
     * this view's dimension is changed. */
    private float squareSize;

    /** Translation of screen coordinates to display the grid at the center. */
    private float transX;

    /** Translation of screen coordinates to display the grid at the center. */
    private float transY;

    private int selectedX = -1;

    private int selectedY = -1;

    /** Paint to draw the background of the grid. */
    private Paint boardPaint= new Paint(Paint.ANTI_ALIAS_FLAG);
    {
        int boardColor = Color.rgb(201, 186, 145);
        boardPaint.setColor(boardColor);
        boardPaint.setAlpha(80); // semi transparent
    };

    private Paint winBoardPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    {
        winBoardPaint.setColor(Color.rgb(187, 255, 153));
        boardPaint.setAlpha(80); //semi transparent
    }

    private Paint grayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    {
        grayPaint.setColor(Color.GRAY);
    }

    private final Paint blackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    {
        blackPaint.setColor(Color.BLACK);
        blackPaint.setStrokeWidth(7);
    }

    private final Paint preFilledPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    {
        preFilledPaint.setColor(Color.BLACK);
        preFilledPaint.setTextSize(50);
    }

    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    {
        textPaint.setColor(Color.rgb(196, 77, 255));
        textPaint.setTextSize(50);
    }


    private final Paint squareSelectionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    {
        squareSelectionPaint.setColor(Color.CYAN);
        squareSelectionPaint.setStyle(Paint.Style.STROKE);
        squareSelectionPaint.setStrokeWidth(7);
    }


    public void setSelectedX(int x){
        this.selectedX = x;
    }

    public void setSelectedY(int y){
        this.selectedY = y;
    }


    boolean win;



    /** Create a new board view to be run in the given context. */
    public BoardView(Context context) { //@cons
        this(context, null);
    }

    /** Create a new board view by inflating it from XML. */
    public BoardView(Context context, AttributeSet attrs) { //@cons
        this(context, attrs, 0);
    }

    /** Create a new instance by inflating it from XML and apply a class-specific base
     * style from a theme attribute. */
    public BoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setSaveEnabled(true);
        getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
    }

    /** Set the board to be displayed by this view. */
    public void setBoard(Board board) {
        this.board = board;
        boardSize = board.size;
    }

    /** Draw a 2-D graphics representation of the associated board. */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(transX, transY);
        if (board != null) {
            //If game is not won, it will display regular colored board
            drawGrid(canvas);
            drawSquares(canvas);
            drawSelection(canvas);
            //If game is won. board will change to color green to indicate the game has been won.
        }

        canvas.translate(-transX, -transY);
    }

    /** Draw horizontal and vertical grid lines. */
    private void drawGrid(Canvas canvas) {
        System.out.println("in drawGrid");
        final float maxCoord = maxCoord();
        if(!board.win)
            canvas.drawRect(0, 0, maxCoord, maxCoord, boardPaint);
        else
            canvas.drawRect(0, 0, maxCoord, maxCoord, winBoardPaint);

        //Top Line
        canvas.drawLine(0,0,maxCoord,0, blackPaint);
        //Bottom Line
        canvas.drawLine(0,maxCoord,maxCoord,maxCoord, blackPaint);
        //Left Line
        canvas.drawLine(0, maxCoord, 0,0, blackPaint);
        //Right Line
        canvas.drawLine(maxCoord,0,maxCoord,maxCoord, blackPaint);

        if(boardSize == 4){
            draw4x4Grid(canvas,maxCoord);
        }
        else if(boardSize == 9){
            draw9x9Grid(canvas, maxCoord);
        }

    }

    /**Draw custom lines for a 4x4 Sudoku**/
    private void draw4x4Grid(Canvas canvas, float maxCoord){
        //vertical bold line
        canvas.drawLine(maxCoord/2,0,maxCoord/2,maxCoord,blackPaint);

        //horizontal bold line
        canvas.drawLine(0,maxCoord/2,maxCoord,maxCoord/2,blackPaint);

        //vertical gray lines
        for(int i =1; i < boardSize; i++){
            canvas.drawLine((maxCoord/boardSize)*i,0,(maxCoord/boardSize)*i,maxCoord,grayPaint);
        }

        //horizontal gray lines
        for(int i = 1; i < boardSize; i++){
            canvas.drawLine(0,(maxCoord/boardSize)*i, maxCoord,(maxCoord/boardSize)*i,grayPaint);
        }

    }

    /**Draw custom lines for a 9x9 Sudoku*/
    private void draw9x9Grid(Canvas canvas, float maxCoord){

        //Vertical bold lines
        canvas.drawLine(maxCoord/(int)Math.sqrt(boardSize),0,maxCoord/(int)Math.sqrt(boardSize),maxCoord,blackPaint);
        canvas.drawLine((maxCoord/(int)Math.sqrt(boardSize))*2, 0, (maxCoord/(int)Math.sqrt(boardSize))*2,maxCoord,blackPaint);
        //Horizontal bold lines
        canvas.drawLine(0,maxCoord/(int)Math.sqrt(boardSize),maxCoord,maxCoord/(int)Math.sqrt(boardSize),blackPaint);
        canvas.drawLine(0,(maxCoord/(int)Math.sqrt(boardSize))*2,maxCoord,(maxCoord/(int)Math.sqrt(boardSize))*2, blackPaint);

        //Vertical gray lines
        for (int i = 1; i < boardSize; i++){
            canvas.drawLine((maxCoord/boardSize)*i,0, (maxCoord/boardSize)*i, maxCoord, grayPaint);
        }

        //Horizontal gray Lines
        for(int i = 1; i < boardSize; i++){
            canvas.drawLine(0,(maxCoord/boardSize)*i,maxCoord,(maxCoord/boardSize)*i,grayPaint);
        }
    }


    /** Draw all the squares (numbers) of the associated board. */
    private void drawSquares(Canvas canvas) {
        int gridSpacing = getHeight()/ board.size;
        int boardSize = board.size * gridSpacing;

        int startX = (getWidth() - boardSize)/(getWidth()/2);
        int startY = (getHeight() - boardSize)/(getHeight()/2);

        for(int i = 0;i< board.size; i++){
            for(int j = 0; j<board.size; j++){
                //Checks if check was selected
                if(board.check){
                    //If the user the number entered is incorrect it would be painted reed
                    if(!board.checkNum(i,j) && board.getSquare(i,j).getUserValue()!=0){
                        Paint wrongPaint = new Paint();
                        wrongPaint.setColor(Color.RED);
                        wrongPaint.setTextSize(50);
                        canvas.drawText(Integer.toString(board.getSquare(i,j).getUserValue()),(startX + (i+1)*gridSpacing-35)-15,(startY + j*gridSpacing)+55, wrongPaint);

                    }
                    //If it's a prefilled value then paint it black
                    else if(board.getSquare(i,j).getDraw() && board.getSquare(i,j).getPrefilled()){
                        canvas.drawText(Integer.toString(board.getSquare(i,j).getValue()),(startX + (i+1)*gridSpacing-35)-15,(startY + j*gridSpacing)+55,preFilledPaint);
                    }
                    //if the number is correct leave it purple
                    else if(board.getSquare(i,j).getDraw()){
                        canvas.drawText(Integer.toString(board.getSquare(i,j).getUserValue()),(startX + (i+1)*gridSpacing-35)-15,(startY + j*gridSpacing)+55,textPaint);
                    }
                    else{
                        Paint numPaint = new Paint();
                        numPaint.setColor(Color.BLACK);
                        numPaint.setTextSize(15);
                        for(int num = 0; num < board.permittedNums(i,j).size(); num++){
                            canvas.drawText(Integer.toString(board.permittedNums(i,j).get(num)), (startX + (i + 1) * gridSpacing - 35) -(34-(num*9)), (startY + j * gridSpacing) + 68, numPaint);
                        }
                    }
                }
                //Check if it's one of the prefilled values
                else if(board.getSquare(i,j).getDraw() && board.getSquare(i,j).getPrefilled()){
                    canvas.drawText(Integer.toString(board.getSquare(i,j).getValue()),(startX + (i+1)*gridSpacing-35)-15,(startY + j*gridSpacing)+55,preFilledPaint);
                }
                else if(board.getSquare(i,j).getDraw()) {
                    canvas.drawText(Integer.toString(board.getSquare(i, j).getUserValue()), (startX + (i + 1) * gridSpacing - 35) - 15, (startY + j * gridSpacing) + 55, textPaint);

                } else{
                    Paint numPaint = new Paint();
                    numPaint.setColor(Color.BLACK);
                    numPaint.setTextSize(15);
                    for(int num = 0; num < board.permittedNums(i,j).size(); num++){
                        canvas.drawText(Integer.toString(board.permittedNums(i,j).get(num)), (startX + (i + 1) * gridSpacing - 35) -(34-(num*9)), (startY + j * gridSpacing) + 68, numPaint);
                    }
                }
            }
        }


    }

    /**Draws a red border around the cell the user selected**/
    private void drawSelection(Canvas canvas){
        //the difference between board size and the selected x-coordinate
        float diff = maxCoord() /(float)boardSize;
        if(selectedX != -1 && selectedY != -1){
            System.out.println("Selected X: " + selectedX);
            System.out.println("Selected Y: " + selectedY);

            if(board.getSquare(selectedX,selectedY).getPrefilled()){
                squareSelectionPaint.setColor(Color.BLUE);
                canvas.drawRect(selectedX*diff,selectedY*diff,selectedX*diff+diff,selectedY*diff+diff,squareSelectionPaint);
            }else {
                squareSelectionPaint.setColor(Color.CYAN);
                canvas.drawRect(selectedX * diff, selectedY * diff, selectedX * diff + diff, selectedY * diff + diff, squareSelectionPaint);
            }
        }

    }

    /** Overridden here to detect tapping on the board and
     * to notify the selected square if exists. */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                int xy = locateSquare(event.getX(), event.getY());
                if (xy >= 0) {
                    // xy encoded as: x * 100 + y
                    notifySelection(xy / 100, xy % 100);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    /**
     * Given screen coordinates, locate the corresponding square of the board, or
     * -1 if there is no corresponding square in the board.
     * The result is encoded as <code>x*100 + y</code>, where x and y are 0-based
     * column/row indexes of the corresponding square.
     */
    private int locateSquare(float x, float y) {
        x -= transX;
        y -= transY;
        if (x <= maxCoord() &&  y <= maxCoord()) {
            final float squareSize = lineGap();
            int ix = (int) (x / squareSize);
            int iy = (int) (y / squareSize);
            return ix * 100 + iy;
        }
        return -1;
    }

    /** To obtain the dimension of this view. */
    private final ViewTreeObserver.OnGlobalLayoutListener layoutListener
            =  new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            squareSize = lineGap();
            float width = Math.min(getMeasuredWidth(), getMeasuredHeight());
            transX = (getMeasuredWidth() - width) / 2f;
            transY = (getMeasuredHeight() - width) / 2f;
        }
    };

    /** Return the distance between two consecutive horizontal/vertical lines. */
    protected float lineGap() {
        return Math.min(getMeasuredWidth(), getMeasuredHeight()) / (float) boardSize;
    }

    /** Return the number of horizontal/vertical lines. */
    private int numOfLines() { //@helper
        return boardSize + 1;
    }

    /** Return the maximum screen coordinate. */
    protected float maxCoord() { //@helper
        return lineGap() * (numOfLines() - 1);
    }

    /** Register the given listener. */
    public void addSelectionListener(SelectionListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /** Unregister the given listener. */
    public void removeSelectionListener(SelectionListener listener) {
        listeners.remove(listener);
    }

    /** Notify a square selection to all registered listeners.
     *
     * @param x 0-based column index of the selected square
     * @param y 0-based row index of the selected square
     */
    private void notifySelection(int x, int y) {
        for (SelectionListener listener: listeners) {
            listener.onSelection(x, y);
        }
    }

}
