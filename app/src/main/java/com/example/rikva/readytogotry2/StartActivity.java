package com.example.rikva.readytogotry2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StartActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        final Button login = (Button)findViewById(R.id.login_button);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tokenCheck(new VolleyCallBack() {
                    @Override
                    public void onSuccess() {
                        startActivity(new Intent(StartActivity.this, HomeActivity.class));
                    }
                    @Override
                    public void onFailure() {
                        startActivity(new Intent(StartActivity.this, SignInActivity.class));
                    }
                });
            }
        });

    }

    private void tokenCheck(final VolleyCallBack callBack) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://andreasp.ulyssis.be/auth/users/me/";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            String detail = responseObject.getString("detail");
                            callBack.onFailure();
                            Log.d("DEBUG", "BLAH");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callBack.onSuccess();
                            Log.d("DEBUG", "BLAH2");

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callBack.onFailure();
                Log.d("DEBUG", "BLAH3");

            }
        }) {

                @Override
                public Map getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
                    String token = prefs.getString("token","");
                    String headerString = "Token " + token;
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", headerString);
                    return headers;
                }
        };
        queue.add(stringRequest);
    }

    public void switchToRegisterActivity(View view) {
        startActivity(new Intent(StartActivity.this, RegisterActivity.class));

    }

    public interface VolleyCallBack {
        void onSuccess();
        void onFailure();
    }
}
