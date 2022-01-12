package com.example.exerciseminiproject;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class BridgeActivity extends AppCompatActivity {
    private EditText Rounds;
    private EditText Seconds;
    private EditText Rest;
    private Button Start, CalculateTime, Stop,Pause;
    private ProgressBar Progress;

    private TextView ShowTotalTime;
    private TextView ShowTimer;

    private SoundPool soundPool;

    private int soundStart, soundStep, soundEnd, soundError, soundRest;

    int rounds, seconds, rest;

    int totalTime;

    int secCount = 0;
    char next = 'r';
    int reach;
    boolean wrongInput = false;
    boolean stop = false;
    boolean running = false;
    boolean pause = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bridge);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder().setMaxStreams(1).setAudioAttributes(audioAttributes).build();
        } else {
            soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        }

        soundStep = soundPool.load(this, R.raw.coin, 1);
        soundError =soundPool.load(this, R.raw.gpmuthu, 1);
        soundRest = soundPool.load(this, R.raw.rest, 1);

        Rounds = findViewById(R.id.bridge_round_input);
        Seconds = findViewById(R.id.bridge_seconds_input);
        Rest = findViewById(R.id.bridge_rest_input);

        Start = findViewById(R.id.bridge_start);
        CalculateTime = findViewById(R.id.bridge_cal);
        Stop = findViewById(R.id.bridge_stop);
        Pause = findViewById(R.id.bridge_pause);

        Progress = findViewById(R.id.bridge_progressbar);
        ShowTotalTime = findViewById(R.id.bridge_time);
        ShowTimer = findViewById(R.id.bridge_showTime);


        CalculateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    calTime();
            }
        });

        Start.setOnClickListener(v -> {
            if(!running){
                calTime();
                if(!wrongInput){
                    startTimer();
                }
            }
            else{
                Toast.makeText(BridgeActivity.this, "Already running stop that to create New", Toast.LENGTH_LONG).show();
            }

        });
        Pause.setOnClickListener(v -> {
            if(running){
                if(pause){
                    Pause.setText("pause");
                    pause = false;
                }
                else{
                    pause = true;
                    Pause.setText("resume");
                }
            }
            else{
                Toast.makeText(BridgeActivity.this, "pause can be done in the middle of the exercise", Toast.LENGTH_SHORT).show();
            }

        });
        Stop.setOnClickListener(v -> {
            stop = true;
        });
    }

    private void startTimer() {
        running = true;
        reach = seconds;
        soundPool.play(soundStep, 1, 1, 0, 0, 1);
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(pause){
                    if(stop){
                        Toast.makeText(BridgeActivity.this, "Stop", Toast.LENGTH_SHORT).show();
                        reset();
                    }
                    else
                    handler.postDelayed(this, 1000);
                }
                else if(secCount <= totalTime && !stop ){

                    float p = (secCount / (float) totalTime) * 100;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Progress.setProgress((int) p, true);
                    }
                    else{
                        Progress.setProgress((int)p);
                    }

                    int mins = (secCount % 3600) / 60;
                    int sec = secCount % 60;

                    String time = String.format("%02d:%02d", mins, sec);
                    ShowTimer.setText(time);
                    if(secCount == reach){
                        if(next=='r'){
                            soundPool.play(soundRest, 1, 1, 0, 0, 1);
                            reach+=rest;
                            next = 'w';
                        }
                        else if(next=='w'){
                            soundPool.play(soundStep, 1, 1, 0, 0, 1);
                            reach+=seconds;
                            next = 'r';
                        }
                    }
                    secCount+=1;
                    handler.postDelayed(this, 1000);
                }
                else{
                    if(stop){
                        Toast.makeText(BridgeActivity.this, "Stop", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(BridgeActivity.this, "Finished", Toast.LENGTH_SHORT).show();
                    }
                    reset();
                }
            }
        });

    }


    private void calTime() {
        parseInput();
        if(!wrongInput){
                totalTime =  (rounds * seconds) + (rounds * rest);
                int min = (totalTime % 3600) / 60;
                int sec = totalTime % 60;
                String time = String.format("%02d:%02d", min, sec);
                ShowTotalTime.setText(time);
            }
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void reset(){
        secCount = 0;
        next = 'r';
        running = false;
        wrongInput = false;
        stop = false;
        reach = 0;
        ShowTimer.setText("00:00");
        pause = false;
        Pause.setText("pause");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Progress.setProgress(0, true);
        }
        else{
            Progress.setProgress(0);
        }

    }
    public void parseInput() {
        try {
            closeKeyboard();
            rounds = Integer.parseInt(Rounds.getText().toString());
            seconds = Integer.parseInt(Seconds.getText().toString());
            rest = Integer.parseInt(Rest.getText().toString());
            wrongInput =false;


        } catch (Throwable e) {
            Toast.makeText(BridgeActivity.this, "^_^ Invalid input 😠", Toast.LENGTH_LONG).show();
            soundPool.play(soundError, 1, 1, 0, 1, 1);
            wrongInput =true;
        }
    }
}