package com.example.rikva.readytogotry2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    public void switchToLoginActivity(View view) {
        startActivity(new Intent(StartActivity.this, SignInActivity.class));
    }
    public void switchToRegisterActivity(View view) {
        startActivity(new Intent(StartActivity.this, RegisterActivity.class));

    }

    }
