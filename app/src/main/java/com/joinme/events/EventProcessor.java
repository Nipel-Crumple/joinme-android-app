package com.joinme.events;

import android.app.Activity;
import android.app.usage.UsageEvents;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Johnny D on 25.04.2015.
 */
public class EventProcessor implements Runnable {
    private static final String TAG = EventProcessor.class.getSimpleName();

    private String category;
    private String token;
    private String serverMessage;
    private String action;
    private JSONObject jsonResponse;
    private String eventId;

    public EventProcessor(Bundle bundle, String token) {
        this.category = bundle.getString("category");
        this.token = token;
    }

    public EventProcessor(String token, String action, String eventId) {
        this.action = action;
        this.token = token;
        this.eventId = eventId;
    }

    public URL buildGetCardsURL() {
        URL url = null;
        String urlString = "https://joinmipt.com/api/events/?token=" + token
                + "&category=" + category;
        Log.d("Request url: ", urlString + "");
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.d(TAG, "Can' create get cards URL");
        }

        return url;
    }

    public URL buildJoinURL() {
        URL url = null;
        String urlString = "https://joinmipt.com/api/event/join/?token=" + token +
                "&id=" + eventId;
        Log.d("Request url: ", urlString + "");
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.d(TAG, "Can' create get cards URL");
        }

        return url;
    }

    public URL buildLeaveURL() {
        URL url = null;
        String urlString = "https://joinmipt.com/api/event/leave/?token=" + token +
                "&id=" + eventId;
        Log.d("Request url: ", urlString + "");
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.d(TAG, "Can' create get cards URL");
        }

        return url;
    }

    public URL buildDeleteURL() {
        URL url = null;
        String urlString = "https://joinmipt.com/api/event/delete/?token=" + token +
                "&id=" + eventId;

        Log.d("Request url: ", urlString + "");
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.d(TAG, "Can' create get cards URL");
        }

        return url;
    }

    @Override
    public void run() {
        URL url;
        if (this.category == null) {
            if (this.action.equals("ПРИСОЕДИНИТЬСЯ")) {
                url = buildJoinURL();
                Log.d("JOIN URL: ", url.toString());
            } else if (this.action.equals("ПОКИНУТЬ")) {
                url = buildLeaveURL();
                Log.d("LEAVE URL: ", url.toString());
            } else {
                url = buildDeleteURL();
                Log.d("DELETE URL: ", url.toString());
            }
        } else {
            url = buildGetCardsURL();
            Log.d("GET CARDS URL: ", url.toString());
        }
        try {
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            String responseString = getHTTPSContent(con);
//            Log.d("Response: ", responseString);
            JSONObject dataJSON = new JSONObject(responseString);
            this.jsonResponse = dataJSON;
//            Log.d("json Response=", getJsonResponse().toString());
            JSONObject error = dataJSON.optJSONObject("error");
            if (error == null) {
                serverMessage = dataJSON.toString();
//                Log.d("Server response: ", serverMessage);
            } else {
                serverMessage = error.getString("error");
                Log.d("Error: ", serverMessage);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }


    public JSONObject getJsonResponse() {
        return jsonResponse;
    }

    private String getHTTPSContent(HttpsURLConnection con) {
        int capacity = 200;
        String input = null;
        StringBuilder stringBuilder = new StringBuilder(capacity);
        if (con != null) {

            try {
                BufferedReader br =
                        new BufferedReader(
                                new InputStreamReader(con.getInputStream()));


                while ((input = br.readLine()) != null) {
                    stringBuilder.append(input);
                }
                br.close();
                return stringBuilder.toString();
            } catch (IOException e) {
                Log.d(TAG, "Can't retrieve data via HTTPS");
            }

        }
        return null;
    }
}
