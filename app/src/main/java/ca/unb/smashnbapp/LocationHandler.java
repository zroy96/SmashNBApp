package ca.unb.smashnbapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

import java.util.Locale;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class LocationHandler {
    private LocationRequest locationRequest;
    private Context appConext;
    private int locationRequestCode = 1000;
    private long UPDATE_INTERVAL = 5 * 1000;
    private long FASTEST_INTERVAL = 2000;

    private final Location FREDERICTON = new Location("ur mom");
    private final Location MONCTON = new Location("ur mom");
    private final Location SAINT_JOHN = new Location("ur mom");
    private final Location MIRAMICHI = new Location("ur mom");
    private final Location BATHURST = new Location("ur mom");

    private FusedLocationProviderClient flpc;

    /*
    private final String[] FREDERICTON = {"45.963337", "-66.643220"};
    private final String[] MONCTON = {"46.089412", "-64.775211"};
    private final String[] SAINT_JOHN = {"45.273445", "-66.063024"};
    private final String[] MIRAMICHI = {"47.027701", "-65.503644"};
    private final String[] BATHURST = {"47.618716", "-65.654581"};
    */
    public LocationHandler(Context context) {
        appConext = context;

        FREDERICTON.setLatitude(45.963337);
        FREDERICTON.setLatitude(-66.643220);
        MONCTON.setLatitude(46.089412);
        MONCTON.setLatitude(-64.775211);
        SAINT_JOHN.setLatitude(45.273445);
        SAINT_JOHN.setLatitude(-66.063024);
        MIRAMICHI.setLatitude(47.027701);
        MIRAMICHI.setLatitude(-65.503644);
        BATHURST.setLatitude(47.618716);
        BATHURST.setLatitude(-65.654581);

        startLocationUpdates();


    }

    // Trigger new location updates at interval
    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(appConext);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        getFusedLocationProviderClient(appConext).requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        //TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO
                        ///onLocationChanged(locationResult.getLastLocation()); TODO
                        //TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO
                    }
                },
                Looper.myLooper());
    }

    public String getCity() {

        return "";
    }
}