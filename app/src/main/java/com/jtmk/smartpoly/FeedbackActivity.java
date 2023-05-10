package com.jtmk.smartpoly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.transition.Fade;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.smartpoly.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class FeedbackActivity extends AppCompatActivity {
    RatingBar star;
    EditText feedback;
    Button sendBtn;
    ImageButton backBtn;
    private FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        Fade fade=new Fade();
        View decor=getWindow().getDecorView();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);

        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

        star=(RatingBar) findViewById(R.id.star);
        auth=FirebaseAuth.getInstance();
        feedback=(EditText) findViewById(R.id.feedbackET);
        sendBtn=(Button) findViewById(R.id.sendBtn);
        backBtn=(ImageButton)findViewById(R.id.backBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (auth!=null) {
                    String email=auth.getCurrentUser().getEmail();
                    database= FirebaseDatabase.getInstance("https://smartpoly-69872-default-rtdb.asia-southeast1.firebasedatabase.app/");
                    reference=database.getReference("User");
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
                                feedbackHelper obj1=new feedbackHelper(star.getRating()+"/5.0", feedback.getText().toString());
                                database.getReference("User Feedback/"+ value).setValue(obj1);
                                Toast.makeText(getApplicationContext(), "Thank You For Your FeedBack!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getApplicationContext(), "Fail to get data.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}