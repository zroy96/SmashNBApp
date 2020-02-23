package ca.unb.smashnbapp;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ApiHandler{

    //hardcoding like this is kind of WeirdChamp
    private final String API_KEY = "IzljIdLTolMZsQBWFYlkCCB16u4ES8T3u20IRMbZ";
    private final String BASEURL = "https://api.challonge.com/v1/";
    private String TOURNAMENT = "DicksBroLOL";

    public ApiHandler(Context main, String city){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date yest = cal.getTime();
        String yestString = dateFormat.format(yest);

        //look at pending tournaments created within the last day
        //and find name base on city
        findTournamentName(main, city, yestString);
    }

    public void addParticipant(Context main, String name){
        String request = BASEURL + "tournaments/" + TOURNAMENT + "/participants.json" + "?api_key=" + API_KEY + "&participant[name]=" + name;
        Intent apiIntent = new Intent();
        apiIntent.setClass(main, ApiCaller.class);
        apiIntent.putExtra("requestUrl", request);
        apiIntent.putExtra("method", "POST");
        main.startService(apiIntent);
    }

    public void randomizeSeeds(Context main){
        String request = BASEURL + "tournaments/" + TOURNAMENT + "/participants/randomize.json" + "?api_key=" + API_KEY;
        Intent apiIntent = new Intent();
        apiIntent.setClass(main, ApiCaller.class);
        apiIntent.putExtra("requestUrl", request);
        apiIntent.putExtra("method", "POST");
        main.startService(apiIntent);
    }

    public void findTournamentName(Context main, String city, String date)
    {
        String request = BASEURL + "tournaments.json" + "?api_key=" + API_KEY
                + "&state=pending&created_after=" + date;
        Intent apiIntent = new Intent();
        apiIntent.setClass(main, ApiCaller.class);
        apiIntent.putExtra("requestUrl", request);
        apiIntent.putExtra("method", "GET");
        main.startService(apiIntent);

        //TODO: get back json and game tournament names until it matches the right one for the city
        //use switch to set search string

    }

}
