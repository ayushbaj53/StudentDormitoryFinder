package com.example.studentdormitoryfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateProfileActivity extends AppCompatActivity {

    private EditText editTextUpdateName, editTextUpdateDoB, editTextUpdateMobile;
    private RadioGroup radioGroupUpdateGender;
    private RadioButton radioButtonUpdateGenderSelected;
    private String textFullName, textDoB, textGender, textMobile;
    private FirebaseAuth authProfile;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        getSupportActionBar().setTitle("Update Profile Details");

        progressBar = findViewById(R.id.progressBar);
        editTextUpdateName = findViewById(R.id.editText_update_profile_name);
        editTextUpdateDoB = findViewById(R.id.editText_update_profile_dob);
        editTextUpdateMobile = findViewById(R.id.editText_update_profile_mobile);

        radioGroupUpdateGender = findViewById(R.id.radio_group_update_gender);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        //Show Profile Data
        showProfile(firebaseUser);

        //Upload Profile Pic
        Button buttonUploadProfilePic = findViewById(R.id.button_upload_profile_pic);
        buttonUploadProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdateProfileActivity.this,UploadProfilePicActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Update Email
        Button buttonUpdateEmail = findViewById(R.id.button_profile_update_email);
        buttonUploadProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdateProfileActivity.this,UpdateEmailActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Setting up Date Picker on EditText
        editTextUpdateDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Extracting saved dd, mm, yyyy, into different variables by creating an array delimited by "/"
                String textSADoB[] = textDoB.split("/");

                int day = Integer.parseInt(textSADoB[0]);
                int month = Integer.parseInt(textSADoB[1]) -1; //to take care of month index staring from 0
                int year = Integer.parseInt(textSADoB[2]);

                DatePickerDialog picker;

                //Date picker Dialog
                picker = new DatePickerDialog(UpdateProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayofMonth) {
                        editTextUpdateDoB.setText(dayofMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        //Update Profile
        Button buttonUpdateProfile = findViewById(R.id.button_update_profile);
        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile(firebaseUser);
            }
        });

    }

    //Update Profile
    private void updateProfile(FirebaseUser firebaseUser) {
        int selectedGenderID = radioGroupUpdateGender.getCheckedRadioButtonId();
        radioButtonUpdateGenderSelected = findViewById(selectedGenderID);

        //Validate Mobile Number using Matcher and Pattern (Regular Expression)
        String mobileRegex = "[9][0-9]{9}"; // First no. should be 9 and rest 9 nos. can be any no.
        Matcher mobileMatcher;
        Pattern mobilePattern = Pattern.compile(mobileRegex);
        mobileMatcher = mobilePattern.matcher(textMobile);

        if (TextUtils.isEmpty(textFullName)){
            Toast.makeText(UpdateProfileActivity.this, "Please enter your full name", Toast.LENGTH_LONG).show();
            editTextUpdateName.setError("Full name is required");
            editTextUpdateName.requestFocus();
        } else if (TextUtils.isEmpty(textDoB)) {
            Toast.makeText(UpdateProfileActivity.this, "Please enter your date of birth", Toast.LENGTH_LONG).show();
            editTextUpdateDoB.setError("Date of Birth is required");
            editTextUpdateDoB.requestFocus();
        } else if (TextUtils.isEmpty(radioButtonUpdateGenderSelected.getText())) {
            Toast.makeText(UpdateProfileActivity.this, "Please enter your gender", Toast.LENGTH_LONG).show();
            radioButtonUpdateGenderSelected.setError("Gender is required");
            radioButtonUpdateGenderSelected.requestFocus();
        } else if (TextUtils.isEmpty(textMobile)) {
            Toast.makeText(UpdateProfileActivity.this, "Please enter your mobile no.", Toast.LENGTH_LONG).show();
            editTextUpdateMobile.setError("Mobile No. is required");
            editTextUpdateMobile.requestFocus();
        } else if (textMobile.length() != 10) {
            Toast.makeText(UpdateProfileActivity.this, "Please re-enter your mobile no.", Toast.LENGTH_LONG).show();
            editTextUpdateMobile.setError("Mobile No. should be 10 digits");
            editTextUpdateMobile.requestFocus();
        } else if (!mobileMatcher.find()){
            Toast.makeText(UpdateProfileActivity.this, "Please re-enter your mobile no.", Toast.LENGTH_LONG).show();
            editTextUpdateMobile.setError("Mobile No. is not valid");
            editTextUpdateMobile.requestFocus();
        } else {
            //Obtain the data entered by the user
            textGender = radioButtonUpdateGenderSelected.getText().toString();
            textFullName = editTextUpdateName.getText().toString();
            textDoB = editTextUpdateDoB.getText().toString();
            textMobile = editTextUpdateMobile.getText().toString();

            //Enter User Data into the Firebase Realtime Database. Set up dependencies
            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textDoB, textGender, textMobile);

            //Enter User reference from Database for "Registered Users"
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

            String userID = firebaseUser.getUid();

            progressBar.setVisibility(View.VISIBLE);

            referenceProfile.child(userID).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){

                        //Setting new display Name
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                        firebaseUser.updateProfile(profileUpdates);

                        Toast.makeText(UpdateProfileActivity.this, "Update Successful!", Toast.LENGTH_LONG).show();

                        //Stop user form returning to UpdateProfileActivity on pressing back button and close activity
                        Intent intent = new Intent(UpdateProfileActivity.this, UserProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        try {
                            throw task.getException();
                        } catch (Exception e) {
                            Toast.makeText(UpdateProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }


    }

    //fetch data from Firebase and display
    private void showProfile(FirebaseUser firebaseUser) {
        String userIDofRegistered = firebaseUser.getUid();

        //Extracting User Reference from Database for "Registered Users"
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

        progressBar.setVisibility(View.VISIBLE);

        referenceProfile.child(userIDofRegistered).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null) {
                    textFullName = firebaseUser.getDisplayName();
                    textDoB = readUserDetails.DoB;
                    textGender = readUserDetails.gender;
                    textMobile = readUserDetails.mobile;

                    editTextUpdateName.setText(textFullName);
                    editTextUpdateDoB.setText(textDoB);
                    editTextUpdateMobile.setText(textMobile);

                    //Show Gender through Radio Button
                    if (textGender.equals("Male")){
                        radioButtonUpdateGenderSelected = findViewById(R.id.radio_male);
                    } else {
                        radioButtonUpdateGenderSelected = findViewById(R.id.radio_female);
                    }
                    radioButtonUpdateGenderSelected.setChecked(true);
                } else {
                    Toast.makeText(UpdateProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateProfileActivity.this, "Something went Wrong!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);

            }
        });
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

        if (id == R.id.menu_refresh){
            //Refresh Activity
            startActivity(getIntent());
            finish();
            overridePendingTransition(0, 0);
        } else if (id == R.id.menu_update_profile){
            Intent intent = new Intent(UpdateProfileActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_update_email) {
            Intent intent = new Intent(UpdateProfileActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_settings){
            Toast.makeText(UpdateProfileActivity.this, "menu_settings", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.menu_change_password) {
            Intent intent = new Intent(UpdateProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        } /*else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent(UserProfileActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
        } */else if (id == R.id.menu_logout){
            authProfile.signOut();
            Toast.makeText(UpdateProfileActivity.this, "Logged Out", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(UpdateProfileActivity.this, MainActivity.class);

            //Clear Stack to prevent user coming back to UserProfileActivity on pressing back button after Logging Out
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();  //Close UserProfileActivity
        } else {
            Toast.makeText(UpdateProfileActivity.this, "Something went Wrong!", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }
}