package ca.unb.smashnbapp;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiCaller extends IntentService {

    private Intent intent;

    public ApiCaller() {
        super("I DONT KNOW WHY I NEED THIS STRING");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        this.intent = intent;
        int responseCd = addParticipant();
        Log.d("API CALLER RESPONSE", Integer.toString(responseCd));
    }

    public int addParticipant(){
        int responseCode = 0;
        String request = intent.getStringExtra("requestUrl");
        try{
            URL url = new URL(request);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            responseCode = conn.getResponseCode();
        }
        catch (IOException e){
            Log.d("API_CALLER_ERROR", "addParticipant failure", e);
        }
        return responseCode;
    }
}