package com.example.rikva.readytogotry2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity {
    private Boolean clicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button submit = (Button)findViewById(R.id.register_button);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!clicked) {
                    clicked = true;
                    makeRequest();
                }

            }
        });
    }

    private void makeRequest () {

        final EditText username_field = (EditText)findViewById(R.id.tb_username);
        final EditText password_field = (EditText)findViewById(R.id.tb_password);
        final EditText fname_field = (EditText)findViewById(R.id.tb_fname);
        final EditText lname_field = (EditText)findViewById(R.id.tb_lname);
        final EditText email_field = (EditText)findViewById(R.id.tb_email);

        if (TextUtils.isEmpty(username_field.getText().toString())) {
            username_field.setError(getString(R.string.error_field_required));
            clicked = false;
        }
        if (TextUtils.isEmpty(email_field.getText().toString())) {
            email_field.setError(getString(R.string.error_field_required));
            clicked = false;
        }
        if (TextUtils.isEmpty(password_field.getText().toString())) {
            password_field.setError(getString(R.string.error_field_required));
            clicked = false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email_field.getText().toString()).matches()) {
            email_field.setError(getString(R.string.invalid_email));
            clicked = false;
        }
        RequestQueue queue = Volley.newRequestQueue(this);

        String url ="https://andreasp.ulyssis.be/auth/users/create/";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("DEBUG",response);
                        SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
                        clicked = false;
                        prefs.edit().remove("token").apply();
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("DEBUG","ERROR");
                clicked = false;
                showerror();


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

    }

    public void switchToMapsActivity(View view) {
        startActivity(new Intent(RegisterActivity.this, MapActivity.class));
    }
    public void showerror (){
        Context context = getApplicationContext();
        CharSequence text_fail = "Please check internet connection, if your internet connection is ok please choose a more difficult password or another username or check if your email is spelled correctly";
        int duration = Toast.LENGTH_SHORT;

        final Toast toast_fail = Toast.makeText(context, text_fail, duration);
        toast_fail.show();

    }


}
