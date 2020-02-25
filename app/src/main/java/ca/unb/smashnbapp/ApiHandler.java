package ca.unb.smashnbapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ApiHandler{

    //hardcoding like this is kind of WeirdChamp
    private final String API_KEY = "IzljIdLTolMZsQBWFYlkCCB16u4ES8T3u20IRMbZ";
    private final String BASEURL = "https://api.challonge.com/v1/";
    public String tournamentUrl = "hvn680y0"; //url
    public String tournamentId = "8138483";
    public String tournamentName = "The guard 111111";
    private Context appContext;
    public String yesterdayDate;

    public ApiHandler(Context main){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date yest = cal.getTime();
        yesterdayDate = dateFormat.format(yest);
        yesterdayDate = "2020-02-21";
        appContext = main;
    }

    public void addParticipant(String name){
        String request = BASEURL + "tournaments/" + tournamentUrl + "/participants.json?api_key=" + API_KEY + "&participant[name]=" + name;
        Intent apiIntent = new Intent();
        apiIntent.setClass(appContext, ApiCaller.class);
        apiIntent.putExtra("requestUrl", request);
        apiIntent.putExtra("endPoint", "participant");
        apiIntent.putExtra("type", "POST");
        apiIntent.putExtra("method", "addParticipant");
        apiIntent.putExtra("expectJson", true);
        appContext.startService(apiIntent);
    }

    public void getParticipant(String id, String method){
        String request = BASEURL + "tournaments/" + tournamentUrl + "/participants/" + id + ".json?api_key=" + API_KEY;
        Intent apiIntent = new Intent();
        apiIntent.setClass(appContext, ApiCaller.class);
        apiIntent.putExtra("requestUrl", request);
        apiIntent.putExtra("endPoint", "participant");
        apiIntent.putExtra("type", "GET");
        apiIntent.putExtra("method", "getParticipant");
        apiIntent.putExtra("expectJson", true);
        appContext.startService(apiIntent);
    }

    public void randomizeSeeds(){
        String request = BASEURL + "tournaments/" + tournamentUrl + "/participants/randomize.json?api_key=" + API_KEY;
        Intent apiIntent = new Intent();
        apiIntent.setClass(appContext, ApiCaller.class);
        apiIntent.putExtra("requestUrl", request);
        apiIntent.putExtra("endPoint", "participant");
        apiIntent.putExtra("type", "POST");
        apiIntent.putExtra("method", "randomizeSeeds");
        appContext.startService(apiIntent);
    }

    public void findTournamentName(String date)
    {
        Log.d("findTournamentName", "called");
        String request = BASEURL + "tournaments.json?api_key=" + API_KEY
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
        String request = BASEURL + "tournaments/" + tournamentUrl + ".json?api_key=" + API_KEY;
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
        String request = BASEURL + "tournaments/" + id + ".json?api_key=" + API_KEY;
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

    public void getMatches(){
        String request = BASEURL + "tournaments/" + tournamentId + "/matches.json?api_key=" + API_KEY;
        Intent apiIntent = new Intent();
        apiIntent.setClass(appContext, ApiCaller.class);
        apiIntent.putExtra("requestUrl", request);
        apiIntent.putExtra("endPoint", "match");
        apiIntent.putExtra("type", "GET");
        apiIntent.putExtra("method", "getMatches");
        apiIntent.putExtra("expectJson", true);
        appContext.startService(apiIntent);
    }

    public void updateScore(String matchId, int playerNum, int playerScore, int opponentScore){
        String score = "";

        if(playerNum == 1)
            score = "" + playerScore + "-" + opponentScore;
        else
            score = "" + opponentScore + "-" + playerScore;

        String request = BASEURL + "tournaments/" + tournamentId + "/matches/" + matchId + ".json?api_key=" + API_KEY;
        Intent apiIntent = new Intent();
        apiIntent.setClass(appContext, ApiCaller.class);
        apiIntent.putExtra("requestUrl", request);
        apiIntent.putExtra("endPoint", "match");
        apiIntent.putExtra("type", "PUT");
        apiIntent.putExtra("method", "updateScore");
        apiIntent.putExtra("expectJson", true);
        appContext.startService(apiIntent);
    }
}