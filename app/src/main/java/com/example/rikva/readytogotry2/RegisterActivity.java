package com.example.rikva.readytogotry2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button submit = (Button)findViewById(R.id.register_button);


        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (makeRequest()) {
                    SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
                    prefs.edit().remove("token").commit();
                    finish();
                }
            }
        });
    }

    private boolean makeRequest () {

        final EditText username_field = (EditText)findViewById(R.id.tb_username);
        final EditText password_field = (EditText)findViewById(R.id.tb_password);
        final EditText fname_field = (EditText)findViewById(R.id.tb_fname);
        final EditText lname_field = (EditText)findViewById(R.id.tb_lname);
        final EditText email_field = (EditText)findViewById(R.id.tb_email);

        if (TextUtils.isEmpty(username_field.getText().toString())) {
            username_field.setError(getString(R.string.error_field_required));
            return false;
        }
        if (TextUtils.isEmpty(email_field.getText().toString())) {
            email_field.setError(getString(R.string.error_field_required));
            return false;
        }
        if (TextUtils.isEmpty(password_field.getText().toString())) {
            password_field.setError(getString(R.string.error_field_required));
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email_field.getText().toString()).matches()) {
            email_field.setError(getString(R.string.invalid_email));
            return false;
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://nomis.ulyssis.be/xbike/auth/users/create/";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("DEBUG",response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("DEBUG","ERROR");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String>  params = new HashMap<>();

                String username = username_field.getText().toString();
                String passwd = password_field.getText().toString();
                String fname = fname_field.getText().toString();
                String lname = lname_field.getText().toString();
                String email = email_field.getText().toString();


                params.put("username", username);
                params.put("password", passwd);
                params.put("first_name", fname);
                params.put("last_name", lname);
                params.put("email", email);

                return params;
            }
        };
        queue.add(stringRequest);
        return true;
    }

    public void switchToMapsActivity(View view) {
        startActivity(new Intent(RegisterActivity.this, MapsActivity.class));
    }
}
