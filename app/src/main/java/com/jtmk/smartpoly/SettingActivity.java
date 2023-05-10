package com.jtmk.smartpoly;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.android.smartpoly.R;
import com.google.firebase.auth.FirebaseAuth;

public class SettingActivity extends AppCompatActivity {

    Button signOut, feedbackBtn, close ,termsBtn, privacyBtn, forgotBtn, aboutBtn;
    FirebaseAuth auth;
    ImageButton backBtn;
    Switch darkMode;
    private AlertDialog.Builder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Fade fade=new Fade();
        View decor=getWindow().getDecorView();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);

        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

        signOut=(Button) findViewById(R.id.logoutBtn);
        feedbackBtn=(Button)findViewById(R.id.feedbackBtn);
        backBtn=(ImageButton)findViewById(R.id.backBtn) ;
        termsBtn=(Button)findViewById(R.id.termsBtn);
        privacyBtn=(Button)findViewById(R.id.privacyBtn);
        auth=FirebaseAuth.getInstance();
        darkMode=(Switch)findViewById(R.id.darkSwt);

        feedbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(auth!=null) {
                    Intent intent=new Intent(SettingActivity.this, FeedbackActivity.class);
                    ActivityOptionsCompat options=ActivityOptionsCompat.makeSceneTransitionAnimation(SettingActivity.this, feedbackBtn, ViewCompat.getTransitionName(feedbackBtn));
                    startActivity(intent, options.toBundle());
                }else {
                    Toast.makeText(SettingActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        aboutBtn=findViewById(R.id.aboutBtn);
        aboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(auth!=null) {
                    Intent intent=new Intent(SettingActivity.this, ContactUs.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(SettingActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        darkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });

        boolean isDarkModeOn=AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
        darkMode.setChecked(isDarkModeOn);

        logout();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        termsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewDialog();
            }
        });

        privacyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewDialog2();
            }
        });

    }
    private void logout()
    {
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(auth!=null) {
                    auth.signOut();
                    Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    Toast.makeText(SettingActivity.this, "User Successfully Logout", Toast.LENGTH_SHORT).show();

                    finish();
                }else {
                    Toast.makeText(SettingActivity.this, "Can't Logout User", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void createNewDialog()
    {
        dialogBuilder=new AlertDialog.Builder(this);
        final View contactPopupView=getLayoutInflater().inflate(R.layout.popup, null);

        close=(Button) contactPopupView.findViewById(R.id.closeBtn);

        dialogBuilder.setView(contactPopupView);
        AlertDialog dialog=dialogBuilder.create();
        dialog.show();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });
    }
    public void createNewDialog2()
    {
        dialogBuilder=new AlertDialog.Builder(this);
        final View contactPopupView=getLayoutInflater().inflate(R.layout.popup2, null);

        close=(Button) contactPopupView.findViewById(R.id.closeBtn2);

        dialogBuilder.setView(contactPopupView);
        AlertDialog dialog=dialogBuilder.create();
        dialog.show();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });
    }

}