package com.example.studentdormitoryfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class HostelLoginForm extends AppCompatActivity {
    private EditText editTextLoginEmail, editTextLoginPwd;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private static final String TAG = "HostelLoginForm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hostel_login_form);
        getSupportActionBar().setTitle("HostelLogin");

        editTextLoginEmail = findViewById(R.id.editText_login_email_hostel);
        editTextLoginPwd = findViewById(R.id.editText_login_pwd_hostel);
        progressBar = findViewById(R.id.progressBar);
        authProfile = FirebaseAuth.getInstance();
        ImageView imageViewShowHidePwd = findViewById(R.id.imageView_show_hide_pwd_hostel);
        imageViewShowHidePwd.setImageResource(R.drawable.ic_show_pwd);
        imageViewShowHidePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextLoginPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    //If password is visible then Hide it
                    editTextLoginPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //Change Icon
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_show_pwd);
                } else {
                    editTextLoginPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
                }
            }
        });
        Button buttonLogin = findViewById(R.id.button_login_hostel);
        buttonLogin.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textEmail = editTextLoginEmail.getText().toString();
                String textPwd = editTextLoginPwd.getText().toString();

                if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(HostelLoginForm.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Email is required");
                    editTextLoginEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(HostelLoginForm.this, "Please re-enter your email", Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Valid Email is required");
                    editTextLoginEmail.requestFocus();
                } else if (TextUtils.isEmpty(textPwd)) {
                    Toast.makeText(HostelLoginForm.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    editTextLoginPwd.setError("Email is required");
                    editTextLoginPwd.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(textEmail, textPwd);
                }
            }
        }));
    }
        private void loginUser(String email, String pwd) {
            authProfile.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(HostelLoginForm.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){

                        //Get instance of the current User
                        FirebaseUser firebaseUser = authProfile.getCurrentUser();

                        //Check if email is verified before user can access their profile
                        if (firebaseUser.isEmailVerified()){
                            Toast.makeText(HostelLoginForm.this, "You are logged in now", Toast.LENGTH_SHORT).show();

                            //Open User Profile
                            //Start the UserProfileActivity
                            startActivity(new Intent(HostelLoginForm.this, HostelUserProfileActivity.class));
                            finish();     //Close LoginActivity
                        } else {
                            firebaseUser.sendEmailVerification();
                            authProfile.signOut(); //Sign out user
                            showAlertDialog();
                        }
                    } else {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthInvalidUserException e) {
                            editTextLoginEmail.setError("User does not exists or is no longer valid. PLease register again.");
                            editTextLoginEmail.requestFocus();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            editTextLoginEmail.setError("Invalid credentials. Kindly, check and re-enter.");
                            editTextLoginEmail.requestFocus();
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                            Toast.makeText(HostelLoginForm.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(HostelLoginForm.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });

    }

    private void showAlertDialog() {
        //Setup Alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(HostelLoginForm.this);
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please verify your email now. You can not login without email verification");

        //Open Email Apps if User clicks/taps Continue button
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    //To email app in new window and not within our app
                startActivity(intent);
            }
        });

        //Create the AlertDialog
        AlertDialog alertDialog = builder.create();

        //Show the AlertDialog
        alertDialog.show();
    }

    //Check if User is already logged in. In such case, straightaway take the User to the User's Profile
    @Override
    protected void onStart() {
        super.onStart();
        if (authProfile.getCurrentUser() != null) {
            Toast.makeText(HostelLoginForm.this, "Already Logged In", Toast.LENGTH_SHORT).show();

            //Start the HostelUserProfileActivity
            startActivity(new Intent(HostelLoginForm.this, HostelUserProfileActivity.class));
            finish();       //Close LoginActivity
        }
        else {
            Toast.makeText(HostelLoginForm.this, "You can Login now", Toast.LENGTH_SHORT).show();

        }
    }

    }
