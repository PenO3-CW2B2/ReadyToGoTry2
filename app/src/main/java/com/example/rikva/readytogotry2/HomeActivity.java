package com.example.rikva.readytogotry2;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    public Boolean renting = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FloatingActionButton refreshButton = (FloatingActionButton) findViewById(R.id.refresh_fab);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateHomeActivity();
            }
        });
        updateHomeActivity();
    }

    public void onResume() {
        super.onResume();
        updateHomeActivity();
    }

    public void switchToMapsActivity(View view) {
        startActivity(new Intent(this, MapActivity.class));
    }
    public void switchToUnlockActivity(View view) {
        if (renting) {
            startActivity(new Intent(this, UnlockActivity.class));
        } else {
            ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.HomeActivityLayout);
            Snackbar snackbar = Snackbar.make(layout, "Please rent a bike first", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }
    public void switchToEndContractActivity(View view) {
        if (renting) {
            startActivity(new Intent(this, EndContractActivity.class));
        } else {
            ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.HomeActivityLayout);
            Snackbar snackbar = Snackbar.make(layout, "Please rent a bike first", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    public void updateHomeActivity() {
        final TextView contractInfoTV = (TextView)findViewById(R.id.contract_info_tv);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://andreasp.ulyssis.be/auth/users/contracts/";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray dataArray = new JSONArray(response);
                    JSONObject dataObject = dataArray.getJSONObject(0);
                    Boolean payed = dataObject.getBoolean("payed");
                    if (!payed) {
                        contractInfoTV.setText("You're renting a bike!");
                        renting = true;
                    }
                } catch (JSONException e) {
                    contractInfoTV.setText("You're not renting a bike!");
                    renting = false;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("cw2B2", "FATAL: VolleyError: " + error.toString());
            }
        }) {
            @Override
            public Map getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
                String token = prefs.getString("token","");
                String headerString = "Token " + token;
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                headers.put("Authorization", headerString);
                return headers;
            }
        };
        queue.add(stringRequest);
    }

}
