package com.example.studentdormitoryfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HostelRegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hostel_register);

        //Set the Title
        getSupportActionBar().setTitle("Student Dormitory Finder");

        //Open Login Activity
        Button buttonLogin = findViewById(R.id.hostel_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HostelRegisterActivity.this, HostelLoginForm.class);
                startActivity(intent);
            }
        });

        //Open Register Activity
        Button buttonRegister = findViewById(R.id.hostel_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HostelRegisterActivity.this, HostelRegisterForm.class);
                startActivity(intent);
            }
        });

        //Open Register for hostel
        TextView hostelregister = findViewById(R.id.textview_hostel_owner);
        hostelregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HostelRegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}