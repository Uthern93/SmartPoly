package com.android.smartpoly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NoticeActivity extends AppCompatActivity {
    FirebaseAuth auth;
    TextView usernameTxt;
    ImageButton settingBtn;
    FloatingActionButton chatbotBtn, addFab;
    RecyclerView recyclerView;
    ArrayList<DataClass> dataList;
    MyAdapter adapter;
    final private DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Notice Board");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.bottom_nav);
        // Set Selected Id
        bottomNav.setSelectedItemId(R.id.activity);
        Menu menu=bottomNav.getMenu();
        auth=FirebaseAuth.getInstance();
        settingBtn=(ImageButton) findViewById(R.id.setting);
        usernameTxt=(TextView)findViewById(R.id.username);
        chatbotBtn=(FloatingActionButton)findViewById(R.id.chatbotB);
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView) ;

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataList=new ArrayList<>();
        adapter=new MyAdapter(dataList, this);
        recyclerView.setAdapter(adapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DataClass dataClass=dataSnapshot.getValue(DataClass.class);
                    dataList.add(dataClass);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        chatbotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatbot = new Intent(NoticeActivity.this, MainActivity.class);
                startActivity(chatbot);
            }
        });

        Fade fade=new Fade();
        View decor=getWindow().getDecorView();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);

        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

        if (auth!=null)
        {
            String currentUser=auth.getCurrentUser().getEmail();
            usernameTxt.setText(currentUser);
        }
        setting();

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.introduction:
                        Intent intro = new Intent(NoticeActivity.this, Intro.class);
                        startActivity(intro);
                        finish();
                        break;

                    case R.id.activity:
                        break;


                    case R.id.staff:
                        Intent staff = new Intent(NoticeActivity.this, Staff.class);
                        startActivity(staff);
                        finish();
                        break;

                    case R.id.aboutus:
                        Intent aboutus = new Intent(NoticeActivity.this, AboutUs.class);
                        startActivity(aboutus);
                        finish();
                        break;
                }
                return true;
            }
        });
    }
    private void setting()
    {
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(NoticeActivity.this, SettingActivity.class);
                ActivityOptionsCompat options=ActivityOptionsCompat.makeSceneTransitionAnimation(NoticeActivity.this, settingBtn, ViewCompat.getTransitionName(settingBtn));
                startActivity(intent, options.toBundle());
            }
        });
    }

    }