package ca.unb.smashnbapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Locale;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;


//*************************************************
// *********NOT USING THIS CLASS RIGHT NOW*********
//*************************************************

public class LocationHandler  extends AppCompatActivity {
    public boolean enteredTournament = false; //set to true to prevent location from being updated while gamering
    private LocationRequest locationRequest;
    private Context appConext;
    private int locationRequestCode = 1000;
    private long UPDATE_INTERVAL = 5 * 1000;
    private long FASTEST_INTERVAL = 2000;
    private final int PERMISSION_ID = 69;

    private final Location FREDERICTON = new Location("ur mom");
    private final Location MONCTON = new Location("ur mom");
    private final Location SAINT_JOHN = new Location("ur mom");
    private final Location MIRAMICHI = new Location("ur mom");
    private final Location BATHURST = new Location("ur mom");

    public String currentCity = "nowhere";

    private double currentLatitude = 0.0;
    private double currentLongitude = 0.0;

    private FusedLocationProviderClient fusedLocClient;

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            currentLatitude = mLastLocation.getLatitude();
            currentLongitude = mLastLocation.getLongitude();
            calculateCurrentCity();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        //appConext = context;
        fusedLocClient = LocationServices.getFusedLocationProviderClient(appConext);

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

        getLastLocation();
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                fusedLocClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    currentLatitude = location.getLatitude();
                                    currentLongitude = location.getLongitude();
                                }
                            }
                        }
                );
            } else {
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

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setNumUpdates(1);

        fusedLocClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(appConext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                (Activity)appConext,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) appConext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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