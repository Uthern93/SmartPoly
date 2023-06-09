package com.jtmk.smartpoly;

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
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;

public class Staff extends AppCompatActivity {
    FirebaseAuth auth;
    TextView usernameTxt;
    ImageButton settingBtn;
    FloatingActionButton chatbotBtn;
    String myuid;
    RecyclerView recyclerView;
    List<ModelPost> posts;
    AdapterPost adapterPosts;
    SearchView search;
    FloatingActionButton blogBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff);


        BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.bottom_nav);
        // Set Selected Id
        bottomNav.setSelectedItemId(R.id.staff);
        Menu menu=bottomNav.getMenu();
        auth=FirebaseAuth.getInstance();
        settingBtn=(ImageButton) findViewById(R.id.setting);
        usernameTxt=(TextView)findViewById(R.id.username);
        chatbotBtn=(FloatingActionButton)findViewById(R.id.chatbotB);
        search=findViewById(R.id.search);
        search.bringToFront();
        recyclerView=findViewById(R.id.postrecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        posts = new ArrayList<>();
        loadPosts();

        blogBtn=findViewById(R.id.addBlogBtn);
        blogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Staff.this, AddDiscussionActivity.class));
                finish();
            }
        });

        chatbotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatbot = new Intent(Staff.this, MainActivity.class);
                startActivity(chatbot);
                overridePendingTransition(R.anim.from_right_in, R.anim.from_left_out);
                finish();
            }
        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchList(s);
                return true;
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
                        Intent intro = new Intent(Staff.this, Intro.class);
                        startActivity(intro);
                        overridePendingTransition(R.anim.from_right_in, R.anim.from_left_out);
                        finish();
                        break;

                    case R.id.activity:
                        Intent activity = new Intent(Staff.this, Activities.class);
                        startActivity(activity);
                        overridePendingTransition(R.anim.from_right_in, R.anim.from_left_out);
                        finish();
                        break;

                    case R.id.staff:
                        break;

                    case R.id.aboutus:
                        Intent aboutus = new Intent(Staff.this, AboutUs.class);
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
                Intent intent=new Intent(Staff.this, SettingActivity.class);
                ActivityOptionsCompat options=ActivityOptionsCompat.makeSceneTransitionAnimation(Staff.this, settingBtn, ViewCompat.getTransitionName(settingBtn));
                startActivity(intent, options.toBundle());
            }
        });
    }

    private void loadPosts() {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot1:snapshot.getChildren()){
                    ModelPost modelPost = dataSnapshot1.getValue(ModelPost.class);
                    posts.add(0, modelPost);
                    adapterPosts = new AdapterPost(Staff.this, posts);
                    recyclerView.setAdapter(adapterPosts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Staff.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void searchList(String text) {
        ArrayList<ModelPost> searchList2 = new ArrayList<>();
        for (ModelPost modelPost: posts) {
            if(modelPost.getTitle().toLowerCase().contains(text.toLowerCase())) {
                searchList2.add(modelPost);
            }
        }
        adapterPosts.searchDataList(searchList2);
    }
}