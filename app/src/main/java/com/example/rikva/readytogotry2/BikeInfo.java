package com.example.rikva.readytogotry2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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
    public String bikeId = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_info);

        final Button rentButton = (Button) findViewById(R.id.rentButton);

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
                bikeId = obj.getString("id");

                bikeIDTextView.setText(bikeId);

                bikeLocation = new Location("");
                bikeLocation.setLatitude(latitude);
                bikeLocation.setLongitude(longitude);


            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("CW2B2", e.toString());
            }
        }

        final String id = bikeId;

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

                try {
                    requestContract();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(new Intent(BikeInfo.this, UnlockActivity.class));

            }
        });
    }


    public StringRequest stringRequest;
    public JsonObjectRequest objectRequest;
    public JSONObject Params;

//            private JSONObject createparams() throws JSONException {
//        Params = new JSONObject();
//
//        Params.put("bike_id", bikeId);
//        return Params;
//
//
//    }


    private void requestContract() throws JSONException {

        String url = "http://nomis.ulyssis.be/xbike/auth/contracts/create/";
        RequestQueue queue = Volley.newRequestQueue(this);


//        objectRequest = new JsonObjectRequest(Request.Method.POST,url,createparams(), new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                try {
//                    JSONObject dataObject = response;
//                    String hash = dataObject.getString("hash");
//                    Log.d("cw2b2",hash);
//                    String startTime = dataObject.getString("time_start");
//                    Log.d("cw2b2",startTime);
//                    SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
//                    prefs.edit().putString("hash", hash);
//                    prefs.edit().putString("startTime", startTime);
//                } catch (JSONException e) {
//                    Log.d("CW2B2", e.toString());
//                }
//
//            }
//        },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.d("CW2B2", objectRequest.toString());
//                        Log.d("CW2B2", error.toString()+"123582");
//                    }
//                })



        stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject dataObject = new JSONObject(response);
                            String hash = dataObject.getString("hash");
                            Log.d("cw2b2",hash);
                            String startTime = dataObject.getString("time_start");
                            Log.d("cw2b2",startTime);
                            SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                            prefs.edit().putString("hash", hash);
                            prefs.edit().putString("startTime", startTime);
                        } catch (JSONException e) {
                            Log.d("CW2B2", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.d("CW2B2", stringRequest.toString());
                Log.d("CW2B2", error.toString()+"123582");
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
                String token = prefs.getString("token", "");
                String headerString = "Token " + token;
                Log.d("CW2B2",headerString);
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                headers.put("Authorization", headerString);
                return headers;
            }
            @Override
            protected Map<String, String > getParams() {
                Log.d("cw2b2", "called");
                Map<String, String> params = new HashMap<>();
                params.put("bike_id", bikeId);
                return params;
            }

        };
        queue.add(stringRequest);
    }

}
