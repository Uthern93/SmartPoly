package com.jtmk.smartpoly;

import android.app.AlertDialog;
import android.app.Dialog;
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
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpFragment extends Fragment {
    private EditText email, password, name, username;
    private Button register, close;
    private FirebaseAuth auth;
    private ProgressBar pBar;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private TextView terms, policy;
    FirebaseDatabase database;
    DatabaseReference reference;
    View view;
    Dialog pd;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignUpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignUpFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignUpFragment newInstance(String param1, String param2) {
        SignUpFragment fragment = new SignUpFragment();
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

    private void SendEmailVerification() {
        FirebaseUser user=auth.getCurrentUser();

        if(user!= null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(getContext(), "Registration Successful, We've sent you an email. Please Check and verify your account.", Toast.LENGTH_SHORT).show();
                        auth.signOut();
                    }
                    else {
                        String error=task.getException().getMessage();
                        Toast.makeText(getContext(), "Error :" + error, Toast.LENGTH_SHORT).show();
                        auth.signOut();
                    }
                }
            });
        }
    }

    private void checkUserExists()
    {
        try
        {
            if(auth!=null && !email.getText().toString().isEmpty() && !password.getText().toString().isEmpty() && !name.getText().toString().isEmpty() && !username.getText().toString().isEmpty())
            {
                if(isValidEmail(email.getText().toString())) {
                    auth.fetchSignInMethodsForEmail(email.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                    boolean checkResult=!task.getResult().getSignInMethods().isEmpty();

                                    if(!checkResult)
                                    {
                                        createUser();

                                    }
                                    else {

                                        Toast.makeText(getContext(), "The email or user already been created", Toast.LENGTH_SHORT).show();
                                        pd.dismiss();

                                        register.setEnabled(true);
                                    }

                                }
                            });
                } else {
                    pd.dismiss();

                    register.setEnabled(true);
                    Toast.makeText(getContext(), "The email address is badly formatted, Please Enter Correct Email address", Toast.LENGTH_SHORT).show();
                }


            } else {
                email.setError("Please Insert Your Email");
                password.setError("Please Insert Your Password");
                name.setError("Please Insert Your Name");
                username.setError("Please Insert Your Username");
            }

        }
        catch (Exception e)
        {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
    public static boolean isValidEmail(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    private void createUser()
    {
                String txtEmail=email.getText().toString().toLowerCase();
                String txtPassword=password.getText().toString();
                String txtName=name.getText().toString();
                String txtUsername=username.getText().toString();

                Pattern specailCharPatten = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
                Pattern UpperCasePatten = Pattern.compile("[A-Z ]");
                Pattern lowerCasePatten = Pattern.compile("[a-z ]");
                Pattern digitCasePatten = Pattern.compile("[0-9 ]");

                if(TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtPassword) || TextUtils.isEmpty(txtName) || TextUtils.isEmpty(txtUsername)) {
                    Toast.makeText(getContext(), "Empty Credential !", Toast.LENGTH_SHORT).show();
                    email.setError("Please Insert Your Email");
                    password.setError("Please Insert Your Password");
                    name.setError("Please Insert Your Name");
                    username.setError("Please Insert Your Username");

                } else if (txtPassword.length()<8) {
                    Toast.makeText(getContext(), "Password too short!", Toast.LENGTH_SHORT).show();
                    password.setError("Please Ensure The Password is more than 8 characters");
                } else if (!specailCharPatten.matcher(txtPassword).find()){
                    Toast.makeText(getContext(), "Password doesn't contain special character", Toast.LENGTH_SHORT).show();
                    password.setError("Password must have at least one special character");
                }else if (!UpperCasePatten.matcher(txtPassword).find()){
                    Toast.makeText(getContext(), "Password has no uppercase character", Toast.LENGTH_SHORT).show();
                    password.setError("Password must have at least one uppercase character");
                }else if (!digitCasePatten.matcher(txtPassword).find()){
                    Toast.makeText(getContext(), "Password has no digit character", Toast.LENGTH_SHORT).show();
                    password.setError("Password must have at least one digit character");
                }else {
                    pd.show();
                    // disable it so that user cant click on it when creating a user
                    register.setEnabled(false);

                    auth.createUserWithEmailAndPassword(txtEmail,txtPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {

                                    pd.show();
                                    //enable the button again after successfully create new user
                                    register.setEnabled(true);
                                    email.setText("");
                                    password.setText("");
                                    name.setText("");
                                    username.setText("");

                                    if(auth.getCurrentUser()!=null)
                                    {
                                        String uid=auth.getCurrentUser().getUid();
                                        Boolean notAdmin=false;
                                        String userImg="";
                                        database=FirebaseDatabase.getInstance("https://smartpoly-69872-default-rtdb.asia-southeast1.firebasedatabase.app/");
                                        reference=database.getReference("User");

                                        HelperClass helperClass=new HelperClass(txtName, txtEmail,txtUsername, txtPassword, uid, notAdmin, userImg);
                                        database.getReference("User/"+ uid).setValue(helperClass);

                                        SendEmailVerification();
                                    }

                                    pd.dismiss();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    pd.dismiss();

                                    register.setEnabled(true);
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }

    }
    public void createNewDialog()
    {
        dialogBuilder=new AlertDialog.Builder(getContext());
        final View contactPopupView=getLayoutInflater().inflate(R.layout.popup, null);

        close=(Button) contactPopupView.findViewById(R.id.closeBtn);

        dialogBuilder.setView(contactPopupView);
        dialog=dialogBuilder.create();
        dialog.show();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });
    }
    public void createNewDialog2()
    {
        dialogBuilder=new AlertDialog.Builder(getContext());
        final View contactPopupView=getLayoutInflater().inflate(R.layout.popup2, null);

        close=(Button) contactPopupView.findViewById(R.id.closeBtn2);

        dialogBuilder.setView(contactPopupView);
        dialog=dialogBuilder.create();
        dialog.show();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        email=(EditText) view.findViewById(R.id.email_signup);
        password=(EditText) view.findViewById(R.id.password_signup);
        name=(EditText)view.findViewById(R.id.name_signup);
        username=(EditText)view.findViewById(R.id.username_signup);
        register=(Button) view.findViewById(R.id.signupBtn);

        // dialog for loading screen
        pd = new Dialog(getContext(), android.R.style.Theme_Black);
        View view2 = LayoutInflater.from(getContext()).inflate(R.layout.progress, null);
        pd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pd.getWindow().setBackgroundDrawableResource(R.color.transparent);
        pd.setContentView(view2);

        policy=(TextView)view.findViewById(R.id.policy);
        terms=(TextView)view.findViewById(R.id.terms);

        auth=FirebaseAuth.getInstance();


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtEmail=email.getText().toString();
                String txtPassword=password.getText().toString();
                String txtName=name.getText().toString();
                String txtUsername=username.getText().toString();

                checkUserExists();

            }
        });

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewDialog();

            }
        });
        policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createNewDialog2();
            }
        });

        return view;

    }
}