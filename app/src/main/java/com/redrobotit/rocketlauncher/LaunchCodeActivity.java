package com.redrobotit.rocketlauncher;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_launch_code);

        buildSound();

        soundID_siren = soundPool.load(this,R.raw.siren,1);
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
        EditText codeET = (EditText)findViewById(R.id.codeET);
        String codeEntered = codeET.getText().toString();
        String code = "1230";
        if(codeEntered.equals(code)){
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


}
