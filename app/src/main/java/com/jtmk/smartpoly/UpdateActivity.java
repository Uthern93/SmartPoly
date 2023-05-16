package com.jtmk.smartpoly;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.smartpoly.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UpdateActivity extends AppCompatActivity {

    private Button updateFab, cancelBtn2;
    private ImageView updateImg;
    private EditText UpdateDescTxt, UpdateTitleTxt, UpdateDateTxt, UpdateTimeTxt;
    private Dialog pd;
    private Uri uri;
    String title, desc, date, time, uploadTime, Utime;
    String imageUrl;
    String key, OldimageURL;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        updateFab=findViewById(R.id.updateBtn);
        cancelBtn2=findViewById(R.id.cancelBtn2);
        updateImg=findViewById(R.id.updateImg);
        UpdateDescTxt=findViewById(R.id.updateDesc);
        UpdateTitleTxt=findViewById(R.id.updateTitle);
        UpdateDateTxt=findViewById(R.id.DateUpdate);
        UpdateTimeTxt=findViewById(R.id.TimeUpdate);

        cancelBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UpdateActivity.this, Activities.class));
                finish();
            }
        });

        ActivityResultLauncher<Intent> activityResultLauncher=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data=result.getData();
                            uri=data.getData();
                            updateImg.setImageURI(uri);
                        } else {
                            Toast.makeText(UpdateActivity.this, "No Image Selected",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        Bundle bundle=getIntent().getExtras();
        if(bundle != null) {
            Glide.with(UpdateActivity.this).load(bundle.getString("image")).into(updateImg);
            UpdateDescTxt.setText(bundle.getString("description"));
            UpdateTitleTxt.setText(bundle.getString("title"));
            UpdateDateTxt.setText(bundle.getString("Edate"));
            UpdateTimeTxt.setText(bundle.getString("Etime"));
            time=bundle.getString("uploadTime");
            key=bundle.getString("key");
            OldimageURL=bundle.getString("image");
        }

        databaseReference= FirebaseDatabase.getInstance().getReference("Notice Board").child(key);

        updateImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker=new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        updateFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
                Intent intent=new Intent(UpdateActivity.this, Activities.class);
                startActivity(intent);
            }
        });

        // dialog for loading screen
        pd = new Dialog(UpdateActivity.this, android.R.style.Theme_Black);
        View view2 = LayoutInflater.from(getApplicationContext()).inflate(R.layout.progress, null);
        pd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pd.getWindow().setBackgroundDrawableResource(R.color.transparent);
        pd.setContentView(view2);
    }

    public void saveData() {
        storageReference= FirebaseStorage.getInstance().getReference().child("Notice Board Images").child(uri.getLastPathSegment());
        pd.show();

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri>uriTask=taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlImage=uriTask.getResult();
                imageUrl=urlImage.toString();
                updateData();
                pd.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
            }
        });
    }

    public void updateData() {
        title=UpdateTitleTxt.getText().toString();
        desc=UpdateDescTxt.getText().toString();
        time=UpdateTimeTxt.getText().toString();
        date=UpdateDateTxt.getText().toString();

        DataClass dataClass=new DataClass(imageUrl, title, desc, date, time, Utime);

        databaseReference.setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    StorageReference reference=FirebaseStorage.getInstance().getReferenceFromUrl(OldimageURL);
                    reference.delete();
                    Toast.makeText(UpdateActivity.this,"Updated", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}