package com.joinme.signin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.gc.materialdesign.views.ButtonFlat;
import com.joinme.Category;
import com.joinme.R;
import com.joinme.processors.LoginProcessor;

/**
 * Created by Johnny D on 11.04.2015.
 */
public class SignInActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set view login.xml
        setContentView(R.layout.login);

        final ButtonFlat login = (ButtonFlat) findViewById(R.id.btnLogin);
        final ButtonFlat register = (ButtonFlat) findViewById(R.id.btnRegister);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = ((EditText) findViewById(R.id.email)).getText().toString();
                String password = ((EditText) findViewById(R.id.password)).getText().toString();
                LoginProcessor loginProcessor = new LoginProcessor(email, password, LoginProcessor.Type.REGISTER);
                Thread thr = new Thread(loginProcessor);
                thr.start();
                String serverResponse = null;
                try {
                    thr.join();
                    serverResponse = loginProcessor.getServerMessage();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (serverResponse != null) {
                    Log.d("Successful register ", email);
                    Intent i = new Intent(getApplicationContext(), Category.class);
                    startActivity(i);
                } else {
                    Log.d("Cannot login: ", email);
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = ((EditText) findViewById(R.id.email)).getText().toString();
                String password = ((EditText) findViewById(R.id.password)).getText().toString();
                LoginProcessor loginProcessor = new LoginProcessor(email, password, LoginProcessor.Type.LOGIN);
                Thread thr = new Thread(loginProcessor);
                thr.start();
                String serverResponse = null;

                try {
                    thr.join();
                    serverResponse = loginProcessor.getServerMessage();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (serverResponse != null) {
                    Log.d("Successful login ", email);
                    Intent i = new Intent(getApplicationContext(), Category.class);
                    startActivity(i);
                } else {
                    Log.d("Cannot login: ", email);
                }
            }
        });
    }
}
