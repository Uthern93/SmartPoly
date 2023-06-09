package com.jtmk.smartpoly;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.smartpoly.R;
import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {
    private ProgressBar progressB;
    private TextView percentage, bottom, bottom2;
    Animation topAnim, bottomAnim;
    ImageView logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        // Below line to configure window to full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        progressB=(ProgressBar) findViewById(R.id.progress);
        percentage=(TextView) findViewById(R.id.percent);
        bottom=(TextView)findViewById(R.id.bottomtxt);
        bottom2=(TextView)findViewById(R.id.bottomtxt2);
        logo=(ImageView)findViewById(R.id.logo);

        // Animation
        topAnim= AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim= AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        bottom.setAnimation(bottomAnim);
        bottom2.setAnimation(bottomAnim);

        // Start lengthy operation in a background thread
        new Thread(new Runnable() {
            public void run() {
                doWork();
                startApp();
                finish();
            }
        }).start();

    }
    private void doWork() {
        for (int progress=0; progress<100; progress+=15) {
            try {
                percentage.setText(progress+"%"+"\nLoading...");
                Thread.sleep(500);
                progressB.setProgress(progress);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    private void startApp() {
        FirebaseAuth auth=FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // User is signed in (getCurrentUser() will be null if not signed in)
            Intent intent = new Intent(SplashScreen.this, Intro.class);
            startActivity(intent);
            finish();
        }else {
            Intent intent = new Intent(SplashScreen.this, AuthActivity.class);
            startActivity(intent);
        }
    }
}