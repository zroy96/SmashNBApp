package ca.unb.smashnbapp;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ApiCaller extends IntentService {

    private Intent intent;

    public ApiCaller() {
        super("I DONT KNOW WHY I NEED THIS STRING");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        this.intent = intent;
        int responseCd = makeCall();
        Log.d("API RESPONSE CODE", Integer.toString(responseCd));
    }

    public int makeCall(){
        int responseCode = 0;
        String request = intent.getStringExtra("requestUrl");
        String type = intent.getStringExtra("type");
        String[] headers = intent.getStringArrayExtra("responseHeaders");
        String[] headerResponses = {"ass"};
        try{
            URL url = new URL(request);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(type);
            conn.setConnectTimeout(8*1000);
            conn.setReadTimeout(8*1000);
            responseCode = conn.getResponseCode();


            //get specified response headers (if any)
            if(headers != null){
                headerResponses = new String[headers.length];
                for (int i = 0; i < headers.length; i++) {
                    headerResponses[i] = getField(conn, headers[i]);
                }
            }

            //HEADER RESPONSES ARE NOW FILLED IN BUT I DON'T KNOW WHERE TO SEND THEM YET

            conn.disconnect();
        }
        catch (IOException e){
            Log.d("API_CALLER_ERROR", "makeCall failure", e);
        }
        catch (Exception e){
            Log.d("API_CALLER_ERROR", "something went terribly wrong in makeCall", e);
        }
        return responseCode;
    }




    private String getField(HttpURLConnection connection, String field){ //THIS IS A SHORTCUT THAT COULD RETURN A WRONG STRING IF A TOURNAMENT/PARTICIPANT SETS THEIR NAME TO A RESPONSE HEADER
        String buffer = "error?";
        try {
            InputStream in = connection.getInputStream();
            Scanner scan = new Scanner(in);
            scan.useDelimiter(field);
            buffer = scan.next();
            scan.useDelimiter(",");
            buffer = scan.next();
            in.close();
            scan.close();
            buffer = buffer.replace("\"", "");
        }
        catch (IOException e){
            Log.d("API_CALLER_ERROR", "getField failure", e);
        }
        catch (Exception e){
            Log.d("API_CALLER_ERROR", "something went terribly wrong in getField", e);
        }
        return buffer;
    }
}