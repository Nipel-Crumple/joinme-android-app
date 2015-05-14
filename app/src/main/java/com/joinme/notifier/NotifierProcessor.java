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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Johnny D on 26.04.2015.
 */
public class NotifierProcessor implements Runnable {

    private static final String TAG = NotifierProcessor.class.getSimpleName();

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

    public URL buildGetNotifierUrl() {
        String urlString = "https://joinmipt.com/api/events/next/?token=" + token;
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.d(TAG, "Can't get notifications");
        }
        Log.d("Request while start app", urlString);
        return url;
    }

    @Override
    public void run() {
        HttpClient client = new DefaultHttpClient();
        URL url = buildGetNotifierUrl();
        try {
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            String responseString = getHTTPSContent(con);
            Log.d("Notifier response", responseString);

            JSONObject data = new JSONObject(responseString);
            String error = "";
            try {
                data.getString("error");
            } catch (JSONException e) {
                error = "";
            }
            JSONObject response = data.optJSONObject("response");

            if (response == null && !error.equals("")) {
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
