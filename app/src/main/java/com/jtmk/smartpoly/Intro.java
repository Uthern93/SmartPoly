package com.jtmk.smartpoly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Vibrator;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.smartpoly.R;
import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import ng.max.slideview.SlideView;

public class Intro extends AppCompatActivity {
    FirebaseAuth auth;
    TextView usernameTxt, nameTxt, usernameCard, emailCard, uidCard;
    ImageButton settingBtn;
    FirebaseDatabase database;
    DatabaseReference reference;
    FloatingActionButton chatbotBtn;
    Button editBtn;
    ImageView userImg;
    private AppUpdateManager appUpdateManager;
    private static final int IMMEDIATE_APP_UPDATE_REQ_CODE = 124;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);



        BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.bottom_nav);
        // Set Selected Id
        bottomNav.setSelectedItemId(R.id.introduction);
        Menu menu=bottomNav.getMenu();

        appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
        checkUpdate();

        auth=FirebaseAuth.getInstance();
        settingBtn=(ImageButton)findViewById(R.id.setting);
        usernameTxt=(TextView)findViewById(R.id.username);
        nameTxt=(TextView)findViewById(R.id.nameTxt);
        chatbotBtn=(FloatingActionButton)findViewById(R.id.chatbotB);
        editBtn=(Button)findViewById(R.id.editBtn);
        userImg=findViewById(R.id.profileImg);

        usernameCard=findViewById(R.id.usernameCard);
        emailCard=findViewById(R.id.emailCard);
        uidCard=findViewById(R.id.uidCard);

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent edit = new Intent(Intro.this, editProfile.class);
                startActivity(edit);
                overridePendingTransition(R.anim.from_right_in, R.anim.from_left_out);
                finish();
            }
        });

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

        FirebaseDatabase.getInstance().getReference().child("Notice Board").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for(DataSnapshot data: snapshot.getChildren())
                {
                    slideModels.add(0, new SlideModel(data.child("imageURL").getValue().toString(), data.child("title").getValue().toString(), ScaleTypes.FIT));

                    imgSlider.setImageList(slideModels, ScaleTypes.FIT);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        setting();


        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.introduction:
                        break;

                    case R.id.activity:
                            Intent activity = new Intent(Intro.this, Activities.class);
                            startActivity(activity);
                            overridePendingTransition(R.anim.from_right_in, R.anim.from_left_out);
                            finish();
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
                    // name column
                    String value = childSnapshot.child("name").getValue(String.class);
                    nameTxt.setText(value);
                    // username column
                    String value2 = childSnapshot.child("username").getValue(String.class);
                    usernameCard.setText(value2);
                    // email column
                    String value3 = childSnapshot.child("email").getValue(String.class);
                    emailCard.setText("email : "+value3);
                    // email column
                    String value4 = childSnapshot.child("uid").getValue(String.class);
                    uidCard.setText("uid : "+value4);
                    // user image column
                    String value5 = childSnapshot.child("userImg").getValue(String.class);
                    Glide.with(getApplicationContext()).load(value5).into(userImg);
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

    private void checkUpdate() {

        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                startUpdateFlow(appUpdateInfo);
            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS){
                startUpdateFlow(appUpdateInfo);
            }
        });
    }
    private void startUpdateFlow(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this, IMMEDIATE_APP_UPDATE_REQ_CODE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMMEDIATE_APP_UPDATE_REQ_CODE) {
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Update canceled by user! Result Code: " + resultCode, Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Update success! Result Code: " + resultCode, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Update Failed! Result Code: " + resultCode, Toast.LENGTH_LONG).show();
                checkUpdate();
            }
        }
    }

}
