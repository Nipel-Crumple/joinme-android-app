package com.joinme.processors;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.jar.Attributes;

/**
 * Created by Johnny D on 11.04.2015.
 */
public class LoginProcessor implements Runnable {

    private String token;
    private String serverMessage = "";
    private String email;
    private String password;

    public enum Type {
        LOGIN, REGISTER
    }

    private Type type;
    public LoginProcessor(String email, String password, Type type) {
        this.email = email;
        this.password = password;
        this.type = type;
    }

    @Override
    public void run() {

        //getting token in not the main thread
//        try {
//            token = getCSRFToken();
//            Log.d("token= ", "" + token);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        HttpClient client = new DefaultHttpClient();

        try {
            String url = null;

            switch(type) {
                case LOGIN:
                    url = buildLoginUrl();
                    HttpGet httpGet = new HttpGet(url);
                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    Log.d("GET to signin", "" + url);
                    serverMessage = client.execute(httpGet, responseHandler);
                    break;
                case REGISTER:
                    url = buildRegisterUrl();
                    HttpPost httpPost = new HttpPost(url);
                    // Execute HTTP Post Request
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("email", email));
                    params.add(new BasicNameValuePair("password", password));
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                    HttpResponse response = client.execute(httpPost);
                    serverMessage = EntityUtils.toString(response.getEntity());
                    break;
            }
            Log.d("Server response:", "msg= " + serverMessage);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String buildLoginUrl() throws MalformedURLException {
        String urlString = null;
        if (email != null && password != null) {
            urlString = "http://master-igor.com/joinme/api/login?login=" +
                    email + "&password=" + password;
        }
        Log.d("String to send:", urlString);
        return urlString;
    }

    private String buildRegisterUrl() throws MalformedURLException {
        String urlString = "http://master-igor.com/joinme/api/reg/";

        Log.d("String to send:", urlString);
        return urlString;
    }


    private String getCSRFToken() throws IOException {
        String urlString = "http://master-igor.com/joinme/api/csrf/";
        Log.d("String to send:", urlString);

        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(urlString);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String response = client.execute(httpGet, responseHandler);

        String token = null;
        try {
            JSONObject json = new JSONObject(response);
            token = (String) json.get("token");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return token;
    }

    public String getServerMessage() {
        return serverMessage;
    }
}
