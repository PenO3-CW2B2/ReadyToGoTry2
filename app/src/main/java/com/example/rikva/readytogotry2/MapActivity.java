package com.example.rikva.readytogotry2;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.HashMap;
import java.util.Map;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MapActivity extends AppCompatActivity {

    MapView map = null;
    //public LocationRequest mLocationRequest;
    private MyLocationNewOverlay mLocationOverlay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //startLocationUpdates();


        //handle permissions first, before map is created. not depicted here

        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's tile servers will get you banned based on this string

        //inflate and create the map
        setContentView(R.layout.activity_map);

        map = (MapView) findViewById(R.id.map2);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        IMapController mapController = map.getController();
        mapController.setZoom(15.0);

        this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(ctx),map);
        this.mLocationOverlay.enableMyLocation();
        this.mLocationOverlay.enableFollowLocation();
        map.getOverlays().add(this.mLocationOverlay);


        getBikes(new VolleyCallBack() {
            @Override
            public void onSuccess() {
                Log.d("CW2B2", "SUCCESS");
            }

            @Override
            public void onFailure() {
                Log.d("CW2B2", "FAILURE");
            }
        });
    }

    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause(){
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    private void getBikes(final VolleyCallBack callBack) {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://nomis.ulyssis.be/xbike/auth/freebikes";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray data = new JSONArray(response);

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                Double longitude = obj.getDouble("last_longitude");
                                Double latitude = obj.getDouble("last_laltitude");
                                String id = obj.getString("id");
                                Log.d("CW2B2", longitude.toString() + " " + latitude.toString());

                                GeoPoint bikeLocation = new GeoPoint(latitude, longitude);
                                Marker bike = new Marker(map);
                                bike.setPosition(bikeLocation);
                                bike.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                map.getOverlays().add(bike);
                                bike.setTitle(id);
                            }
                            callBack.onSuccess();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("CW2B2", e.toString());
                            callBack.onFailure();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callBack.onFailure();
                Log.d("CW2B2", "BLAH3");

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

    public interface VolleyCallBack {
        void onSuccess();
        void onFailure();
    }

    /*protected void startLocationUpdates() {
        long FASTEST_INTERVAL = 2000;
        long UPDATE_INTERVAL = 5 * 1000;



        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mLocationRequest.setInterval(UPDATE_INTERVAL);

        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
//        LocationSettingsRequest locationSettingsRequest = builder.build();


        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    10);
            return;
        }
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {


                        if (locationResult != null) {
                            Log.d("", "OKE");
                            Log.d("", "OKE" + " LATITUDE= " + locationResult.getLastLocation().getLatitude() + " LONGITTUDE" + locationResult.getLastLocation().getLongitude());

                            GeoPoint currentLocation = new GeoPoint(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                            Marker locationMarker = new Marker(map);
                            locationMarker.setPosition(currentLocation);
                            locationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                            map.getOverlays().add(locationMarker);
                            locationMarker.setTitle("This is my location !! :D ");

                            // Logic to handle location object
                        } else {
                            Log.d("", "NIET OKE");
                        }
                        //TODO Push location data to server

                        // do work (push data to server)

                    }
                },
                Looper.myLooper());
    }*/

}
