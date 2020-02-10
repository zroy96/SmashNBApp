package ca.unb.smashnbapp;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class ApiHandler{

    //hardcoding like this is kind of WeirdChamp
    private final String API_KEY = "IzljIdLTolMZsQBWFYlkCCB16u4ES8T3u20IRMbZ";
    private final String BASEURL = "https://api.challonge.com/v1/";
    private final String TOURNAMENT = "DicksBroLOL";

    public ApiHandler(){
        //Nothing to do here rn lol
    }

    public void addParticipant(Context main, String name){
        String request = BASEURL + "tournaments/" + TOURNAMENT + "/participants.json" + "?api_key=" + API_KEY + "&participant[name]=" + name;
        Intent apiIntent = new Intent();
        apiIntent.setClass(main, ApiCaller.class);
        apiIntent.putExtra("requestUrl", request);
        main.startService(apiIntent);
    }

    public void randomizeSeeds(Context main){
        String request = BASEURL + "tournaments/" + TOURNAMENT + "/participants/randomize.json" + "?api_key=" + API_KEY;
        Intent apiIntent = new Intent();
        apiIntent.setClass(main, ApiCaller.class);
        apiIntent.putExtra("requestUrl", request);
        main.startService(apiIntent);
    }

}
