package com.example.exerciseminiproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class CatNcowActivity extends AppCompatActivity {
    private EditText Rounds;
    private EditText Seconds;
    private Button Start, CalculateTime, Stop, Pause;
    private ProgressBar Progress;
    private TextView ShowTotalTime;
    private SoundPool soundPool;
    private MediaPlayer mediaPlayer;

    TextView ShowTime;
    TextView ShowRound;

    private int soundStart, soundStep, soundEnd, soundError;

    private Button PossibleCatNcow;

    int rounds, seconds;
    int secCount = 1;
    int round = 1;
    boolean running = false;
    boolean musicFinished = false;
    int Startflag = 0;
    int midStop = 0;
    boolean wrongInput = false;
    boolean pause = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cat_ncow);

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

        soundStart = R.raw.start;
        soundEnd = R.raw.end;

        ShowTotalTime = findViewById(R.id.totalTime);
        Rounds = findViewById(R.id.roundInput);
        Seconds = findViewById(R.id.secondsInput);
        Start = findViewById(R.id.start);
        Progress = findViewById(R.id.progressbar);
        CalculateTime = findViewById(R.id.cc_cal_time);
        Pause = findViewById(R.id.cc_btn_pause);
        ShowTime = findViewById(R.id.showTime);
        ShowRound = findViewById(R.id.ShowRound);

        PossibleCatNcow = findViewById(R.id.cc_possible_exercise);
        PossibleCatNcow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CatNcowActivity.this, "clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CatNcowActivity.this, PossibleCatNCow.class);
                startActivity(intent);
            }
        });

        Stop = findViewById(R.id.cc_btn_stop);
        Stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(running){
                    midStop = 1;
                }
                else{
                    Toast.makeText(CatNcowActivity.this, "Exercise Not Started", Toast.LENGTH_SHORT).show();
                }
            }
        });
        CalculateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalTime(v);
            }
        });
        Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!running) {
                    CalTime(v);
                    if (!wrongInput) {
                        start();
                    }
                }
                else{
                    Toast.makeText(CatNcowActivity.this, "Exercise Already Started", Toast.LENGTH_SHORT).show();
                }
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
                Toast.makeText(CatNcowActivity.this, "pause can be done in the middle of the exercise", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void play(int music) {

        mediaPlayer = MediaPlayer.create(this, music);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.release();
//                musicFinished = true;
            }
        });
        mediaPlayer.start();
    }

    public void start() {
//            play(soundStart);
//            while(musicFinished==false);
//            musicFinished=false;
        running = true;
        int total_time = 2 * rounds * seconds;



        soundPool.play(soundStep, 1, 1, 0, 0, 1);

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(secCount > total_time){
                    Toast.makeText(CatNcowActivity.this, "finished", Toast.LENGTH_SHORT).show();
                    play(soundEnd);
                    reset();
                }
                else if(midStop == 1){
                    Toast.makeText(CatNcowActivity.this, "Stoped", Toast.LENGTH_SHORT).show();
                    reset();
                }
                else if(pause){
                    handler.postDelayed(this, 1000);
                }
                else{
                    float p = (secCount / (float) total_time) * 100;
                    Progress.setProgress((int) p);
                    int mins = (secCount % 3600) / 60;
                    int sec = secCount % 60;
                    String time = String.format("%02d:%02d", mins, sec);
                    ShowTime.setText(time);
                    ShowRound.setText("" + round);
                    if (secCount % seconds == 0) {
                        soundPool.play(soundStep, 1, 1, 0, 0, 1);
                        if (secCount % (2 * seconds) == 0)
                            round++;
                    }
                    secCount++;
                    handler.postDelayed(this, 1000);
                }
            }
        });

    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private void reset() {
        midStop = 0;
        round =1;
        secCount = 1;
        running = false;
        ShowTime.setText("00:00");
        pause = false;
        Pause.setText("pause");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Progress.setProgress(0, true);
        } else {
            Progress.setProgress(0);
        }
        ShowRound.setText("");
    }
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release();
        soundPool = null;
    }

    public void CalTime(View v) {
        parseInput();
        if(!wrongInput){
            int total_time = 2 * rounds * seconds;
            int mins = (total_time % 3600) / 60;
            int sec = total_time % 60;
            String time = String.format("%02d:%02d", mins, sec);
            ShowTotalTime.setText(time);
        }
    }

    public void parseInput() {
        try {
            closeKeyboard();
            rounds = Integer.parseInt(Rounds.getText().toString());
            seconds = Integer.parseInt(Seconds.getText().toString());
            wrongInput = false;
        } catch (Throwable e) {
            Toast.makeText(CatNcowActivity.this, "^_^ enter numbers", Toast.LENGTH_LONG).show();
            soundPool.play(soundError, 1, 1, 0, 0, 1);
            Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
            wrongInput =true;
        }
    }
}
