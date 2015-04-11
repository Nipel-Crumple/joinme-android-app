package edu.phystech.varnavski.joinme.processors;

import android.util.Log;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Johnny D on 11.04.2015.
 */
public class LoginProcessor implements Processor{


    private URL url;
    public String serverMessage = "";

    public LoginProcessor(URL url) {
        this.url = url;
    }

    @Override
    public void run() {
        HttpClient client = new DefaultHttpClient();

        try {
            HttpGet httpGet = new HttpGet(String.valueOf(url));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            serverMessage = client.execute(httpGet, responseHandler);
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
