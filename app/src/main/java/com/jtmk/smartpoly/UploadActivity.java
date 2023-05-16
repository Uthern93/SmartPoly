package com.jtmk.smartpoly;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.smartpoly.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UploadActivity extends AppCompatActivity {

    private Button uploadFab, cancelBtn;
    private ImageView uploadImg;
    private EditText descTxt, titleTxt, dateTxt, timeTxt;
    private Dialog pd;
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
        cancelBtn=(Button)findViewById(R.id.cancelBtn);

        // dialog for loading screen
        pd = new Dialog(UploadActivity.this, android.R.style.Theme_Black);
        View view2 = LayoutInflater.from(getApplicationContext()).inflate(R.layout.progress, null);
        pd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pd.getWindow().setBackgroundDrawableResource(R.color.transparent);
        pd.setContentView(view2);


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

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UploadActivity.this, Activities.class));
                finish();
            }
        });

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

        pd.show();

        storageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri>uriTask=taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlImage=uriTask.getResult();
                imageURL=urlImage.toString();
                uploadData();

                pd.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
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

        String currentDate= DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        databaseReference.child(currentDate).setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
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