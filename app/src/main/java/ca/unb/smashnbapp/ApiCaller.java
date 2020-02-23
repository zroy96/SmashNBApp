package ca.unb.smashnbapp;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiCaller extends IntentService {

    private Intent intent;
    HttpURLConnection conn;

    public ApiCaller() {
        super("I DONT KNOW WHY I NEED THIS STRING");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        this.intent = intent;
        String[] response = makeCall();
        String weGotJSON = "No";
        if(!response[1].contentEquals(""))
            weGotJSON = "Yes";

        //TODO: put this data somewhere, maybe some intent result stuff
        Log.d("API CALLER RESPONSE: ", response[0]);
        Log.d("JSON RECEIVED: ", weGotJSON);
    }

    public String[] makeCall(){
        int responseCode = 0;
        StringBuilder json = new StringBuilder();
        json.append("");
        String request = intent.getStringExtra("requestUrl");
        try {
            URL url = new URL(request);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(intent.getStringExtra("method"));
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            responseCode = conn.getResponseCode();

            if (intent.getBooleanExtra("json", false)) {
                InputStream in = new BufferedInputStream((conn.getInputStream()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while((line = reader.readLine()) != null){
                    json.append(line);
                }
            }
        }
        catch (IOException e){
            Log.d("API_CALLER_ERROR", "makeCall failure", e);
        }
        finally{
            conn.disconnect();
        }
        String[] output = {"" + responseCode, json.toString()};
        return output;
    }
}