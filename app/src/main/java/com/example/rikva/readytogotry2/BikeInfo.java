package com.example.rikva.readytogotry2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BikeInfo extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_info);

        Button rentButton = (Button)findViewById(R.id.rentButton);

        TextView bikeIDTextView = (TextView) findViewById(R.id.bikeID);
        TextView distTextView = (TextView) findViewById(R.id.dist);
        TextView addressTextView = (TextView) findViewById(R.id.address);
        TextView cityTextView = (TextView) findViewById(R.id.city);
        TextView countryTextView = (TextView) findViewById(R.id.country);


        Location bikeLocation = null;
        Double latitude = null;
        Double longitude = null;
        if (getIntent().hasExtra("bikeObject")) {
            try {
                JSONObject obj = new JSONObject(getIntent().getStringExtra("bikeObject"));
                longitude = obj.getDouble("last_longitude");
                latitude = obj.getDouble("last_laltitude");
                String id = obj.getString("id");

                bikeIDTextView.setText(id);

                bikeLocation = new Location("");
                bikeLocation.setLatitude(latitude);
                bikeLocation.setLongitude(longitude);


            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("CW2B2", e.toString());
            }
        }

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String address[] = addresses.get(0).getAddressLine(0).trim().split(",");

            addressTextView.setText(address[0]);
            cityTextView.setText(address[1]);
            countryTextView.setText(address[2]);

        } catch (IOException e) {
            e.printStackTrace();
        }

        Location currentLocation = null;
        if (getIntent().hasExtra("currentLocation")) {
            String[] location = getIntent().getStringExtra("currentLocation").split(",");
            currentLocation = new Location("");
            currentLocation.setLongitude(Double.valueOf(location[1]));
            currentLocation.setLatitude(Double.valueOf(location[0]));
        }

        float dist = 0;
        if (currentLocation != null) {
            dist = currentLocation.distanceTo(bikeLocation);
        }

        distTextView.setText(String.valueOf(dist));

        rentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                CharSequence text = "You rented a bike! :D";
                int duration = Toast.LENGTH_SHORT;

                final Toast toast = Toast.makeText(context, text, duration);
                toast.show();

                startActivity(new Intent(BikeInfo.this, UnlockActivity.class));

                }
        });
    }


    private void signInRequest(final SignInActivity.VolleyCallBack callBack, final String bike_id) {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://nomis.ulyssis.be/xbike/auth/contracts/create/";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                            callBack.onSuccess();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callBack.onFailure();
            }
        }
        ) {
            @Override
            protected Map<String, String > getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("bike_id", bike_id);

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
