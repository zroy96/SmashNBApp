package ca.unb.smashnbapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private boolean enteredTournament = false; //set to true to prevent location from being updated while gamering
    private long UPDATE_INTERVAL = 5 * 1000;
    private long FASTEST_INTERVAL = 2000;
    private final int PERMISSION_ID = 69;

    private final Location FREDERICTON = new Location("ur mom");
    private final Location MONCTON = new Location("ur mom");
    private final Location SAINT_JOHN = new Location("ur mom");
    private final Location MIRAMICHI = new Location("ur mom");
    private final Location BATHURST = new Location("ur mom");

    private String currentCity = "nowhere";

    private double currentLatitude = 0.0;
    private double currentLongitude = 0.0;

    private FusedLocationProviderClient fusedLocClient;
    private TextView cityText;

    private LocationCallback mLocationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ApiHandler apiBoi = new ApiHandler();
        apiBoi.addParticipant(this, "TESTPARTICIPANT1");
        apiBoi.addParticipant(this, "TESTPARTICIPANT2");
        apiBoi.addParticipant(this, "TESTPARTICIPANT3");
        apiBoi.addParticipant(this, "TESTPARTICIPANT4");
        apiBoi.randomizeSeeds(this);

        fusedLocClient = LocationServices.getFusedLocationProviderClient(this);

        FREDERICTON.setLatitude(45.963337);
        FREDERICTON.setLongitude(-66.643220);
        MONCTON.setLatitude(46.089412);
        MONCTON.setLongitude(-64.775211);
        SAINT_JOHN.setLatitude(45.273445);
        SAINT_JOHN.setLongitude(-66.063024);
        MIRAMICHI.setLatitude(47.027701);
        MIRAMICHI.setLongitude(-65.503644);
        BATHURST.setLatitude(47.618716);
        BATHURST.setLongitude(-65.654581);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.d("updating loc", "onLocResult (new loc)");
                Location mLastLocation = locationResult.getLastLocation();
                currentLatitude = mLastLocation.getLatitude();
                currentLongitude = mLastLocation.getLongitude();
                calculateCurrentCity();
                cityText.setText(currentCity);
                fusedLocClient.removeLocationUpdates(mLocationCallback);
            }
        };

        getLastLocation();

        Button cityButton = findViewById(R.id.cityButton);
        cityText = findViewById(R.id.cityButton);

        /*
        cityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent locationIntent = new Intent(MainActivity.this, LocationHandler.class);
                startActivity(locationIntent);
            }
        });
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        Log.d("getLastLocation", "hit");
        if (checkPermissions()) {
            Log.d("checkPermissions", "true");
            if (isLocationEnabled()) {
                Log.d("isLocationEnabled", "true");
                fusedLocClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    Log.d("Updating loc", "LastLocation");
                                    currentLatitude = location.getLatitude();
                                    currentLongitude = location.getLongitude();
                                }
                            }
                        }
                );
            } else {
                Log.d("checkPermissions", "false");
                Toast.makeText(this, "You have location services disabled bud!!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){
        Log.d("requestNewLocationData", "hit");
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        //mLocationRequest.setNumUpdates(1);

        fusedLocClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("onRequestPermResult", "hit");
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Granted. Start getting the location information
                if(isLocationEnabled())
                    getLastLocation();
            }
        }
    }



    public void calculateCurrentCity() {
        float[] results = new float[1];
        Location.distanceBetween(currentLatitude, currentLongitude,
                FREDERICTON.getLatitude(), FREDERICTON.getLongitude(), results);
        float minDistance = results[0];
        currentCity = "FRED";

        Location.distanceBetween(currentLatitude, currentLongitude,
                MONCTON.getLatitude(), MONCTON.getLongitude(), results);
        if(results[0] < minDistance) {
            minDistance = results[0];
            currentCity = "MONC";
        }

        Location.distanceBetween(currentLatitude, currentLongitude,
                SAINT_JOHN.getLatitude(), SAINT_JOHN.getLongitude(), results);
        if(results[0] < minDistance) {
            minDistance = results[0];
            currentCity = "SJ";
        }

        Location.distanceBetween(currentLatitude, currentLongitude,
                MIRAMICHI.getLatitude(), MIRAMICHI.getLongitude(), results);
        if(results[0] < minDistance) {
            minDistance = results[0];
            currentCity = "MIRA";
        }

        Location.distanceBetween(currentLatitude, currentLongitude,
                BATHURST.getLatitude(), BATHURST.getLongitude(), results);
        if(results[0] < minDistance) {
            minDistance = results[0];
            currentCity = "BATH";
        }

    }
}
