package edu.phystech.varnavski.joinme.processors;

import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class RegisterProcessor implements Processor {

    private URL url;
    public String serverMessage = "";

    public RegisterProcessor(URL url) {
        this.url = url;
    }

    @Override
    public void run() {
        HttpClient client = new DefaultHttpClient();

        try {
            HttpPost httpPost = new HttpPost(String.valueOf(url));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            serverMessage = client.execute(httpPost, responseHandler);
            Log.d("SetServer", "getting" + serverMessage);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getServerMessage() {
        return serverMessage;
    }
}

