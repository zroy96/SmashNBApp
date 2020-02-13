package ca.unb.smashnbapp;

import android.location.Location;

import com.google.android.gms.location.LocationRequest;

public class LocationHandler {
    private LocationRequest locationRequest;

    private final Location FREDERICTON = new Location("ur mom");
    FREDERICTON.setLatitude(45.963337);
    FREDERICTON.setLatitude(-66.643220);

    private final Location MONCTON = new Location("ur mom");
    MONCTON.setLatitude(46.089412);
    MONCTON.setLatitude(-64.775211);

    private final Location SAINT_JOHN = new Location("ur mom");
    SAINT_JOHN.setLatitude(45.273445);
    SAINT_JOHN.setLatitude(-66.063024);

    private final Location MIRAMICHI = new Location("ur mom");
    MIRAMICHI.setLatitude(47.027701);
    MIRAMICHI.setLatitude(-65.503644);

    private final Location BATHURST = new Location("ur mom");
    BATHURST.setLatitude(47.618716);
    BATHURST.setLatitude(-65.654581);

    /*
    private final String[] FREDERICTON = {"45.963337", "-66.643220"};
    private final String[] MONCTON = {"46.089412", "-64.775211"};
    private final String[] SAINT_JOHN = {"45.273445", "-66.063024"};
    private final String[] MIRAMICHI = {"47.027701", "-65.503644"};
    private final String[] BATHURST = {"47.618716", "-65.654581"};
    */
    public LocationHandler(){
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        locationRequest.setInterval(5*60*1000); //TODO
    }

    public String getCity(){
        
        return "";
    }
}
