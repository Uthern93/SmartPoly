package com.jtmk.smartpoly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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

import com.android.smartpoly.R;
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

public class Activities extends AppCompatActivity {
    FirebaseAuth auth;
    TextView usernameTxt;
    ImageButton settingBtn;
    FloatingActionButton chatbotBtn, addFab;
    RecyclerView recyclerView;
    ArrayList<DataClass> dataList;
    MyAdapter adapter;
    SearchView searchView;
    final private DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Notice Board");
    final private DatabaseReference adminRef= FirebaseDatabase.getInstance().getReference("User");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities);


        BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.bottom_nav);
        // Set Selected Id
        bottomNav.setSelectedItemId(R.id.activity);
        Menu menu=bottomNav.getMenu();
        auth=FirebaseAuth.getInstance();
        settingBtn=(ImageButton) findViewById(R.id.setting);
        usernameTxt=(TextView)findViewById(R.id.username);
        chatbotBtn=(FloatingActionButton)findViewById(R.id.chatbotB);
        addFab=(FloatingActionButton)findViewById(R.id.addBtn);
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView) ;
        searchView=findViewById(R.id.searching);
        searchView.bringToFront();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataList=new ArrayList<>();
        adapter=new MyAdapter(dataList, this);
        recyclerView.setAdapter(adapter);

        adminRef.child(auth.getCurrentUser().getUid()).child("isAdmin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean value=snapshot.getValue(Boolean.class);
                if (value.equals(true)) {
                    addFab.setVisibility(View.VISIBLE);
                    addFab.setEnabled(true);
                } else {
                    addFab.setVisibility(View.INVISIBLE);
                    addFab.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DataClass dataClass=dataSnapshot.getValue(DataClass.class);
                    dataClass.setKey(dataSnapshot.getKey());
                    dataList.add(dataClass);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });

        chatbotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatbot = new Intent(Activities.this, MainActivity.class);
                startActivity(chatbot);
                overridePendingTransition(R.anim.from_right_in, R.anim.from_left_out);
                finish();
            }
        });

        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Activities.this, UploadActivity.class));
                finish();
            }
        });

        Fade fade=new Fade();
        View decor=getWindow().getDecorView();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);

        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

        setting();

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.introduction:
                        Intent intro = new Intent(Activities.this, Intro.class);
                        startActivity(intro);
                        overridePendingTransition(R.anim.from_right_in, R.anim.from_left_out);
                        finish();
                        break;

                    case R.id.activity:
                        break;


                    case R.id.staff:
                        Intent staff = new Intent(Activities.this, Staff.class);
                        startActivity(staff);
                        overridePendingTransition(R.anim.from_right_in, R.anim.from_left_out);
                        finish();
                        break;

                    case R.id.aboutus:
                        Intent aboutus = new Intent(Activities.this, AboutUs.class);
                        startActivity(aboutus);
                        overridePendingTransition(R.anim.from_right_in, R.anim.from_left_out);
                        finish();
                        break;
                }
                return true;
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
                Intent intent=new Intent(Activities.this, SettingActivity.class);
                ActivityOptionsCompat options=ActivityOptionsCompat.makeSceneTransitionAnimation(Activities.this, settingBtn, ViewCompat.getTransitionName(settingBtn));
                startActivity(intent, options.toBundle());
            }
        });
    }
    public void searchList(String text) {
        ArrayList<DataClass> searchList=new ArrayList<>();
        for (DataClass dataClass: dataList) {
            if(dataClass.getTitle().toLowerCase().contains(text.toLowerCase())) {
                searchList.add(dataClass);
            }
        }
        adapter.searchDataList(searchList);
    }
}