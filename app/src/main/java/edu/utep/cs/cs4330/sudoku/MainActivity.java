package edu.utep.cs.cs4330.sudoku;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.utep.cs.cs4330.sudoku.model.Board;

/**
 * HW1 template for developing an app to play simple Sudoku games.
 * You need to write code for three callback methods:
 * newClicked(), numberClicked(int) and squareSelected(int,int).
 * Feel free to improved the given UI or design your own.
 *
 * <p>
 *  This template uses Java 8 notations. Enable Java 8 for your project
 *  by adding the following two lines to build.gradle (Module: app).
 * </p>
 *
 * <pre>
 *  compileOptions {
 *  sourceCompatibility JavaVersion.VERSION_1_8
 *  targetCompatibility JavaVersion.VERSION_1_8
 *  }
 * </pre>
 *
 * @author Yoonsik Cheon, Marina Chong, Ana Garcia
 */
public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice connectingDevice;
    private ArrayAdapter<String> discoveredDevicesAdapter;
    //private SudokuController sudokuController;

    private Board board;

    private BoardView boardView;

    /** All the number buttons. */
    private List<View> numberButtons;
    private static final int[] numberIds = new int[] {
            R.id.n0, R.id.n1, R.id.n2, R.id.n3, R.id.n4,
            R.id.n5, R.id.n6, R.id.n7, R.id.n8, R.id.n9
    };

    /** Width of number buttons automatically calculated from the screen size. */
    private static int buttonWidth;

    private int squareX;
    private int squareY;
    public int size = 9;
    private Button bluetoothButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetoothButton = findViewById(R.id.bluetoothButton);
        board = new Board(size,1);
        boardView = findViewById(R.id.boardView);
        boardView.setBoard(board);
        boardView.addSelectionListener(this::squareSelected);
        numberButtons = new ArrayList<>(numberIds.length);
        for (int i = 0; i < numberIds.length; i++) {
            final int number = i; // 0 for delete button
            View button = findViewById(numberIds[i]);
            button.setOnClickListener(e -> numberClicked(number));
            numberButtons.add(button);
            setButtonWidth(button);
        }

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //the device doesn't support Bluetooth
        if(bluetoothAdapter == null){
            toast("Bluetooth is not supported!");
        }

        //show bluetooth devices dialog when click connect button
        bluetoothButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
            }
        });

    }

    /*private void showPrinterPickDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_bluetooth);
        dialog.setTitle("Bluetooth Devices");

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();

        //Initializing bluetooth adapters
        ArrayAdapter<String> pairedDevicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        discoveredDevicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        //locate listviews and attatch the adapters
        ListView listView = (ListView) dialog.findViewById(R.id.pairedDeviceList);
        ListView listView2 = (ListView) dialog.findViewById(R.id.discoveredDeviceList);
        listView.setAdapter(pairedDevicesAdapter);
        listView2.setAdapter(discoveredDevicesAdapter);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(discoveryFinishReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(discoveryFinishReceiver, filter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                pairedDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            pairedDevicesAdapter.add(getString(R.string.none_paired));
        }

        //Handling listview item click event
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bluetoothAdapter.cancelDiscovery();
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);

                connectToDevice(address);
                dialog.dismiss();
            }

        });

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                bluetoothAdapter.cancelDiscovery();
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);

                connectToDevice(address);
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }*/

    //Enable buttons depending in the size of the array
    public void enableButtons(){
        if(size == 4){
            for (int i = 5 ; i < numberIds.length; i++){
                View button = findViewById(numberIds[i]);
                button.setEnabled(false);
            }
        }
    }


    //create the 3 dots
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //determine which item's selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //Solve menu item
        if(id == R.id.action_solve){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Are you sure you want to give up?");
            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SudokuSolver solver = new SudokuSolver(board);
                    solver.solve();
                    boardView.postInvalidate();
                }
            });
            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        //Check menu item
        else if(id == R.id.action_check){
            if(board.check) {
                item.setTitle("Check: Off");
                board.check = false;
                boardView.postInvalidate();
            } else{
                item.setTitle("Check: On");
                board.check = true;
                boardView.postInvalidate();
            }
        }
        //4x4 board size option
        else if(id == R.id.small){
            if(item.isChecked()){
                item.setChecked(true);
            }else{
                board = new Board(4,1);
                size = 4;
                boardView = findViewById(R.id.boardView);
                boardView.setBoard(board);
                enableButtons();
                boardView.setSelectedX(-1);
                boardView.setSelectedY(-1);
                boardView.postInvalidate();
                item.setChecked(false);
            }
        }
        //9x9 board size option
        else if(id == R.id.large){
            if(item.isChecked()){
                item.setChecked(true);
            }else{
                board = new Board(9,1);
                size = 9;
                boardView = findViewById(R.id.boardView);
                boardView.setBoard(board);
                boardView.setSelectedX(-1);
                boardView.setSelectedY(-1);
                boardView.postInvalidate();
                item.setChecked(false);
            }
        }
        //Easy level option
        else if(id == R.id.easy){
            if(item.isChecked()){
                item.setChecked(true);
            }else{
                board = new Board(size, 1);
                boardView = findViewById(R.id.boardView);
                boardView.setBoard(board);
                boardView.setSelectedX(-1);
                boardView.setSelectedY(-1);
                boardView.postInvalidate();
                item.setChecked(false);
            }
        }
        //Medium level option
        else if(id == R.id.medium){
            if(item.isChecked()){
                item.setChecked(true);
            }else{
                board = new Board(size, 2);
                boardView = findViewById(R.id.boardView);
                boardView.setBoard(board);
                boardView.setSelectedX(-1);
                boardView.setSelectedY(-1);
                boardView.postInvalidate();
                item.setChecked(false);
            }
        }
        //Hard level option
        else if(id == R.id.hard){
            if(item.isChecked()){
                item.setChecked(true);
            }else{
                board = new Board(size, 3);
                boardView = findViewById(R.id.boardView);
                boardView.setBoard(board);
                boardView.setSelectedX(-1);
                boardView.setSelectedY(-1);
                boardView.postInvalidate();
                item.setChecked(false);
            }
        }


        return super.onOptionsItemSelected(item);
    }

    /** Callback to be invoked when the new button is tapped. */
    public void newClicked(View view) {

        //Create notification to ask user if they are sure about creating new game
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to start a new game?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Restart Activity
                recreate();
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /** Callback to be invoked when a number button is tapped.
     *
     * @param n Number represented by the tapped button
     *          or 0 for the delete button.
     */
    public void numberClicked(int n) {
        //Deletes number in square selected
        if(n==0 && board.getSquare(squareX,squareY).getUserValue()!=0){
            board.insertZero(squareX,squareY);
            board.getSquare(squareX,squareY).setDraw(false);
            boardView.postInvalidate();
        }
        //Insert number in square selected
        else if(board.getSquare(squareX,squareY).getUserValue()==0 && n!=0){
            board.insertNumber(squareX, squareY, n);
            //if game is won, display winning message
            if(board.win){
                boardView.win = true;
                toast("YOU WIN!");
            }
            boardView.postInvalidate();
        }else{
            if(board.getSquare(squareX,squareY).getPrefilled())
                toast("Can't delete prefilled value");
            else
                toast("Space is taken.");
        }
    }

    /**
     * Callback to be invoked when a square is selected in the board view.
     *
     * @param x 0-based column index of the selected square.
     * @param x 0-based row index of the selected square.
     */
    private void squareSelected(int x, int y) {
        //Get coordinates of square
        squareX = x;
        squareY = y;

          /*Update selected x,y coordinates in BoardView
        to draw a red border around the selected cell.
         */
        boardView.setSelectedX(x);
        boardView.setSelectedY(y);
        //force the screen to redraw upon selection
        boardView.postInvalidate();
        disableButtons();
        board.permittedNums(x,y);
       //  toast(String.format("Square selected: (%d, %d)", x, y));
    }

    //Disable buttons for numbers that are not permitted for the selected square and for the prefilled values
    public void disableButtons(){
        for(int i = 1 ; i <= board.size; i++){
            View button = findViewById(numberIds[i]);
            if(!board.isValidNumber(squareX,squareY, i) && !board.getSquare(squareX,squareY).getPrefilled()){
                button.setEnabled(false);
            } else if(board.getSquare(squareX,squareY).getPrefilled() || board.getSquare(squareX,squareY).getUserValue()!=0){
                button.setEnabled(false);
            } else
                button.setEnabled(true);
        }
    }

    /** Show a toast message. */
    private void toast(String msg) {
        Toast toast=Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP,-7,130);
        toast.show();

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to exit?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MainActivity.this.finish();
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /** Set the width of the given button calculated from the screen size. */
    private void setButtonWidth(View view) {
        if (buttonWidth == 0) {
            final int distance = 2;
            int screen = getResources().getDisplayMetrics().widthPixels;
            buttonWidth = (screen - ((9 + 1) * distance)) / 9; // 9 (1-9)  buttons in a row
        }
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = buttonWidth;
        view.setLayoutParams(params);
    }
}
