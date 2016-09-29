package com.redrobotit.rocketlauncher;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LaunchCodeActivity extends Activity {

    SoundPool soundPool;
    SoundPool.Builder soundPoolBuilder;

    AudioAttributes attributes;
    AudioAttributes.Builder attributesBuilder;

    int soundID_siren;
    String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_launch_code);

        buildSound();

        soundID_siren = soundPool.load(this,R.raw.siren,1);

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        code = SP.getString("lcode", "NA");

        setupUI(findViewById(R.id.parent));

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);


    }

    @Override
    public void onResume() {
        super.onResume();
        setContentView(R.layout.activity_launch_code);
    }

    public void checkCode(View view){
        hideSoftKeyboard(LaunchCodeActivity.this);
        EditText codeET = (EditText)findViewById(R.id.codeET);
        String codeEntered = codeET.getText().toString();
        if(codeEntered.equals("2580")){
            //Do work
            setContentView(R.layout.granted);
            soundPool.play(soundID_siren,1,1,0,0,1);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(LaunchCodeActivity.this,LaunchActivity.class);
                    startActivity(intent);
                }
            }, 1000);
        }else{
            setContentView(R.layout.denied);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setContentView(R.layout.activity_launch_code);
                }
            }, 1000);
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

    public void onClickSettings(View view){
        Intent intent = new Intent(LaunchCodeActivity.this, Settings.class);
        startActivity(intent);
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(LaunchCodeActivity.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
}
