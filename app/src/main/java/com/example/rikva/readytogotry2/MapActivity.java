package com.example.rikva.readytogotry2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.inputmethodservice.Keyboard;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationSettingsRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.api.IMapView;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.NetworkLocationIgnorer;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MapActivity extends AppCompatActivity  {
    MapView map = null;
    //public LocationRequest mLocationRequest;
    private IMyLocationConsumer mMyLocationConsumer;
    public RikProvider provider;
    public IMyLocationProvider providermap;
    private MyLocationNewOverlay mLocationOverlay;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("", "PROBLEEM");


        setContentView(R.layout.activity_map);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    10);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    10);

        }





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

        Configuration.getInstance().setUserAgentValue(getPackageName());
        //inflate and create the map
        setContentView(R.layout.activity_map);

        map = (MapView) findViewById(R.id.map2);
        map.setTileSource(TileSourceFactory.HIKEBIKEMAP);
        map.setBuiltInZoomControls(false);
        map.setMultiTouchControls(true);
        final IMapController mapController = map.getController();
        mapController.setZoom(15.0);
        provider = new RikProvider(ctx);




        this.mLocationOverlay = new MyLocationNewOverlay(provider,map);

//        this.mLocationOverlay.enableMyLocation();
//        this.mLocationOverlay.mMyLocationProvider.destroy();
//        this.mLocationOverlay.mMyLocationProvider.stopLocationProvider();


        map.getOverlays().add(this.mLocationOverlay);

        if (mLocationOverlay.getMyLocation() != null) {
            mapController.setCenter(mLocationOverlay.getMyLocation());
            this.mLocationOverlay.enableFollowLocation();
            Log.d("CW2B2", mLocationOverlay.getMyLocation().toString() );
        } else {
            mapController.setCenter(new GeoPoint(50.883333, 4.7));
            ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.mapMainLayout);
            Snackbar snackbar = Snackbar.make(layout, "No Location Services Available", Snackbar.LENGTH_LONG);
            Log.d("CW2B2", "No Location Services Available" );

            snackbar.show();
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLocationOverlay.enableFollowLocation();
            }
        });







        final EditText searchStringET = (EditText)findViewById(R.id.searchbar);
        final ImageButton searchButton = (ImageButton)findViewById(R.id.searchButton);
        final Marker searchResult = new Marker(map);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchString = searchStringET.getText().toString();
                if (searchString.isEmpty()) {
                    return;
                }

                ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.mapMainLayout);
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(layout.getWindowToken(), 0);

                Geocoder geocoder = new Geocoder(MapActivity.this);
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocationName(searchString, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(addresses.size() > 0) {
                    double latitude = addresses.get(0).getLatitude();
                    double longitude = addresses.get(0).getLongitude();
                    mLocationOverlay.disableFollowLocation();
                    mapController.setCenter(new GeoPoint(latitude, longitude));
                    searchResult.setPosition(new GeoPoint(latitude, longitude));
                    searchResult.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    map.getOverlays().add(searchResult);
                }
            }
        });

        Log.d("", "Start updates ONCreate");

        providermap = mLocationOverlay.getMyLocationProvider();
        Log.d("", provider.getLocationSources().toString());
        startLocationUpdates();


    }
@Override
    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        Log.d("", "OnResume");

        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
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

        startLocationUpdates();
        Log.d("", "OnresumeFinished");


    }
    @Override

    public void onPause(){
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
        Log.d("cw2b2","PAsue");
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
//        mFusedLocationClient = null;
//        mLocationCallback = null;
//        mLocationRequest = null;
        Log.d("cw2b2","PAsue2");
//        finish();

    }


    private void getBikes(final VolleyCallBack callBack) {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://andreasp.ulyssis.be/auth/freebikes";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray data = new JSONArray(response);

                            for (int i = 0; i < data.length(); i++) {
                                 final JSONObject obj = data.getJSONObject(i);
                                Double longitude = obj.getDouble("last_longitude");
                                final Double latitude = obj.getDouble("last_laltitude");
                                Log.d("CW2B2", longitude.toString() + " " + latitude.toString());

                                GeoPoint bikeLocation = new GeoPoint(latitude, longitude);
                                Marker bike = new Marker(map);
                                bike.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker, MapView mapView) {
                                        Intent intent = new Intent(MapActivity.this, BikeInfo.class);
                                        intent.putExtra("bikeObject", obj.toString());
                                        GeoPoint currentLocation = mLocationOverlay.getMyLocation();
                                        if (currentLocation!= null){
                                            intent.putExtra("currentLocation", String.valueOf(currentLocation));
                                        }

                                        startActivity(intent);
                                        return false;
                                    }
                                });
                                bike.setIcon(getResources().getDrawable(R.mipmap.bicycle));
                                bike.setPosition(bikeLocation);
                                bike.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                map.getOverlays().add(bike);
                                //bike.setTitle(id);
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

    protected void startLocationUpdates() {
        long FASTEST_INTERVAL = 2000;
        long UPDATE_INTERVAL = 5 * 1000;
        Log.d("", "Start updates");
        mFusedLocationClient= getFusedLocationProviderClient(this);








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
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {


                if (locationResult != null) {
                    Log.d("cw2b2", "OKE" + " LATITUDE= " + locationResult.getLastLocation().getLatitude() + " LONGITTUDE" + locationResult.getLastLocation().getLongitude());
//                    mLocationOverlay.setLoccationHack(locationResult.getLastLocation());



                    // Logic to handle location object
                } else {
                    Log.d("", "NIET OKE");
                }

                // do work (push data to server)

            }
        };
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        Log.d("", "Start updates2");

    }



}
