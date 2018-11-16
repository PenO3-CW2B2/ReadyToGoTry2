package com.example.rikva.readytogotry2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void switchToMapsActivity(View view) {
        startActivity(new Intent(this, MapActivity.class));
    }
    public void switchToUnlockActivity(View view) {
        startActivity(new Intent(this, UnlockActivity.class));
    }

}
