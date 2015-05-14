package com.joinme.processors;

import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import junit.framework.Assert;

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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
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
import java.util.Scanner;

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
                    String error = dataJSON.optString("error");
                    if (error.isEmpty()) {
                        serverMessage = dataJSON.getString("token");
                        Log.d("Token in Login ", serverMessage);
                    } else {
                        serverMessage = error;
                        Log.d("Error in login ", serverMessage);
                        setError(true);
                    }
                }
            } catch (MalformedURLException e) {
                serverMessage = null;
                setError(true);
                Log.d("Login failed", "isError TRUE1");
            } catch (JSONException e) {
                serverMessage = null;
                setError(true);
                Log.d("Login failed", "isError TRUE2");
            } catch (ClientProtocolException e) {
                serverMessage = null;
                setError(true);
                Log.d("Login failed", "isError TRUE3");
            } catch (IOException e) {
                serverMessage = null;
                setError(true);
                Log.d("Login failed", "isError TRUE4");
            }

        } else if (type == Type.REGISTER) {
            try {
                HttpsURLConnection conn;
                url = buildRegisterUrl();
                String param = "email=" + URLEncoder.encode(email, "UTF-8") +
                        "&password=" + URLEncoder.encode(password, "UTF-8");
                conn = (HttpsURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setFixedLengthStreamingMode(param.getBytes().length);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                PrintWriter out = new PrintWriter(conn.getOutputStream());
                out.print(param);
                out.close();

                String response = "";
                Scanner inStream = new Scanner(conn.getInputStream());
                while (inStream.hasNextLine())
                    response += (inStream.nextLine());

                serverMessage = response;
                JSONObject dataJSON = new JSONObject(response);
                String error = dataJSON.optString("error");
                if (error == null) {
                    serverMessage = dataJSON.getString("token");
                    Log.d("Token in Login ", serverMessage);
                } else {
                    serverMessage = error;
                    setError(true);
                }
                Log.d("Response from Register", response.toString());
            } catch (MalformedURLException e) {
                serverMessage = null;
                setError(true);
                e.printStackTrace();
            } catch (ProtocolException e) {
                serverMessage = null;
                setError(true);
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                serverMessage = null;
                setError(true);
                e.printStackTrace();
            } catch (IOException e) {
                serverMessage = null;
                setError(true);
                e.printStackTrace();
            } catch (JSONException e) {
                serverMessage = null;
                setError(true);
                e.printStackTrace();
            }
        }

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

    private InputStream getInputStream(String urlStr, String user, String password) throws IOException, KeyManagementException {
        URL url = new URL(urlStr);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

        // Create the SSL connection
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        sc.init(null, null, new java.security.SecureRandom());
        conn.setSSLSocketFactory(sc.getSocketFactory());

        // Use this if you need SSL authentication
        String userpass = user + ":" + password;
        String basicAuth = "Basic " + Base64.encodeToString(userpass.getBytes(), Base64.DEFAULT);
        conn.setRequestProperty("Authorization", basicAuth);

        // set Timeout and method
        conn.setReadTimeout(7000);
        conn.setConnectTimeout(7000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);

        // Add any data you wish to post here

        conn.connect();
        return conn.getInputStream();
    }
}

