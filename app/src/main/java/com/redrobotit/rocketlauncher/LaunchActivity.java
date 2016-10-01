package com.redrobotit.rocketlauncher;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Created by JBray on 9/28/2016.
 */
public class LaunchActivity extends Activity {

    SoundPool soundPool;
    SoundPool.Builder soundPoolBuilder;

    AudioAttributes attributes;
    AudioAttributes.Builder attributesBuilder;

    int soundID_beep;
    int soundID_explosion;

    private final String DEVICE_ADDRESS="20:15:04:13:08:70";
    private String btMac;
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    boolean con=false;
    SharedPreferences SP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_launch);

        buildSound();

        soundID_beep = soundPool.load(this,R.raw.beep,1);
        soundID_explosion = soundPool.load(this,R.raw.explosion,1);

        SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        if(BTinit()){
            con = BTconnect();
        }
        if(!con){
            TextView tTV = (TextView)findViewById(R.id.timerTV);
            tTV.setText("Connection \n Failure!");
            Button lBTN = (Button)findViewById(R.id.launchBTN);
            lBTN.setText("RETRY");
            lBTN.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    finish();
                    startActivity(getIntent());

                }
            });
        }




    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);


    }

    public void onClickLaunch(View view){
        led();
        new CountDownTimer(5500, 1000) {
            TextView tTV = (TextView)findViewById(R.id.timerTV);

            public void onTick(long millisUntilFinished) {
                long sec = ((millisUntilFinished + 99) / 1000 );
                tTV.setText("" + sec);
                beep(soundID_beep);
            }

            public void onFinish() {

                if(launch()) {
                    tTV.setText("LAUNCH!!!");
                    launch();
                    beep(soundID_explosion);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 3000);
                }else{
                    tTV.setText("LAUNCH FAILURE!!!");
                }
            }

        }.start();
    }

    public void led(){
        if(con) {
            String launchCode = "j\n";
            try {
                outputStream.write(launchCode.getBytes());

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @TargetApi(21)
    private void buildSound(){
        attributesBuilder = new AudioAttributes.Builder();
        attributesBuilder.setUsage(AudioAttributes.USAGE_GAME);
        attributesBuilder.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION);
        attributes = attributesBuilder.build();

        soundPoolBuilder = new SoundPool.Builder();
        soundPoolBuilder.setAudioAttributes(attributes);
        soundPool = soundPoolBuilder.build();
    }

    private void beep(int s){
        soundPool.play(s,1,1,0,0,1);
    }

    private boolean launch(){
        boolean sendLaunchCode = false;
        if(con) {
                String launchCode = "l\n";
                try {
                    outputStream.write(launchCode.getBytes());
                    sendLaunchCode = true;
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    sendLaunchCode = false;
                }

        }
        return sendLaunchCode;
    }

    public boolean BTinit()
    {
        boolean found=false;
        BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(),"Device doesnt Support Bluetooth",Toast.LENGTH_SHORT).show();
        }
        if(!bluetoothAdapter.isEnabled())
        {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableAdapter, 0);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        if(bondedDevices.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"Please Pair the Device first",Toast.LENGTH_SHORT).show();
        }
        else
        {
            btMac = SP.getString("btmac", "NA");
            if(!btMac.equals("NA")) {
                for (BluetoothDevice iterator : bondedDevices) {
                    if (iterator.getAddress().equals( btMac)) {
                        device = iterator;
                        found = true;
                        break;
                    }
                }
            } else {

            }
        }
        return found;
    }

    public boolean BTconnect()
    {
        boolean connected=true;
        try {
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID);
            socket.connect();

        } catch (IOException e) {
            e.printStackTrace();
            connected=false;
        }
        if(connected)
        {
            try {
                outputStream=socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream=socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        led();
        return connected;
    }


}
