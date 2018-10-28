package com.example.rikva.readytogotry2;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ShareActionProvider;
import android.widget.Toast;

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

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        final Button signin = (Button)findViewById(R.id.sign_in_button);

        Context context = getApplicationContext();
        CharSequence text_success = "Successful Signed In";
        CharSequence text_fail = "Unsuccessfully Signed In";
        int duration = Toast.LENGTH_SHORT;

        final Toast toast_success = Toast.makeText(context, text_success, duration);
        final Toast toast_fail = Toast.makeText(context, text_fail, duration);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInRequest(new VolleyCallBack() {
                    @Override
                    public void onSuccess() {
                        Log.d("DEBUG", "SUCCES");
                        toast_success.show();
                    }

                    @Override
                    public void onFailure() {
                        Log.d("DEBUG", "FAIL");
                        toast_fail.show();
                    }
                });
            }
        });
    }

    private void signInRequest(final VolleyCallBack callBack) {
        final EditText usernameField = (EditText)findViewById(R.id.username_ed);
        final EditText passwdField = (EditText)findViewById(R.id.password_ed);

        if (TextUtils.isEmpty(usernameField.getText().toString())) {
            usernameField.setError(getString(R.string.error_field_required));
        }
        if (TextUtils.isEmpty(passwdField.getText().toString())) {
            passwdField.setError(getString(R.string.error_field_required));
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://nomis.ulyssis.be/xbike/auth/token/login/";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("DEBUG", response);
                        try {
                            JSONObject tokenObject = new JSONObject(response);
                            String token = tokenObject.getString("auth_token");
                            Log.d("DEBUG", token);
                            SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
                            prefs.edit().putString("token", token).apply();
                            callBack.onSuccess();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callBack.onFailure();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("DEBUG", "ERROR");
                callBack.onFailure();
            }
        }
    ) {
            @Override
            protected Map<String, String > getParams() {
                Map<String, String> params = new HashMap<>();
                String username = usernameField.getText().toString();
                String passwd = passwdField.getText().toString();

                params.put("username", username);
                params.put("password", passwd);

                return params;
            }
        };
        queue.add(stringRequest);
    }

    public interface VolleyCallBack {
        void onSuccess();
        void onFailure();
    }

}
