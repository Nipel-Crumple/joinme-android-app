package com.joinme.processors;

import android.content.SharedPreferences;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johnny D on 11.04.2015.
 */
public class LoginProcessor implements Runnable {

    private String token;
    private String serverMessage = "";
    private boolean isError;

    public boolean isError() {
        return isError;
    }

    public void setError(boolean isError) {
        this.isError = isError;
    }

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

        HttpClient client = new DefaultHttpClient();

        String url = null;
        setError(false);

        if (type == Type.LOGIN) {
            try {
                url = buildLoginUrl();
                HttpGet httpGet = new HttpGet(url);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                Log.d("GET to signin", "" + url);
                String responseString = client.execute(httpGet, responseHandler);
                JSONObject dataJSON = new JSONObject(responseString);
                JSONObject error = dataJSON.optJSONObject("error");

                if (error == null) {
                    serverMessage = dataJSON.getString("token");
                    Log.d("Token in Login ", serverMessage);
                } else {
                    serverMessage = null;
                    setError(true);
                }
            } catch (MalformedURLException e) {
                serverMessage = null;
                setError(true);
                Log.d("Login failed", "isError TRU1");
            } catch (JSONException e) {
                serverMessage = null;
                setError(true);
                Log.d("Login failed", "isError TRUE");
            } catch (ClientProtocolException e) {
                serverMessage = null;
                setError(true);
                Log.d("Login failed", "isError TRUE");
            } catch (IOException e) {
                serverMessage = null;
                setError(true);
                Log.d("Login failed", "isError TRUE");
            }

        } else if (type == Type.REGISTER) {
            try {
                url = buildRegisterUrl();
                HttpPost httpPost = new HttpPost(url);
                // Execute HTTP Post Request
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("email", email));
                params.add(new BasicNameValuePair("password", password));
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                HttpResponse response = client.execute(httpPost);
                String responseString = EntityUtils.toString(response.getEntity());
                JSONObject dataJSON = new JSONObject(responseString);
                JSONObject error = dataJSON.optJSONObject("error");

                if (error == null) {
                    serverMessage = dataJSON.getString("token");
                } else {
                    setError(true);
                    serverMessage = null;
                }
            }  catch (UnsupportedEncodingException e) {
                setError(true);
                serverMessage = null;
            } catch (IOException e) {
                setError(true);
                serverMessage = null;
            } catch (JSONException e) {
                setError(true);
                serverMessage = null;
            }

        }

        Log.d("Server response:", "msg= " + serverMessage);

    }

    private String buildLoginUrl() throws MalformedURLException {
        String urlString = null;
        if (email != null && password != null) {
            urlString = "https://joinmipt.com/api/login?login=" +
                    email + "&password=" + password;
        }
        Log.d("String to send:", urlString);
        return urlString;
    }

    private String buildRegisterUrl() throws MalformedURLException {
        String urlString = "https://joinmipt.com/api/reg/";

        Log.d("String to send:", urlString);
        return urlString;
    }


/*    private String getCSRFToken() throws IOException {
        String urlString = "https://joinmipt.com/joinme/api/csrf/";
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
    }*/

    public String getServerMessage() {
        return serverMessage;
    }
}
