package com.android.smartpoly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

import ng.max.slideview.SlideView;

public class Intro extends AppCompatActivity {
    FirebaseAuth auth;
    TextView usernameTxt, nameTxt;
    ImageButton settingBtn;
    FirebaseDatabase database;
    DatabaseReference reference;
    FloatingActionButton chatbotBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);



        BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.bottom_nav);
        // Set Selected Id
        bottomNav.setSelectedItemId(R.id.introduction);
        Menu menu=bottomNav.getMenu();

        auth=FirebaseAuth.getInstance();
        settingBtn=(ImageButton)findViewById(R.id.setting);
        usernameTxt=(TextView)findViewById(R.id.username);
        nameTxt=(TextView)findViewById(R.id.nameTxt);
        chatbotBtn=(FloatingActionButton)findViewById(R.id.chatbotB);

        chatbotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatbot = new Intent(Intro.this, MainActivity.class);
                startActivity(chatbot);
                overridePendingTransition(R.anim.from_right_in, R.anim.from_left_out);
                finish();
            }
        });

        Fade fade=new Fade();
        View decor=getWindow().getDecorView();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);

        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

        SlideView slideView = (SlideView) findViewById(R.id.slideView);
        slideView.setOnSlideCompleteListener(new SlideView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideView slideView) {
                // vibrate the device
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(100);

                // go to a new activity
                startActivity(new Intent(Intro.this, MainActivity.class));
            }
        });

        ImageSlider imgSlider=(ImageSlider)findViewById(R.id.slider1);
        ArrayList<SlideModel> slideModels=new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.e1, "JTMK POLIMAS", ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.e2, "Webinar MPCCSustAWARD 2023", ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.orgchart,"Carta Organisasi JTMK Polimas", ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.staffjtmk, "Pensyarah JTMK Polimas", ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.course,"Course Outline of JTMK", ScaleTypes.FIT));

        imgSlider.setImageList(slideModels, ScaleTypes.FIT);

        setting();

        if (auth!=null)
        {
            String currentUser=auth.getCurrentUser().getEmail();
            usernameTxt.setText(currentUser);
        }


        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.introduction:
                        break;

                    case R.id.activity:
                        if (auth.getCurrentUser().getEmail().equals("uthern4@gmail.com") || auth.getCurrentUser().getEmail().equals("smartpolyjtmk@gmail.com")) {
                            Intent activity = new Intent(Intro.this, Activities.class);
                            startActivity(activity);
                            overridePendingTransition(R.anim.from_right_in, R.anim.from_left_out);
                            finish();
                        } else {
                            Intent notice = new Intent(Intro.this, NoticeActivity.class);
                            startActivity(notice);
                            overridePendingTransition(R.anim.from_right_in, R.anim.from_left_out);
                            finish();
                        }

                        break;

                    case R.id.staff:
                        Intent staff = new Intent(Intro.this, Staff.class);
                        startActivity(staff);
                        overridePendingTransition(R.anim.from_right_in, R.anim.from_left_out);
                        finish();
                        break;

                    case R.id.aboutus:
                        Intent aboutus = new Intent(Intro.this, AboutUs.class);
                        startActivity(aboutus);
                        overridePendingTransition(R.anim.from_right_in, R.anim.from_left_out);
                        finish();
                        break;
                }
                return true;
            }
        });

        database=FirebaseDatabase.getInstance("https://smartpoly-69872-default-rtdb.asia-southeast1.firebasedatabase.app/");
        reference=database.getReference("User");
        // Define the keyword to search for
        String email=auth.getCurrentUser().getEmail();
        // Query for users whose email contains the keyword
        Query query = reference.orderByChild("email").startAt(email).endAt(email + "\uf8ff");
        // without query the database will fetch default first data
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    //getKey() = The key name for the source location of this snapshot
                    String userKey = childSnapshot.getKey();
                    String value = childSnapshot.child("name").getValue(String.class);
                    nameTxt.setText(value);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
                }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.from_left_in, R.anim.from_right_out);
    }


    private void setting()
    {
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(auth!=null) {
                    Intent intent=new Intent(Intro.this, SettingActivity.class);
                    ActivityOptionsCompat options=ActivityOptionsCompat.makeSceneTransitionAnimation(Intro.this, settingBtn, ViewCompat.getTransitionName(settingBtn));
                    startActivity(intent, options.toBundle());
                }else {
                    Toast.makeText(Intro.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
