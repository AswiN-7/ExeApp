package com.example.exerciseminiproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;


public class MainActivity extends AppCompatActivity {
    private Button CatCow, Meditate, Bridge, Mode;
    private ImageView CatCowIv, MeditateIv, BridgeIv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);
        BridgeIv = findViewById(R.id.iv_bridge);
        CatCow = findViewById(R.id.btn_cat_n_cow);
        Meditate = findViewById(R.id.btn_meditate);
        Bridge = findViewById(R.id.btn_bridge);
        Mode = findViewById(R.id.btn_mode);


        CatCowIv = findViewById(R.id.iv_cat_n_cow);
        MeditateIv = findViewById(R.id.iv_meditate);
        Mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nightModeFlags = getBaseContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                switch (nightModeFlags) {
                    case Configuration.UI_MODE_NIGHT_YES:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        break;

                    case Configuration.UI_MODE_NIGHT_NO:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        break;
                }
            }
        });

        View.OnClickListener BridgeView = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BridgeActivity.class);
                startActivity(intent);
            }
        };

        View.OnClickListener MediateView = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MeditationActivity.class);
                startActivity(intent);
            }
        };

        View.OnClickListener CatNCowView = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CatNcowActivity.class);
                startActivity(intent);
            }
        };



        Bridge.setOnClickListener(BridgeView);
        BridgeIv.setOnClickListener(BridgeView);

        CatCow.setOnClickListener(CatNCowView);
        CatCowIv.setOnClickListener(CatNCowView);

        Meditate.setOnClickListener(MediateView);
        MeditateIv.setOnClickListener(MediateView);
    }
}