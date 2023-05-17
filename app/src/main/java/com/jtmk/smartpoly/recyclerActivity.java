package com.jtmk.smartpoly;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.smartpoly.R;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class recyclerActivity extends AppCompatActivity {

    final private DatabaseReference adminRef= FirebaseDatabase.getInstance().getReference("User");
    FirebaseAuth auth;
    TextView title;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_item);

        FloatingActionMenu FAMbtn=(FloatingActionMenu) findViewById(R.id.FAMbtn);
        FloatingActionButton deleteBtn=findViewById(R.id.deleteBtn);
        FloatingActionButton editBtn=findViewById(R.id.editBtn);
        title=findViewById(R.id.recyclerTitle);
        auth=FirebaseAuth.getInstance();


        adminRef.child(auth.getCurrentUser().getUid()).child("isAdmin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean value=snapshot.getValue(Boolean.class);
                if (value.equals(true)) {
                    FAMbtn.setVisibility(View.VISIBLE);
                    deleteBtn.setVisibility(View.VISIBLE);
                    FAMbtn.setEnabled(true);
                    deleteBtn.setEnabled(true);
                    editBtn.setVisibility(View.VISIBLE);
                    editBtn.setEnabled(true);
                } else {
                    FAMbtn.setMenuButtonColorNormal(android.R.color.transparent);
                    deleteBtn.setColorNormal(android.R.color.transparent);
                    FAMbtn.setEnabled(false);
                    deleteBtn.setEnabled(false);
                    editBtn.setColorNormal(android.R.color.transparent);
                    editBtn.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}
