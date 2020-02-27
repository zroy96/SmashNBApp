package ca.unb.smashnbapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private boolean enteredTournament = false; //set to true to prevent location from being updated while gamering
    public boolean tournamentStarted = false;
    public String roundName = "";
    public String nextOpponent = "";
    private long UPDATE_INTERVAL = 5 * 1000;
    private long FASTEST_INTERVAL = 2000;
    private final int PERMISSION_ID = 69;
    private int participantID;
    private int playerNum = 0;

    private final Location FREDERICTON = new Location("ur mom");
    private final Location MONCTON = new Location("ur mom");
    private final Location SAINT_JOHN = new Location("ur mom");
    private final Location MIRAMICHI = new Location("ur mom");
    private final Location BATHURST = new Location("ur mom");

    final ApiHandler apiBoi = new ApiHandler(this);

    private String currentCity = "FRED"; //default for now

    private double currentLatitude = 0.0;
    private double currentLongitude = 0.0;

    private FusedLocationProviderClient fusedLocClient;
    private TextView cityText;

    private LocationCallback mLocationCallback;

    private BroadcastReceiver bReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            int responseCode = intent.getIntExtra("responseCode", -1);
            String method = intent.getStringExtra("method");
            String json = intent.getStringExtra("json");
            Log.d("onReceive", "Broadcast received for method: "+method+" Response code: "+responseCode);

            switch(method){

                case "addParticipant":
                    //Participant > Create
                    try {
                        addParticipant(json);
                    }
                    catch(Exception e) {
                        Log.d(method+"Failure", "Response code: "+responseCode, e);
                    }
                    break;

                case "findTournamentName":
                    //Tournament > Index
                    try {
                        findTournamentName(json);
                    }
                    catch(Exception e) {
                        Log.d(method+"Failure", "Response code: "+responseCode, e);
                    }
                    break;

                case "checkTournamentStarted":
                    //Tournament > Show
                    try {
                        checkTournamentStarted(json);
                    }
                    catch(Exception e) {
                        Log.d(method+"Failure", "Response code: "+responseCode, e);
                    }
                    break;

                case "viewBracket":
                    //Tournament > Show
                    try {
                        viewBracket(json);
                    }
                    catch(Exception e) {
                        Log.d(method+"Failure", "Response code: "+responseCode, e);
                    }
                    break;

                case "getMatches":
                    //Match > Index
                    //for seeing upcoming matches, reporting scores, etc. Note: these methods will only be called if the tournament has started

                    break;

                default:
                    Log.d("onReceive", "Method not found in switch cases");
            }
        }
    };



/////////////////////////////////////////////////
/////////////////////////////////////////////////
/////////////////////////////////////////////////
/////////////////////////////////////////////////   LIFECYCLE METHODS



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

        TextView tournamentStartedText = (TextView)findViewById(R.id.tournamentStarted);
        tournamentStartedText.setText("tournament not started");

        apiBoi.findTournamentName(apiBoi.yesterdayDate);

        TextView tournamentNameText = (TextView)findViewById(R.id.tournamentName);
        tournamentNameText.setText(apiBoi.tournamentName);

        Button checkStartedButton = (Button)findViewById(R.id.checkStart);
        checkStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!tournamentStarted)
                    apiBoi.checkTournamentStarted();
            }
        });


        //REGISTER PARTICIPANT FEATURE
        final TextInputEditText tagInput = findViewById(R.id.tagInputText);
        Button signUpButton = findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tag = String.valueOf(tagInput.getText());
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

        //VIEW NOTES
        final Button notesButton = findViewById(R.id.noteButton);
        notesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent noteIntent = new Intent(MainActivity.this, NoteTakingActivity.class);
                noteIntent.putExtra("filename", "SamusMaster44");
                startActivity(noteIntent);
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



/////////////////////////////////////////////////
/////////////////////////////////////////////////
/////////////////////////////////////////////////
/////////////////////////////////////////////////   API CALL RETURN METHODS



    private void addParticipant(String json) throws Exception{
        JSONObject reader = new JSONObject(json);
        JSONObject jsonObject;

        jsonObject = reader.getJSONObject("participant");
        participantID = Integer.parseInt(jsonObject.getString("id"));
        Log.d("AddParticipantSuccess", "ID: " + participantID);
    }

    private void findTournamentName(String json) throws Exception{
        JSONArray jsonArray = new JSONArray(json);
        JSONObject jsonObject;

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
            jsonObject = jsonArray.getJSONObject(i).getJSONObject("tournament");
            tourneyName = jsonObject.getString("name");
            if(tourneyName.contains(titleSnipit)){
                apiBoi.tournamentName = tourneyName;
                apiBoi.tournamentId = jsonObject.getString("id");
                apiBoi.tournamentUrl = jsonObject.getString("url");
                TextView tournamentNameText = (TextView)findViewById(R.id.tournamentName);
                tournamentNameText.setText(tourneyName);
                found = true;
            }
        }
        if(!found)
            throw new Exception("TOURNAMENT NOT FOUND");
        Log.d("findT.NameSuccess", "T.Name: " + tourneyName);
    }

    private void checkTournamentStarted(String json) throws Exception {
        JSONObject reader = new JSONObject(json);
        JSONObject jsonObject;

        jsonObject = reader.getJSONObject("tournament");
        String started = jsonObject.getString("state");
        if (started.equalsIgnoreCase("underway")) {
            tournamentStarted = true;
            TextView tournamentStartedText = (TextView) findViewById(R.id.tournamentStarted);
            tournamentStartedText.setText("tournament started");
            //TODO: now we can show their next match
            //apiBoi.getMatches(); TODO
            apiBoi.updateScore("191437058", 1, 3, 0);
        }
        Log.d("checkT.StartedSuccess", "Started: " + started);
    }

    private void viewBracket(String json) throws Exception {
        JSONObject reader = new JSONObject(json);
        JSONObject jsonObject;

        jsonObject = reader.getJSONObject("tournament");
        //BROWSER VERSION
        URL url = new URL(jsonObject.getString("live_image_url"));
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.valueOf(url)));
        startActivity(browserIntent);
        // TODO BITMAP VERSION cant get this imageview to be visible
        /*Bitmap bmp = intent.getParcelableExtra("bitmap");
        ImageView bracketView = findViewById(R.id.bracketViewID);
        bracketView.setImageBitmap(bmp);
        bracketView.setVisibility(View.VISIBLE);
        */
        Log.d("checkT.StartedSuccess", "URL: " + url);
    }

    private void getMatches(String json) throws Exception {
        JSONObject reader = new JSONObject(json);
        JSONObject jsonObject;
        JSONArray jsonArray;

        jsonArray = new JSONArray(json);
        boolean found = false;
        String player1Id;
        String player2Id;
        for(int i = 0; i < jsonArray.length() && !found; i++){
            jsonObject = jsonArray.getJSONObject(i).getJSONObject("match");
            player1Id = jsonObject.getString("player1_id");
            player2Id = jsonObject.getString("player2_id");
            if(player1Id.equalsIgnoreCase(participantID + "")) {
                playerNum = 1;
                found = true;
            }
            else if(player2Id.equalsIgnoreCase(participantID + "")){
                playerNum = 2;
                found = true;
            }
            if(found) {
                //determine what round (ex: losers R1, wieners semis)
            }
        }
        if(!found)
            throw new Exception("MATCH NOT FOUND");
        Log.d("getMatchesSuccess", "IDK what to put here yet");
    }



/////////////////////////////////////////////////
/////////////////////////////////////////////////
/////////////////////////////////////////////////
/////////////////////////////////////////////////   CLASS METHODS



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