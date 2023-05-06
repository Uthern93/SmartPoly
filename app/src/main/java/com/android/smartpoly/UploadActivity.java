package com.android.smartpoly;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UploadActivity extends AppCompatActivity {

    private Button uploadFab;
    private ImageView uploadImg;
    private EditText descTxt, titleTxt, dateTxt, timeTxt;
    private ProgressBar uploadPB;
    private Uri imageUri;
    String imageURL;
    private Notification.Builder builder;
    final private DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Notice Board");
    final private StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Notice Board Images");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        uploadFab=(Button) findViewById(R.id.uploadBtn);
        uploadImg=(ImageView) findViewById(R.id.uploadImg);
        descTxt=(EditText) findViewById(R.id.txtDesc);
        titleTxt=(EditText) findViewById(R.id.txtTitle);
        dateTxt=(EditText) findViewById(R.id.DateTxt);
        timeTxt=(EditText) findViewById(R.id.TimeTxt);


        ActivityResultLauncher<Intent> activityResultLauncher=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode()== Activity.RESULT_OK) {
                            Intent data=result.getData();
                            imageUri=data.getData();
                            uploadImg.setImageURI(imageUri);
                        } else {
                            Toast.makeText(UploadActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker=new Intent();
                photoPicker.setAction(Intent.ACTION_GET_CONTENT);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        uploadFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageUri != null) {
                    saveData();
                } else {
                    Toast.makeText(UploadActivity.this, "Please Select An Image to be Uploaded", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void saveData() {
        StorageReference storageRef= storageReference.child(imageUri.getLastPathSegment());

        AlertDialog.Builder builder=new AlertDialog.Builder(UploadActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress);
        AlertDialog dialog=builder.create();
        dialog.show();

        storageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri>uriTask=taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlImage=uriTask.getResult();
                imageURL=urlImage.toString();
                uploadData();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
            }
        });
    }

    public void uploadData() {
        String title=titleTxt.getText().toString();
        String caption=descTxt.getText().toString();
        String Edate=dateTxt.getText().toString();
        String Etime=timeTxt.getText().toString();
        // Declaring variable for Date and Time Format
        SimpleDateFormat Dtime=new SimpleDateFormat("h:mm:ss");
        SimpleDateFormat Ddate=new SimpleDateFormat("dd-MM-yyy");
        String date = Dtime.format(Calendar.getInstance().getTime());
        String Hrs = Ddate.format(Calendar.getInstance().getTime());
        String time= "Date Uploaded : " + Hrs +"\nTime Uploaded : "+ date;

        DataClass dataClass=new DataClass(imageURL, title, caption, Edate, Etime, time);

        databaseReference.child(title).setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                startActivity(new Intent(getApplicationContext(), Activities.class));
                Toast.makeText(UploadActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String getFileExtension(Uri fileUri) {
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }
}