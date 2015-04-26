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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johnny D on 25.04.2015.
 */
public class EventProcessor implements Runnable {

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

    public String buildGetCardsURL() {
        String url = "http://master-igor.com/joinme/api/events/?token=" + token
                + "&category=" + category;
        Log.d("Request url: ", url + "");
        return url;
    }

    public String buildJoinURL() {
        return "http://master-igor.com/joinme/api/event/join/?token=" + token +
                "&id=" + eventId;
    }

    public String buildLeaveURL() {
        return "http://master-igor.com/joinme/api/event/leave/?token=" + token +
                "&id=" + eventId;
    }

    public String buildDeleteURL() {
        return "http://master-igor.com/joinme/api/events/";
    }
    @Override
    public void run() {
        HttpClient client = new DefaultHttpClient();
        String url;
        if (this.category == null) {
            if (this.action.equals("JOIN")) {
                url = buildJoinURL();
                Log.d("JOIN URL: ", url);
            }
            else if (this.action.equals("LEAVE")) {
                url = buildLeaveURL();
                Log.d("LEAVE URL: ", url);
            } else {
                url = buildDeleteURL();
                Log.d("DELETE URL: ", url);
            }
        } else {
            url = buildGetCardsURL();
            Log.d("GET CARDS URL: ", url);
        }
        HttpGet httpGet = new HttpGet(url);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseString = null;
        try {
            responseString = client.execute(httpGet, responseHandler);
            Log.d("Response: ", responseString);
            JSONObject dataJSON = new JSONObject(responseString);
            this.jsonResponse = dataJSON;
            Log.d("json Response=", getJsonResponse().toString());
            JSONObject error = dataJSON.optJSONObject("error");
            if (error == null) {
                serverMessage = dataJSON.toString();
                Log.d("Server response: ", serverMessage);
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
}
