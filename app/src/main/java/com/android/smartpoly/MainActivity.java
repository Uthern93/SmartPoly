package com.android.smartpoly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.*;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import okhttp3.Request;
import okio.Timeout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    TextView usernameTxt;
    ImageButton settingBtn;
    FloatingActionButton chatbotBtn;
    private RecyclerView chatsRV;
    private ImageButton sendMsgIB;
    private EditText userMsgEdt;
    private final String USER_KEY = "user";
    private final String BOT_KEY = "bot";

    // creating a variable for
    // our volley request queue.
    private RequestQueue mRequestQueue;

    // creating a variable for array list and adapter class.
    private ArrayList<ChatModal> chatModalArrayList;
    private MessageRVAdapter messageRVAdapter;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.bottom_nav);
        // Set Selected Id
        bottomNav.setSelectedItemId(R.id.chatbot);
        Menu menu=bottomNav.getMenu();
        auth=FirebaseAuth.getInstance();
        settingBtn=(ImageButton) findViewById(R.id.setting);
        usernameTxt=(TextView)findViewById(R.id.username);

        // on below line we are initializing all our views.
        chatsRV = findViewById(R.id.idRVChats);
        sendMsgIB = findViewById(R.id.idIBSend);
        userMsgEdt = findViewById(R.id.idEdtMessage);
        chatbotBtn=(FloatingActionButton)findViewById(R.id.chatbotB);

        chatbotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatbot = new Intent(MainActivity.this, MainActivity.class);
                startActivity(chatbot);
                overridePendingTransition(R.anim.from_right_in, R.anim.from_left_out);
            }
        });

        Fade fade=new Fade();
        View decor=getWindow().getDecorView();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);

        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

        // below line is to initialize our request queue.
        mRequestQueue = Volley.newRequestQueue(MainActivity.this);
        mRequestQueue.getCache().clear();

        // creating a new array list
        chatModalArrayList = new ArrayList<>();

        if (auth!=null)
        {
            String currentUser=auth.getCurrentUser().getEmail();
            usernameTxt.setText(currentUser);
        }
        setting();

        // on below line we are initializing our adapter class and passing our array list to it.
        messageRVAdapter = new MessageRVAdapter(chatModalArrayList, this);

        // below line we are creating a variable for our linear layout manager.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);

        // below line is to set layout
        // manager to our recycler view.
        chatsRV.setLayoutManager(linearLayoutManager);

        // below line we are setting
        // adapter to our recycler view.
        chatsRV.setAdapter(messageRVAdapter);

        startingMsg();

        // adding on click listener for send message button.
        sendMsgIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking if the message entered
                // by user is empty or not.
                if (userMsgEdt.getText().toString().isEmpty()) {
                    // if the edit text is empty display a toast message.
                    Toast.makeText(MainActivity.this, "Please enter your message..", Toast.LENGTH_SHORT).show();
                    return;
                }

                // calling a method to send message
                // to our bot to get response.
                getResponse(userMsgEdt.getText().toString());

                //Insert the message into firebase history database
                database=FirebaseDatabase.getInstance("https://smartpoly-69872-default-rtdb.asia-southeast1.firebasedatabase.app/");
                reference=database.getReference("Message History");
                reference.push().setValue(userMsgEdt.getText().toString());

                // below line we are setting text in our edit text as empty
                userMsgEdt.setText("");
            }
        });


        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.introduction:
                        Intent intro = new Intent(MainActivity.this, Intro.class);
                        startActivity(intro);
                        overridePendingTransition(R.anim.from_right_in, R.anim.from_left_out);
                        finish();
                        break;

                    case R.id.activity:
                        if (auth.getCurrentUser().getEmail().equals("uthern4@gmail.com") || auth.getCurrentUser().getEmail().equals("smartpolyjtmk@gmail.com")) {
                            Intent activity = new Intent(MainActivity.this, Activities.class);
                            startActivity(activity);
                            overridePendingTransition(R.anim.from_right_in, R.anim.from_left_out);
                            finish();
                        } else {
                            Intent notice = new Intent(MainActivity.this, NoticeActivity.class);
                            startActivity(notice);
                            overridePendingTransition(R.anim.from_right_in, R.anim.from_left_out);
                            finish();
                        }
                        break;

                    case R.id.staff:
                        Intent staff = new Intent(MainActivity.this, Staff.class);
                        startActivity(staff);
                        overridePendingTransition(R.anim.from_right_in, R.anim.from_left_out);
                        finish();
                        break;

                    case R.id.aboutus:
                        Intent aboutus = new Intent(MainActivity.this, AboutUs.class);
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
                Intent intent=new Intent(MainActivity.this, SettingActivity.class);
                ActivityOptionsCompat options=ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, settingBtn, ViewCompat.getTransitionName(settingBtn));
                startActivity(intent, options.toBundle());
            }
        });
    }

    private void getResponse (String message) {
        chatModalArrayList.add(new ChatModal(message, USER_KEY));
        messageRVAdapter.notifyDataSetChanged();
        String url= "http://api.brainshop.ai/get?bid=174120&key=KwxcDibecKUNMRyr&uid=[uid]&msg="+message;
        String BASE_URL="http://api.brainshop.ai/";
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetroAPI retrofitAPI=retrofit.create(RetroAPI.class);
        Call<MsgModal> call=retrofitAPI.getMessage(url);
        call.enqueue(new Callback<MsgModal>() {
            @Override
            public void onResponse(Call<MsgModal> call, Response<MsgModal> response) {
                if(response.isSuccessful()) {
                    MsgModal modal=response.body();
                    chatModalArrayList.add(new ChatModal(modal.getCnt(), BOT_KEY));
                    messageRVAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<MsgModal> call, Throwable t) {
                chatModalArrayList.add(new ChatModal("Please revert your question", BOT_KEY));
                messageRVAdapter.notifyDataSetChanged();
            }
        });
    }
    private void startingMsg() {
        String url= "http://api.brainshop.ai/get?bid=174120&key=KwxcDibecKUNMRyr&uid=[uid]&msg=myRequest sayHello";
        String BASE_URL="http://api.brainshop.ai/";
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetroAPI retrofitAPI=retrofit.create(RetroAPI.class);
        Call<MsgModal> call=retrofitAPI.getMessage(url);
        call.enqueue(new Callback<MsgModal>() {
            @Override
            public void onResponse(Call<MsgModal> call, Response<MsgModal> response) {
                MsgModal modal=response.body();
                chatModalArrayList.add(new ChatModal(modal.getCnt(), BOT_KEY));
                messageRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<MsgModal> call, Throwable t) {
                chatModalArrayList.add(new ChatModal("Please revert your question", BOT_KEY));
                messageRVAdapter.notifyDataSetChanged();
            }
        });
    }
    }