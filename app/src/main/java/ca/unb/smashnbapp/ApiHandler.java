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
    public String TOURNAMENT = "DicksBroLOL";
    public String tournamentId = "";
    private Context appContext;

    public ApiHandler(Context main, String city){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date yest = cal.getTime();
        String yestString = dateFormat.format(yest);
        appContext = main;

        //look at pending tournaments created within the last day
        //and find name base on city
        //findTournamentName(main, city, yestString);
    }

    public void addParticipant(String name){
        String request = BASEURL + "tournaments/" + TOURNAMENT + "/participants.json" + "?api_key=" + API_KEY + "&participant[name]=" + name;
        Intent apiIntent = new Intent();
        apiIntent.setClass(appContext, ApiCaller.class);
        apiIntent.putExtra("requestUrl", request);
        apiIntent.putExtra("endPoint", "participant");
        apiIntent.putExtra("type", "POST");
        apiIntent.putExtra("expectJson", true);
        appContext.startService(apiIntent);
    }

    public void getParticipant(String id, String method){
        String request = BASEURL + "tournaments/" + TOURNAMENT + "/participants/" + id + ".json" + "?api_key=" + API_KEY;
        Intent apiIntent = new Intent();
        apiIntent.setClass(appContext, ApiCaller.class);
        apiIntent.putExtra("requestUrl", request);
        apiIntent.putExtra("endPoint", "participant");
        apiIntent.putExtra("type", "GET");
        apiIntent.putExtra("expectJson", true);
        appContext.startService(apiIntent);
    }

    public void randomizeSeeds(){
        String request = BASEURL + "tournaments/" + TOURNAMENT + "/participants/randomize.json" + "?api_key=" + API_KEY;
        Intent apiIntent = new Intent();
        apiIntent.setClass(appContext, ApiCaller.class);
        apiIntent.putExtra("requestUrl", request);
        apiIntent.putExtra("endPoint", "participant");
        apiIntent.putExtra("type", "POST");
        apiIntent.putExtra("method", "randomizeSeeds");
        appContext.startService(apiIntent);
    }

    public void findTournamentName(String city, String date)
    {
        String request = BASEURL + "tournaments.json" + "?api_key=" + API_KEY
                + "&state=pending&created_after=" + date;
        Intent apiIntent = new Intent();
        apiIntent.setClass(appContext, ApiCaller.class);
        apiIntent.putExtra("requestUrl", request);
        apiIntent.putExtra("endPoint", "tournament");
        apiIntent.putExtra("type", "GET");
        apiIntent.putExtra("method", "findTournamentName");
        apiIntent.putExtra("expectJson", true);
        appContext.startService(apiIntent);
}

    public void getTournamentByName(String method){
        String request = BASEURL + "tournaments/" + TOURNAMENT + ".json" + "?api_key=" + API_KEY;
        Intent apiIntent = new Intent();
        apiIntent.setClass(appContext, ApiCaller.class);
        apiIntent.putExtra("requestUrl", request);
        apiIntent.putExtra("endPoint", "tournament");
        apiIntent.putExtra("type", "GET");
        apiIntent.putExtra("method", method);
        apiIntent.putExtra("expectJson", true);
        appContext.startService(apiIntent);
    }

    //use for hasTournamentStarted
    public void getTournamentById(String id, String method){
        String request = BASEURL + "tournaments/" + id + ".json" + "?api_key=" + API_KEY;
        Intent apiIntent = new Intent();
        apiIntent.setClass(appContext, ApiCaller.class);
        apiIntent.putExtra("requestUrl", request);
        apiIntent.putExtra("endPoint", "tournament");
        apiIntent.putExtra("type", "GET");
        apiIntent.putExtra("method", method);
        apiIntent.putExtra("expectJson", true);
        appContext.startService(apiIntent);
    }

    //TODO: called periodically? at 5:00, 5:30, 6:00 etc?
    public void checkTournamentStarted(){
        getTournamentByName("checkTournamentStarted");
    }

    public void getMatches(String tournamentId){
        String request = BASEURL + "tournaments/" + tournamentId + "/matches.json" + "?api_key=" + API_KEY;
        Intent apiIntent = new Intent();
        apiIntent.setClass(appContext, ApiCaller.class);
        apiIntent.putExtra("requestUrl", request);
        apiIntent.putExtra("endPoint", "match");
        apiIntent.putExtra("type", "GET");
        apiIntent.putExtra("method", "getMatches");
        apiIntent.putExtra("expectJson", true);
        appContext.startService(apiIntent);
    }
}