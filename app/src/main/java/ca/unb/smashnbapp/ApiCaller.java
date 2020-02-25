package ca.unb.smashnbapp;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiCaller extends IntentService {

    private Intent intent;
    private HttpURLConnection conn;
    Bitmap bmp;

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
        Log.d("JSON RECEIVED? : ", weGotJSON);
    }

    public String[] makeCall(){
        int responseCode = 0;
        StringBuilder json = new StringBuilder();
        json.append("");
        String request = intent.getStringExtra("requestUrl");
        String method = intent.getStringExtra("method");
        String type = intent.getStringExtra("type");
        String endPoint = intent.getStringExtra("endPoint");
        boolean expectJson = intent.getBooleanExtra("expectJson", false);
        try {
            URL url = new URL(request);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(type);
            conn.setConnectTimeout(6*1000);
            conn.setReadTimeout(6*1000);
            responseCode = conn.getResponseCode();

            if (expectJson) {
                InputStream in = new BufferedInputStream((conn.getInputStream())); //here we catch a FileNotFoundException when the response code != 200, kinda weirdchamp ngl
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while((line = reader.readLine()) != null){
                    json.append(line);
                }

                if(method.equals("viewBracket")){
                    try {
                        JSONObject jsnreader = new JSONObject(json.toString());
                        JSONObject jsnobjct = jsnreader.getJSONObject("tournament");
                        URL image_url = new URL(jsnobjct.getString("live_image_url"));
                        bmp = BitmapFactory.decodeStream(image_url.openConnection().getInputStream());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                sendBroadcast(endPoint, method, responseCode, json.toString());



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

    private void sendBroadcast (String endPoint, String method, int responseCode, String json){
        Intent intent = new Intent ("message"); //put the same message as in the filter you used in the activity when registering the receiver
        intent.putExtra("endPoint", endPoint);
        intent.putExtra("method", method);
        intent.putExtra("responseCode", responseCode);
        intent.putExtra("json", json);
        intent.putExtra("bitmap", bmp);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}