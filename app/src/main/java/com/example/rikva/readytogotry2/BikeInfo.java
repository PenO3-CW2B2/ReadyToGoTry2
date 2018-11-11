package com.example.rikva.readytogotry2;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class BikeInfo extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_info);

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


    }
}
