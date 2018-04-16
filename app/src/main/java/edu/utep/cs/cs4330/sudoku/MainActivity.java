package edu.utep.cs.cs4330.sudoku;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import static android.provider.Settings.NameValueTable.NAME;

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

    private BluetoothAdapter btAdapter;
    private BluetoothDevice device;
    private BluetoothServerSocket server;
    private BluetoothSocket client;
    private List<BluetoothDevice> listDevices;
    private ArrayList<String> nameDevices;
    private int temp;
    private PrintStream logger;
    private OutputStream out;
    public static final UUID MY_UUID = UUID.fromString("6983c9aa-0326-4914-a1a0-0c3da24646e6");
    private NetworkAdapter networkAdapter;
    private NetworkAdapter.MessageListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        listDevices = new ArrayList<BluetoothDevice>();
        nameDevices = new ArrayList<String>();
        device = null;
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        out = new ByteArrayOutputStream(1024);
        logger = new PrintStream(out);
        listener = new NetworkAdapter.MessageListener() {
            @Override
            public void messageReceived(NetworkAdapter.MessageType type, int x, int y, int z, int[] others) {
                switch (type.header){
                    case "join:":
                        ArrayList<Integer> boardInfo = new ArrayList<>();
                        for(int i = 0 ; i <  board.size; i++){
                            for(int j = 0 ; j < board.size; j++){
                                boardInfo.add(i);
                                boardInfo.add(j);
                                boardInfo.add(board.getSquare(i,j).getValue());
                                if(board.getSquare(i,j).getPrefilled())
                                    boardInfo.add(1);
                                else
                                    boardInfo.add(0);
                            }
                        }
                        int[] b = new int[boardInfo.size()];
                        for(int i = 0 ; i < b.length; i++){
                            b[i] = boardInfo.get(i).intValue();
                        }
                        networkAdapter.writeJoinAck(board.size, b);
                        break;
                    case "join_ack:":
                        Board newBoard = new Board(board.size, board.difficulty);
                        for (int i = 0 ; i < others.length-4; i+=4){
                            newBoard.getSquare(others[i],others[i+1]).setValue(others[i+2]);
                            if(others[i+3]==1)
                                newBoard.getSquare(others[i],others[i+1]).setPrefilled(true);
                            else
                                newBoard.getSquare(others[i],others[i+1]).setPrefilled(false);
                        }
                        boardView.setBoard(newBoard);
                        boardView.postInvalidate();
                        break;
                    case "new:":

                        break;
                    case "new_ack:":

                        break;
                    case "fill:":
                        board.getSquare(x,y).setValue(z);
                        networkAdapter.writeFillAck(x, y, z);
                        boardView.postInvalidate();
                        break;
                    case "fill_ack:":
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    toast("Successful");
                                }
                            });
                        break;
                    case "quit:":

                        break;
                }
            }
        };
    }


    public void on(View v){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Select an option");
        alertDialogBuilder.setPositiveButton("Server", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    server();
                } catch (IOException e) {
                }

            }
        });
        alertDialogBuilder.setNegativeButton("Client", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                client();
            }
        });
        alertDialogBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void server() throws IOException {
        if (!btAdapter.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            toast("Turn on");
        } else {
            toast("Already on.");
            Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            startActivityForResult(getVisible, 0);
            acceptThread();
            runServer();
        }
    }

    public void acceptThread() {
        BluetoothServerSocket temp = null;
        try {
            temp = btAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
        } catch (IOException e) {
            Log.e("Not listening", "Socket's listen() method failed", e);
        }
        server = temp;
    }

    public void runServer() throws IOException {
        BluetoothSocket socket = null;
        while (true) {
            try {
                socket = server.accept(5000);
            } catch (IOException e) {
                Log.e("Not accepting", "Socket's accept() method failed", e);
                break;
            }

            if (socket != null) {
                toast("Connected");
                networkAdapter = new NetworkAdapter(socket, logger);
                networkAdapter.setMessageListener(listener);
                networkAdapter.receiveMessagesAsync();
                server.close();
                break;
            }
            else {
                toast("Null socket");
            }
        }
    }

    public void getClient(){
        if (!btAdapter.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            listDevices = new ArrayList<BluetoothDevice>();
            nameDevices = new ArrayList<String>();
            for (BluetoothDevice b : btAdapter.getBondedDevices()) {
                listDevices.add(b);
                nameDevices.add(b.getName());
            }
            Toast.makeText(getApplicationContext(), "Turned on",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Already on", Toast.LENGTH_LONG).show();
            listDevices = new ArrayList<BluetoothDevice>();
            nameDevices = new ArrayList<String>();
            for (BluetoothDevice b : btAdapter.getBondedDevices()) {
                listDevices.add(b);
                nameDevices.add(b.getName());
            }
        }
    }

    public void connectToThread(BluetoothDevice peerDevice) {
        BluetoothSocket tmp = null;
        device = peerDevice;
        try {
            tmp = peerDevice.createRfcommSocketToServiceRecord(MY_UUID);
        }
        catch (IOException e) {
            Log.e("Error", "Socket: " + tmp.toString() + " create() failed", e);
        }

        client = tmp;
        Log.d("socket", device.toString());
    }

    public void runClient() {
        btAdapter.cancelDiscovery();
        try {
            client.connect();
        } catch (IOException connectException) {
            try {
                client.close();
            } catch (IOException closeException) {
                Log.e("Close socket", "Could not close the client socket", closeException);
            }
            return;
        }
        toast("Connected");
        if(client == null){
            toast("Null client");
        }else {
            networkAdapter = new NetworkAdapter(client, logger);
            networkAdapter.setMessageListener(listener);
            networkAdapter.receiveMessagesAsync();
        }
    }

    public void client() {
        if (!btAdapter.isEnabled()) {
            getClient();
        }
        if(listDevices.isEmpty()){
            Intent turnOn = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
            startActivityForResult(turnOn, 0);
            getClient();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Paired Devices");

        String[] arrDevices = nameDevices.toArray(new String[nameDevices.size()]);
        int checkedItem = 0;
        builder.setSingleChoiceItems(arrDevices, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                toast(arrDevices[which]);
                temp = which;
            }
        });

        builder.setPositiveButton("CONNECT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                device = listDevices.get(temp);
                Log.d("devices", device.getAddress());
                connectToThread(device);
                runClient();
            }
        });
        builder.setNeutralButton("PAIR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent turnOn = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivityForResult(turnOn, 0);
            }
        });
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }






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
                    if(!board.solvable)
                        toast("Board can't be solved.");
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
            Board copyBoard = board.copyBoard(new Board(board.size, board.difficulty));
            SudokuSolver solver = new SudokuSolver(board);
            solver.solve();
            if(!copyBoard.solvable)
                toast("Game is not solvable");
            else
                toast("Game is solvable!");
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
                //recreate();
                if(board.size==4){
                    board = new Board(4,board.difficulty);
                    boardView = findViewById(R.id.boardView);
                    boardView.setBoard(board);
                    boardView.setSelectedX(-1);
                    boardView.setSelectedY(-1);
                    boardView.postInvalidate();
                } else{
                    board = new Board(9,board.difficulty);
                    boardView = findViewById(R.id.boardView);
                    boardView.setBoard(board);
                    boardView.setSelectedX(-1);
                    boardView.setSelectedY(-1);
                    boardView.postInvalidate();
                }
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
        if(n==0 && board.getSquare(squareX,squareY).getValue()!=0){
            board.insertZero(squareX,squareY);
            boardView.postInvalidate();
        }
        //Insert number in square selected
        else if(board.getSquare(squareX,squareY).getValue()==0 && n!=0){
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
            else {
                toast("Space is taken.");
            }
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
            } else if(board.getSquare(squareX,squareY).getPrefilled() || board.getSquare(squareX,squareY).getValue()!=0){
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

