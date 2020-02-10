package ca.unb.smashnbapp;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiCaller extends IntentService {

    //hardcoding like this is kind of WeirdChamp
    private final String API_KEY = "IzljIdLTolMZsQBWFYlkCCB16u4ES8T3u20IRMbZ";
    private final String BASEURL = "https://api.challonge.com/v1/";
    private final String TOURNAMENT = "DicksBroLOL";
    private final String NAME = "TESTERINO_BEN!?!?";

    public ApiCaller() {
        super("I DONT KNOW WHY I NEED THIS STRING");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int responseCd = addParticipant();
        Log.d("API CALLER RESPONSE", Integer.toString(responseCd));
    }

    public int addParticipant(){
        int responseCode = 0;
        try{
            String request = BASEURL + "tournaments/" + TOURNAMENT + "/participants.json" + "?api_key=" + API_KEY + "&participant[name]=" + NAME;
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