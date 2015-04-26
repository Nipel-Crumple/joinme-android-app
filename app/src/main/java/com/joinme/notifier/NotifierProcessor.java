package com.joinme.notifier;

import android.util.Log;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Johnny D on 26.04.2015.
 */
public class NotifierProcessor implements Runnable {
    private JSONObject jsonResponse;
    private String[] events;
    private String subscriptionAmount;
    private String organizerEvent;
    private String token;

    public NotifierProcessor(String token) {
        this.token = token;
    }

    public String getOrganizerEvent() {
        return organizerEvent;
    }

    public void setOrganizerEvent(String organizerEvent) {
        this.organizerEvent = organizerEvent;
    }

    public String getSubscriptionAmount() {
        return subscriptionAmount;
    }

    public void setSubscriptionAmount(String subscriptionAmount) {
        this.subscriptionAmount = subscriptionAmount;
    }

    public String[] getEvents() {
        return events;
    }

    public void setEvents(String[] events) {
        this.events = events;
    }

    public String buildGetNotifierUrl() {
        String url = "http://master-igor.com/joinme/api/events/next/?token=" + token;
        Log.d("Request while start app", url);
        return url;
    }

    @Override
    public void run() {
        HttpClient client = new DefaultHttpClient();
        String url = buildGetNotifierUrl();
        HttpGet httpGet = new HttpGet(url);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseString = null;

        try {
            responseString = client.execute(httpGet, responseHandler);
            Log.d("Notifier response", responseString);

            JSONObject data = new JSONObject(responseString);
            JSONObject error = data.optJSONObject("error");
            JSONObject response = data.optJSONObject("response");

            if (response == null && error != null) {
                Log.d("Error to get notify", error.toString());
            } else {
                subscriptionAmount = response.getString("count");
                Log.d("subscriptionAmount", subscriptionAmount);
                organizerEvent = response.getString("count_author");
                Log.d("organizerEvent", organizerEvent);
                JSONArray array = response.getJSONArray("categories");
                Log.d("ARRAY size", String.valueOf(array.length()));
                String[] tempCategoryArray = new String[array.length()];

                for (int i = 0; i < array.length(); i++) {
                    tempCategoryArray[i] = array.getString(i);
                    /*Log.d("ARRAY", tempCategoryArray[i]);*/
                }
                events = tempCategoryArray;
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}
