package com.conestoga.weatherfits;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.conestoga.weatherfits.databinding.ActivityMapsBinding;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient mLocationClient;
    private LocationManager locationManager;
    BottomNavigationView bottomNav;

    private final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";
    private final String appId = "410dcba1de2ba9722a09724ec7849e66";
    DecimalFormat df = new DecimalFormat("#.##");

    final private int GPS_REQUEST_CODE = 9001;
    double lat, lon, temp, feelsLike;
    String city;
    boolean isRain;

    FloatingActionButton fab;
    AutoCompleteTextView search;
    Button btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

//    Initialize the application
    @SuppressLint("VisibleForTests")
    private void init(){
        checkPermissions();

        search = findViewById(R.id.search);
        fab = findViewById(R.id.fab);
        btnSearch = findViewById(R.id.btnGetFits);
        bottomNav = findViewById(R.id.bottomNav);

        initMap();
        setBottomNavView();

        mLocationClient = new FusedLocationProviderClient(this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrLoc();
                btnSearch.setVisibility(View.VISIBLE);
                city = "Waterloo";
                lat = 43.48893877650618;
                lon = -80.51650744966912;
                search.setText("Waterloo, ON");
            }
        });


        search.setThreshold(2);
        ArrayAdapter<CharSequence> searchAdapter = ArrayAdapter.createFromResource(this, R.array.city_array, android.R.layout.simple_dropdown_item_1line);
        search.setAdapter(searchAdapter);

        search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object obj = parent.getItemAtPosition(position);
                city = obj.toString();
                btnSearch.setVisibility(View.VISIBLE);
                if (city.equals("Waterloo, ON")){
                    city = "Waterloo";
                    lat = 43.48893877650618;
                    lon = -80.51650744966912;
                }
                if (city.equals("Kitchener, ON")){
                    city = "Kitchener";
                    lat = 43.43789224938992;
                    lon = -80.45812077956771;
                }
                if (city.equals("Cambridge, ON")){
                    city = "Cambridge";
                    lat = 43.39454494113689;
                    lon = -80.30890051686033;
                }
                if (city.equals("Toronto, ON")){
                    city = "Toronto";
                    lat = 43.74296412671283;
                    lon = -79.3883647894817;
                }
                if (city.equals("Calgary, AB")){
                    city = "Calgary";
                    lat = 51.0732945014509;
                    lon = -114.07136174067612;
                }
                gotoLocation(lat, lon);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWeatherDetails();
                Intent intent = new Intent(MapsActivity.this, WearTypeActivity.class);
                intent.putExtra("TEMP", temp);
                intent.putExtra("FEELS_LIKE", feelsLike);
                intent.putExtra("IS_RAIN", isRain);
                intent.putExtra("CITY", city);
                startActivity(intent);
            }
        });
    }

//    Set Bottom Navigation
    private void setBottomNavView() {
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        break;
                    case R.id.nav_about:
                        startActivity(new Intent(MapsActivity.this, AboutActivity.class));
                        break;
                    case R.id.nav_profile:
                        startActivity(new Intent(MapsActivity.this, AccountsActivity.class));
                        break;
                }
                return false;
            }
        });
    }

//    Initialize the map
    private void initMap() {
        if(gpsEnabled()) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            assert mapFragment != null;
            mapFragment.getMapAsync(this);
        }
    }

//    Check if GPS is enabled
    private boolean gpsEnabled(){
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(providerEnabled){
            return true;
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("GPS Permission Required")
                    .setMessage("GPS is required for the app to work. Press OK to enable GPS")
                    .setPositiveButton(android.R.string.ok, ((dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, GPS_REQUEST_CODE);
                    }))
                    .setCancelable(false)
                    .show();
        }

        return false;
    }

//    Check if we have permissions or not
    private void checkPermissions(){
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

//    Get the location of the user using GPS
    @SuppressLint("MissingPermission")
    private void getCurrLoc() {
        mLocationClient.getLastLocation().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Location location = task.getResult();
                gotoLocation(location.getLatitude(), location.getLongitude());
            }
        });
    }

//    Add Marker and animation to the transition
    private void gotoLocation(double latitude, double longitude) {
        LatLng LatLng = new LatLng(latitude, longitude);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(LatLng);
        mMap.clear();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng, 15));
        mMap.addMarker(markerOptions);
    }

//    Get Weather Details From OpenWeather API
    private void getWeatherDetails(){
        String tempUrl = "";
        if(city.isEmpty()){
            new AlertDialog.Builder(this)
                    .setTitle("Error!")
                    .setMessage("You need to select a city to find the fits.")
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss());
        } else {
            tempUrl = WEATHER_URL + "?lat=" + lat + "&lon=" + lon + "&appid=" + appId;

            StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
//                    Log.d("LOREM_IPSUM RESP: ", response);
                    String output = "";
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                        JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                        isRain = jsonObjectWeather.getString("main") == "Rain";
                        JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                        temp = jsonObjectMain.getDouble("temp") - 273.15;
                        feelsLike = jsonObjectMain.getDouble("feels_like") - 273.15;
                        output = "Weather: " + isRain + " Temp: " + temp + " Feels Like: " + feelsLike;
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("LOREM_IPSUM ERR: " + error);
                }
            });

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);

        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}