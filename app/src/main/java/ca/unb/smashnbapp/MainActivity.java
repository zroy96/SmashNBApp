package ca.unb.smashnbapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private boolean enteredTournament = false; //set to true to prevent location from being updated while gamering
    public boolean tournamentStarted = false;
    public String roundName = "";
    public String nextOpponent = "";
    private long UPDATE_INTERVAL = 5 * 1000;
    private long FASTEST_INTERVAL = 2000;
    private final int PERMISSION_ID = 69;
    private int participantID;

    private final Location FREDERICTON = new Location("ur mom");
    private final Location MONCTON = new Location("ur mom");
    private final Location SAINT_JOHN = new Location("ur mom");
    private final Location MIRAMICHI = new Location("ur mom");
    private final Location BATHURST = new Location("ur mom");

    final ApiHandler apiBoi = new ApiHandler(this, "Fredericton");

    private String currentCity = "FRED"; //default for now

    private double currentLatitude = 0.0;
    private double currentLongitude = 0.0;

    private FusedLocationProviderClient fusedLocClient;
    private TextView cityText;

    private LocationCallback mLocationCallback;

    private BroadcastReceiver bReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("onReceive", "Broadcast Received");
            int responseCode = intent.getIntExtra("responseCode", 0);
            String endPoint = intent.getStringExtra("endPoint");
            String type = intent.getStringExtra("type");
            String method = intent.getStringExtra("method");
            String json = intent.getStringExtra("json");
            Log.d("responseCode: ", "" + responseCode);
            Log.d("endPoint: ", endPoint);
            Log.d("type: ", type);

            JSONObject reader;
            JSONObject jsonObject;
            JSONArray jsonArray;

            try {
                reader = new JSONObject(json);
                switch(method){

                    // PARTICIPANT METHODS

                    case "addParticipant":
                        //Participant > Create
                        jsonObject = reader.getJSONObject(endPoint);
                        participantID = Integer.parseInt(jsonObject.getString("id"));
                        Log.d("partcipantID", "" + participantID);
                        break;

                    // TOURNAMENT METHODS

                    case "findTournamentName":
                        //Tournament > Index
                        jsonArray = new JSONArray(json);
                        String titleSnipit = "";
                        switch(currentCity) {
                            case "FRED":
                                titleSnipit = "guard";
                                break;
                            case "MONC":
                                titleSnipit = "hub";
                                break;
                            case "SJ":
                                titleSnipit = "port";
                                break;
                            case "BATH":
                                titleSnipit = "bathurst";
                                break;
                            case "MIRA":
                                titleSnipit = "miramichi";
                                break;
                        }
                        boolean found = false;
                        String tourneyName = "";
                        for(int i = 0; i < jsonArray.length() && !found; i++){
                            jsonObject = jsonArray.getJSONObject(i);
                            tourneyName = jsonObject.getString("name");
                            if(tourneyName.contains(titleSnipit)){
                                apiBoi.TOURNAMENT = tourneyName;
                                apiBoi.tournamentId = jsonObject.getString("id");
                                found = true;
                            }
                        }
                        break; //end findTournamentName method


                    case "checkTournamentStarted":
                        //Tournament > Show
                        jsonObject = reader.getJSONObject(endPoint);
                        String started = jsonObject.getString("state");
                        if(started.equalsIgnoreCase("underway")){
                            tournamentStarted = true;
                            //TODO: now we can show their next match
                            apiBoi.getMatches();
                        }

                        break; // end checkTournamentStarted method
                    case "viewBracket":
                        //Tournament > Show
                        //URL url = new URL("http://image10.bizrate-images.com/resize?sq=60&uid=2216744464");
                        //Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        //imageView.setImageBitmap(bmp);

                        break;

                    // MATCH METHODS

                    case "getMatches":
                        //Match > Index
                        //for seeing upcoming matches, reporting scores, etc.
                        //these methods will only be called if the tournament has started
                        jsonArray = new JSONArray(json);
                        found = false;
                        String player1Id;
                        String player2Id;
                        for(int i = 0; i < jsonArray.length() && !found; i++){
                            jsonObject = jsonArray.getJSONObject(i);
                            player1Id = jsonObject.getString("player1_id");
                            player2Id = jsonObject.getString("player2_id");
                            if(player1Id.equalsIgnoreCase(participantID + "")
                                    || player2Id.equalsIgnoreCase(participantID + "")){
                                found = true;
                                //determine what round (ex: losers R1, wieners semis)
                            }
                        }
                        if(!found)
                            //Log.d()
                            break;
                }

            } catch (JSONException e) { //whole thing in try block because json
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
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

        //getLastLocation();

        /*Button cityButton = findViewById(R.id.cityButton);
        cityText = findViewById(R.id.cityTextView);*/

        /*
        cityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent locationIntent = new Intent(MainActivity.this, LocationHandler.class);
                startActivity(locationIntent);
            }
        });
        */

        //REGISTER PARTICIPANT FEATURE
        final TextInputEditText tagInput = findViewById(R.id.tagInputText);
        Button signUpButton = findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tag = String.valueOf(tagInput);
                Log.d("TAG111", "tag?" + tag);
                apiBoi.addParticipant(tag);
            }
        });


        //VIEW TOURNAMENT FEATURE
        Button showBracketButton = findViewById(R.id.showBracketButton);
        showBracketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apiBoi.getTournamentByName("viewBracket");
            }
        });
    }

    protected void onResume(){
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(bReceiver, new IntentFilter("message"));
    }

    protected void onPause (){
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(bReceiver);
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