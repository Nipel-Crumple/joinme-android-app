package edu.phystech.varnavski.joinme.signin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;

import edu.phystech.varnavski.joinme.R;
import edu.phystech.varnavski.joinme.SuccessLogin;
import edu.phystech.varnavski.joinme.processors.LoginProcessor;
import edu.phystech.varnavski.joinme.processors.RegisterProcessor;

/**
 * Created by Johnny D on 11.04.2015.
 */
public class SignInActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set view login.xml
        setContentView(R.layout.login);

        Button login = (Button) findViewById(R.id.btnLogin);
        Button register = (Button) findViewById(R.id.btnRegister);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = ((EditText) findViewById(R.id.email)).getText().toString();
                String password = ((EditText) findViewById(R.id.password)).getText().toString();
                String urlString = "http://master-igor.com/findme/test/api?login=" +
                        email + "&password=" + password;
                Log.d("String to send:", urlString);
                String serverResponse = null;
                try {
                    URL url = new URL(urlString);
                    LoginProcessor loginRequest = new LoginProcessor(url);
                    Log.d("Sending login request ", email);
                    Thread thr = new Thread(loginRequest);
                    thr.start();
                    thr.join();
                    serverResponse = loginRequest.getServerMessage();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (serverResponse != null) {
                    Log.d("Successful login ", email);
                    Intent i = new Intent(getApplicationContext(), SuccessLogin.class);
                    startActivity(i);
                } else {
                    Log.d("Cannot login: ", email);
                }

            }
        });
    }
}
