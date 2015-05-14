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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Johnny D on 11.04.2015.
 */
public class LoginProcessor implements Runnable {

    private static final String TAG = LoginProcessor.class.getSimpleName();

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

        URL url = null;
        setError(false);

        if (type == Type.LOGIN) {
            try {
                url = buildLoginUrl();
                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                String responseString = getHTTPSContent(con);
                if (responseString.equals(null)) {
                    serverMessage = null;
                    setError(true);
                } else {
                    Log.d(TAG, "response string" + responseString);
                    JSONObject dataJSON = new JSONObject(responseString);
                    JSONObject error = dataJSON.optJSONObject("error");

                    if (error == null) {
                        serverMessage = dataJSON.getString("token");
                        Log.d("Token in Login ", serverMessage);
                    } else {
                        serverMessage = null;
                        setError(true);
                    }
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

        } /*else if (type == Type.REGISTER) {
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
            } catch (UnsupportedEncodingException e) {
                setError(true);
                serverMessage = null;
            } catch (IOException e) {
                setError(true);
                serverMessage = null;
            } catch (JSONException e) {
                setError(true);
                serverMessage = null;
            }

        }*/

        Log.d("Server response:", "msg= " + serverMessage);

    }

    private URL buildLoginUrl() throws MalformedURLException {
        String urlString = null;
        URL url = null;
        if (email != null && password != null) {
            urlString = "https://joinmipt.com/api/login?login=" +
                    email + "&password=" + password;
            url = new URL(urlString);
        }
        Log.d("String to send:", urlString);
        return url;
    }

    private URL buildRegisterUrl() throws MalformedURLException {
        String urlString = "https://joinmipt.com/api/reg/";
        URL url = new URL(urlString);
        Log.d("String to send:", urlString);
        return url;
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

