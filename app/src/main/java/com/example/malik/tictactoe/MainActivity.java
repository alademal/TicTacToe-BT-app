package com.example.malik.tictactoe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.bluetooth.*;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "TicTacToe";

    private BluetoothAdapter btAdapter;
    private static final UUID uuid = UUID.fromString("6c1a951e-a5d1-4423-b92d-162a2c230768");
    String serviceName;
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    BluetoothConnectionService mBluetoothConnection;
    BluetoothDevice mBTDevice;

    Button startButton;
    private final int NUM_ROWS = 3;
    Button[][] buttons = new Button[NUM_ROWS][NUM_ROWS];
    private final int numButtons = buttons.length * buttons[0].length;
    private boolean gameInProgress = false;
    private boolean isHosting = false;
    private boolean isJoining = false;
    private boolean isMyTurn = false;
    private boolean turnTaken = false;
    String marked = "####";
    private TicTacToeGame game;
    private String mark = "_";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "MainActivity: onCreate()");

        game = new TicTacToeGame(NUM_ROWS);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        //Broadcasts when bond state changes (ie:pairing)
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver4, filter);

        if (btAdapter == null) {
            Log.d(TAG, "Error: Device does not support Bluetooth!");
            Toast toast = Toast.makeText(getApplicationContext(),
                    "This device does not support Bluetooth!", Toast.LENGTH_LONG);
            toast.show();
        }

        startButton = findViewById(R.id.startBtn);
        buttons[0][0] = findViewById(R.id.btn00);
        buttons[0][1] = findViewById(R.id.btn01);
        buttons[0][2] = findViewById(R.id.btn02);
        buttons[1][0] = findViewById(R.id.btn10);
        buttons[1][1] = findViewById(R.id.btn11);
        buttons[1][2] = findViewById(R.id.btn12);
        buttons[2][0] = findViewById(R.id.btn20);
        buttons[2][1] = findViewById(R.id.btn21);
        buttons[2][2] = findViewById(R.id.btn22);
    }

    //Changes text on button when clicked
    public void btn00placeChar(View view) {
        if (gameInProgress && isMyTurn && !game.spotTaken(0,0) && !turnTaken) {
            ((TextView)findViewById(R.id.btn00)).setText(mark);
            game.updateBoard(0,0, mark);
            turnTaken = true;
            marked = "0" + mark + "00";
        }
    }
    public void btn01placeChar(View view) {
        if (gameInProgress && isMyTurn && !game.spotTaken(0,1) && !turnTaken) {
            ((TextView)findViewById(R.id.btn01)).setText(mark);
            game.updateBoard(0,1, mark);
            turnTaken = true;
            marked = "1" + mark + "01";
        }
    }
    public void btn02placeChar(View view) {
        if (gameInProgress && isMyTurn && !game.spotTaken(0,2) && !turnTaken) {
            ((TextView)findViewById(R.id.btn02)).setText(mark);
            game.updateBoard(0,2, mark);
            turnTaken = true;
            marked = "2" + mark + "02";
        }
    }
    public void btn10placeChar(View view) {
        if (gameInProgress && isMyTurn && !game.spotTaken(1,0) && !turnTaken) {
            ((TextView)findViewById(R.id.btn10)).setText(mark);
            game.updateBoard(1,0, mark);
            turnTaken = true;
            marked = "3" + mark + "10";
        }
    }
    public void btn11placeChar(View view) {
        if (gameInProgress && isMyTurn && !game.spotTaken(1,1) && !turnTaken) {
            ((TextView)findViewById(R.id.btn11)).setText(mark);
            game.updateBoard(1,1, mark);
            turnTaken = true;
            marked = "4" + mark + "11";
        }
    }
    public void btn12placeChar(View view) {
        if (gameInProgress && isMyTurn && !game.spotTaken(1,2) && !turnTaken) {
            ((TextView)findViewById(R.id.btn12)).setText(mark);
            game.updateBoard(1,2, mark);
            turnTaken = true;
            marked = "5" + mark + "12";
        }
    }
    public void btn20placeChar(View view) {
        if (gameInProgress && isMyTurn && !game.spotTaken(2,0) && !turnTaken) {
            ((TextView)findViewById(R.id.btn20)).setText(mark);
            game.updateBoard(2,0, mark);
            turnTaken = true;
            marked = "6" + mark + "20";
        }
    }
    public void btn21placeChar(View view) {
        if (gameInProgress && isMyTurn && !game.spotTaken(2,1) && !turnTaken) {
            ((TextView)findViewById(R.id.btn21)).setText(mark);
            game.updateBoard(2,1, mark);
            turnTaken = true;
            marked = "7" + mark + "21";
        }
    }
    public void btn22placeChar(View view) {
        if (gameInProgress && isMyTurn && !game.spotTaken(2,2) && !turnTaken) {
            ((TextView)findViewById(R.id.btn22)).setText(mark);
            game.updateBoard(2,2, mark);
            turnTaken = true;
            marked = "8" + mark + "22";
        }
    }

    public void resetGame(View view) {
        setAllButtons("_");
    }

    public void setAllButtons(String display) {
        if (!gameInProgress) {
            TypedArray btns = getResources().obtainTypedArray(R.array.buttons);

            for (int i = 0; i < numButtons; ++i) {
                ((TextView)findViewById(btns.getResourceId(i, 0))).setText(display);
            }

//            ticTacToeBoard = new char[NUM_ROWS][NUM_ROWS];
            isHosting = false;
            isJoining = false;
            btns.recycle();
        }
    }

    public void btnDiscover() {
        Log.d(TAG, "btnDiscover: Looking for unpaired devices.");

        if(btAdapter.isDiscovering()){
            btAdapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover: Canceling discovery.");

            btAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
        if(!btAdapter.isDiscovering()){

            btAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
            Toast toast4 = Toast.makeText(getApplicationContext(),
                    "btnDiscover: Starting discovery.", Toast.LENGTH_LONG);
            toast4.show();
        }
    }

    public void hostGame(View view) /*throws IOException*/ {
        if (!gameInProgress && !isJoining){
            isHosting = true;
            serviceName = ((EditText)findViewById(R.id.hostAuthKey)).getText().toString();

            mark = "X";
            game.isGameOn(gameInProgress = true);
            isMyTurn = true;
            playGame();
            mark = "_";
        }
        isHosting = false;
        Toast toast = Toast.makeText(getApplicationContext(), serviceName, Toast.LENGTH_LONG);
        toast.show();
    }

    public void joinGame(View view) {
        String joinKey = "";
        if (!gameInProgress && !isHosting){
            isJoining = true;
            joinKey = ((EditText)findViewById(R.id.joinAuthKey)).getText().toString();

            mark = "O";
            game.isGameOn(gameInProgress = true);
            isMyTurn = false;
            playGame();
            mark = "_";
        }
        isJoining = false;
        Toast toast = Toast.makeText(getApplicationContext(), joinKey, Toast.LENGTH_LONG);
        toast.show();
    }

    public void playGame() {
        String outcome = "DRAW";
        String msgToPlayer = "It's a draw! Nobody wins!";
        String readMsg = "";
        TypedArray btns = getResources().obtainTypedArray(R.array.buttons);

        btnDiscover();
        btAdapter.cancelDiscovery();
        mBTDevice.createBond();
        mBluetoothConnection = new BluetoothConnectionService(MainActivity.this);
        mBluetoothConnection.startClient(mBTDevice, uuid);

        setAllButtons("?");
        while (gameInProgress) {
            while (isMyTurn) {
                if (turnTaken) {
                    //send data
                    mBluetoothConnection.write(marked.getBytes());
                    isMyTurn = false;
                    turnTaken = false;
                }
            }
            if (game.gameEndWon()) {
                outcome = "WIN";
                break;
            }
            else if (game.gameEndDraw()) {
                break;
            }

            while (!isMyTurn) {

                try {
                    readMsg = mBluetoothConnection.read();
                }
                catch (Exception e) {
                    turnTaken = true;
                }

                if (turnTaken) {
                    int index = Character.getNumericValue(readMsg.charAt(0));
                    String opponentMark = String.valueOf(readMsg.charAt(1));
                    int index2D_0 = Character.getNumericValue(readMsg.charAt(2));
                    int index2D_1 = Character.getNumericValue(readMsg.charAt(3));
                    ((TextView)findViewById(btns.getResourceId(index, 0))).setText(opponentMark);
                    game.updateBoard(index2D_0, index2D_1, opponentMark);
                    isMyTurn = true;
                    turnTaken = false;
                }
            }
            if (game.gameEndWon()) {
                outcome = "LOSE";
                break;
            }
            else if (game.gameEndDraw()) {
                break;
            }
        }
        gameInProgress = false;
        game.isGameOn(gameInProgress);

        switch (outcome) {
            case "WIN":
                msgToPlayer = "Congratulations, you win!";
                break;
            case "LOSE":
                msgToPlayer = "You lost. Better luck next time!";
                break;
            case "DRAW":
                break;
        }
        Toast results = Toast.makeText(getApplicationContext(), msgToPlayer, Toast.LENGTH_LONG);
        results.show();
        btns.recycle();
        setAllButtons("_");
    }

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(btAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, btAdapter.ERROR);

                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    /**
     * Broadcast Receiver for changes made to bluetooth states such as:
     * 1) Discoverability mode on/off or expire.
     */
    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    //Device is in Discoverable Mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
                        break;
                    //Device not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver2: Connecting....");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
                        break;
                }

            }
        }
    };




    /**
     * Broadcast Receiver for listing devices that are not yet paired
     * -Executed by btnDiscover() method.
     */
    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
            }
        }
    };

    /**
     * Broadcast Receiver that detects bond state changes (Pairing status changes)
     */
    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            //3 cases:
            //case1: bonded already
            if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");
                //inside BroadcastReceiver4
                mBTDevice = mDevice;
                Toast toast = Toast.makeText(getApplicationContext(),
                        "onReceive: BT bonded.", Toast.LENGTH_LONG);
                toast.show();
            }
            //case2: creating a bond
            if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");
            }
            //case3: breaking a bond
            if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                Log.d(TAG, "BroadcastReceiver: BOND_NONE.");
            }
        }
    };

    protected void onStart() {
        super.onStart();
        if(btAdapter == null){
            Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
        }
        if(!btAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBT: enabling BT.");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
        if(btAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBT: disabling BT.");
            btAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
    }

    protected void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivity: onResume()");
    }

    protected void onPause() {
        super.onPause();
        Log.d(TAG, "MainActivity: onPause()");
    }

    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "MainActivity: onRestart()");
    }

    protected void onStop() {
        super.onStop();
        Log.d(TAG, "MainActivity: onStop()");
    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver1);
        unregisterReceiver(mBroadcastReceiver2);
        unregisterReceiver(mBroadcastReceiver3);
        unregisterReceiver(mBroadcastReceiver4);
        if(btAdapter != null)
        {
            btAdapter.disable();
        }
        btAdapter = null;
        Log.d(TAG, "MainActivity: onDestroy()");
    }
}