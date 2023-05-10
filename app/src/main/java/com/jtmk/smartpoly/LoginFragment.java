package com.jtmk.smartpoly;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.smartpoly.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    EditText emailET, emailForgot;
    EditText passwordET;
    TextView forgot;
    Button loginBtn, close, Done;
    View objSignIn;
    FirebaseAuth auth;
    ProgressBar progressBar;
    FirebaseDatabase database;
    DatabaseReference reference;
    // Custom Loading screen
    AlertDialog.Builder builder2;
    Dialog pd;

    private Boolean emailChecker;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
    }
    private void VerifyEmail() {
        FirebaseUser user=auth.getCurrentUser();
        emailChecker=user.isEmailVerified();

        if(emailChecker) {

            Intent success=new Intent(getActivity().getApplicationContext(), Intro.class);
            success.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(success);


            pd.dismiss();

            loginBtn.setEnabled(true);
            getActivity().finish();
            Toast.makeText(getContext(), "Welcome to SmartPoly App", Toast.LENGTH_SHORT).show();



        } else {
            Toast.makeText(getContext(), "Please Verify Your Email", Toast.LENGTH_SHORT).show();
            auth.signOut();

            pd.dismiss();

            loginBtn.setEnabled(true);
        }
    }

    private void initializeVar()
    {
        try
            {
                auth=FirebaseAuth.getInstance();
                emailET=(EditText) objSignIn.findViewById(R.id.txtEmail);
                passwordET=(EditText) objSignIn.findViewById(R.id.txtPassword);
                loginBtn=(Button)objSignIn.findViewById(R.id.loginBtn);

                loginBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LoginUser();

                    }
                });
            }
            catch(Exception e)
            {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }

        private void LoginUser()
        {
            try {
                if(!emailET.getText().toString().isEmpty() && !passwordET.getText().toString().isEmpty())
                {
                    if(auth!=null)
                    {

                        pd.show();

                        loginBtn.setEnabled(false);

                        auth.signInWithEmailAndPassword(emailET.getText().toString().toLowerCase(), passwordET.getText().toString())
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {

                                        database=FirebaseDatabase.getInstance("https://smartpoly-69872-default-rtdb.asia-southeast1.firebasedatabase.app/");
                                        reference=database.getReference("User");
                                        // Define the keyword to search for
                                        String email=auth.getCurrentUser().getEmail();
                                        // Query for users whose email contains the keyword
                                        Query query = reference.orderByChild("email").startAt(email).endAt(email + "\uf8ff");
                                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                // Loop through the query result and update the users
                                                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                                    //getKey() = The key name for the source location of this snapshot
                                                    String userKey = childSnapshot.getKey();
                                                    DatabaseReference userRef = reference.child(userKey);
                                                    // Update the password field to new value
                                                    userRef.child("password").setValue(passwordET.getText().toString());
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(getContext(), "Error updating users: " + error.getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        });


                                        VerifyEmail();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        pd.dismiss();

                                        loginBtn.setEnabled(true);
                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                });
                    }

                }
                else
                {
                    Toast.makeText(getContext(), "Please fill both fields to Login User", Toast.LENGTH_SHORT).show();
                    emailET.setError("Please fill in your email address");
                    passwordET.setError("Please fill in your password");
                }

            }
            catch (Exception e)
            {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        objSignIn= inflater.inflate(R.layout.fragment_login, container, false);
        forgot=(TextView) objSignIn.findViewById(R.id.forgotTxt);

        // dialog for loading screen
        pd = new Dialog(getContext(), android.R.style.Theme_Black);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.progress, null);
        pd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pd.getWindow().setBackgroundDrawableResource(R.color.transparent);
        pd.setContentView(view);

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                View dialogView=getLayoutInflater().inflate(R.layout.popup_forgot, null);
                auth=FirebaseAuth.getInstance();
                emailForgot=(EditText) dialogView.findViewById(R.id.email_forgot);

                builder.setView(dialogView);
                AlertDialog dialog=builder.create();
                dialog.show();

                dialogView.findViewById(R.id.forgotBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String userEmail=emailForgot.getText().toString();

                        if (TextUtils.isEmpty(userEmail) && !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches())
                        {
                            Toast.makeText(getActivity().getApplicationContext(), "Please Enter your registered email id", Toast.LENGTH_SHORT).show();
                            emailForgot.setError("Please Insert Your Registered Email");
                            return;
                        }
                        auth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(getContext(), "Check Your Email Address", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(getContext(), "Unable to send, Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                dialogView.findViewById(R.id.closeBtn3).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

            }
        });

        initializeVar();

        return objSignIn;
    }


}