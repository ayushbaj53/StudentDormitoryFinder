package com.example.studentdormitoryfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class ChangePasswordActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private EditText editTextPwdCurr, editTextPwdNew, editTextPwdConfirmNew;
    private TextView textViewAuthenticated;
    private Button buttonChangePwd, buttonReAuthenticate;
    private ProgressBar progressBar;
    private String userPwdCurr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Change Password");

        editTextPwdNew = findViewById(R.id.editText_change_pwd_new);
        editTextPwdCurr = findViewById(R.id.editText_change_pwd_current);
        editTextPwdConfirmNew = findViewById(R.id.editText_change_pwd_new_confirm);
        textViewAuthenticated = findViewById(R.id.textView_change_pwd_authenticated);
        progressBar = findViewById(R.id.progressBar);
        buttonReAuthenticate = findViewById(R.id.button_change_pwd_authenticate);
        buttonChangePwd = findViewById(R.id.button_change_pwd);

        //Disable editText for New Password, Confirm new Password and make changes pwd button unclickable till user is authenticated
        editTextPwdNew.setEnabled(false);
        editTextPwdConfirmNew.setEnabled(false);
        buttonChangePwd.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        assert firebaseUser != null;
        if (firebaseUser.equals("")) {
            Toast.makeText(ChangePasswordActivity.this, "Something went wrong! User's details not available", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ChangePasswordActivity.this, UserProfileActivity.class);
            startActivity(intent);
            finish();
        }else {
            reAuthenticateUser(firebaseUser);
        }
    }

    //ReAuthenticate User Before changing password
    @SuppressLint("SetTextI18n")
    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        buttonReAuthenticate.setOnClickListener(view -> {
            userPwdCurr = editTextPwdCurr.getText().toString();

            if (TextUtils.isEmpty(userPwdCurr)){
                Toast.makeText(ChangePasswordActivity.this, "Password is needed", Toast.LENGTH_SHORT).show();
                editTextPwdCurr.setError("Please enter your current password to authenticate");
                editTextPwdCurr.requestFocus();
            }else {
                progressBar.setVisibility(View.VISIBLE);

                //ReAuthenticate User now
                AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(firebaseUser.getEmail()), userPwdCurr);

                firebaseUser.reauthenticate(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        progressBar.setVisibility(View.GONE);

                        //Disable editText for Current Password. Enable EditText for new Password and Confirm New Password
                        editTextPwdCurr.setEnabled(false);
                        editTextPwdNew.setEnabled(true);
                        editTextPwdConfirmNew.setEnabled(true);

                        //Enable Change Pwd Button. Disable Authenticate Button
                        buttonReAuthenticate.setEnabled(false);
                        buttonChangePwd.setEnabled(true);

                        //Set TextView to show User is authenticated/verified
                        textViewAuthenticated.setText("You are authenticated/verified." + "You can change password now!");
                        Toast.makeText(ChangePasswordActivity.this, "Password has been verified." + "Change Password Now", Toast.LENGTH_SHORT).show();

                        //Update color of Change Password Button
                        buttonChangePwd.setBackgroundTintList(ContextCompat.getColorStateList(ChangePasswordActivity.this, R.color.dark_green));

                        buttonChangePwd.setOnClickListener(view1 -> changePwd(firebaseUser));
                    }else {
                        try {
                            throw Objects.requireNonNull(task.getException());
                        }catch (Exception e) {
                            Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                }
                    progressBar.setVisibility(View.GONE);

                });
            }
        });
    }

    private void changePwd(FirebaseUser firebaseUser) {
        String userPwdNew = editTextPwdNew.getText().toString();
        String userPwdConfirmNew = editTextPwdConfirmNew.getText().toString();

        if (TextUtils.isEmpty(userPwdNew)) {
            Toast.makeText(ChangePasswordActivity.this, "New Password is needed", Toast.LENGTH_SHORT).show();
            editTextPwdNew.setError("Please Enter your new Password");
            editTextPwdNew.requestFocus();
        } else if (TextUtils.isEmpty(userPwdConfirmNew)) {
            Toast.makeText(ChangePasswordActivity.this, "Please Confirm you new Password", Toast.LENGTH_SHORT).show();
            editTextPwdConfirmNew.setError("Please re-enter your new Password");
            editTextPwdConfirmNew.requestFocus();
        } else if (!userPwdNew.matches(userPwdConfirmNew)) {
            Toast.makeText(ChangePasswordActivity.this, "Password did not match", Toast.LENGTH_SHORT).show();
            editTextPwdConfirmNew.setError("Please re-enter same Password");
            editTextPwdConfirmNew.requestFocus();
        } else if (userPwdCurr.matches(userPwdNew)) {
            Toast.makeText(ChangePasswordActivity.this, "New Password cannot be same as old password", Toast.LENGTH_SHORT).show();
            editTextPwdNew.setError("Please enter a new Password");
            editTextPwdNew.requestFocus();
        } else {
            progressBar.setVisibility(View.VISIBLE);

            firebaseUser.updatePassword(userPwdNew).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(ChangePasswordActivity.this, "Password has been Changed", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ChangePasswordActivity.this, UserProfileActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        throw Objects.requireNonNull(task.getException());
                    }catch (Exception e){
                        Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            });
        }
    }

    //Creating ActionBar Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate menu items
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //When any menu item is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id ==android.R.id.home){
            NavUtils.navigateUpFromSameTask(ChangePasswordActivity.this);
        }
        else if (id == R.id.menu_refresh){
            //Refresh Activity
            startActivity(getIntent());
            finish();
            overridePendingTransition(0, 0);
        } else if (id == R.id.menu_update_profile){
            Intent intent = new Intent(ChangePasswordActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_update_email) {
            Intent intent = new Intent(ChangePasswordActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_profile){
            Intent intent = new Intent(ChangePasswordActivity.this, UserProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_change_password) {
            Intent intent = new Intent(ChangePasswordActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent(ChangePasswordActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_logout){
            authProfile.signOut();
            Toast.makeText(ChangePasswordActivity.this, "Logged Out", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);

            //Clear Stack to prevent user coming back to UserProfileActivity on pressing back button after Logging Out
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();  //Close UserProfileActivity
        } else {
            Toast.makeText(ChangePasswordActivity.this, "Something went Wrong!", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }
}