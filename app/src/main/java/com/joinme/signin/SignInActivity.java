package com.joinme.signin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFlat;
import com.joinme.categories.Category;
import com.joinme.R;
import com.joinme.notifier.CheckInternetNotifier;
import com.joinme.processors.LoginProcessor;

/**
 * Created by Johnny D on 11.04.2015.
 */
public class SignInActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.login);
        final ButtonFlat login = (ButtonFlat) findViewById(R.id.btnLogin);
        final ButtonFlat register = (ButtonFlat) findViewById(R.id.btnRegister);

        if (CheckInternetNotifier.isInternetAvailable(SignInActivity.this)) {
            //set view login.xml

            SharedPreferences sharedPreferences = getSharedPreferences("JoinMe", Activity.MODE_PRIVATE);
            String token = sharedPreferences.getString("JoinMeToken", "");

            if (!token.equals("")) {
                Intent i = new Intent(getApplicationContext(), Category.class);
                startActivity(i);
                finish();
            }


            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = ((EditText) findViewById(R.id.email)).getText().toString();
                    String password = ((EditText) findViewById(R.id.password)).getText().toString();

                    if (email.equals("") || password.equals("")) {
                        Context context = getApplicationContext();
                        Toast toast = Toast.makeText(context, "Please enter and password", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        LoginProcessor loginProcessor = new LoginProcessor(email, password, LoginProcessor.Type.REGISTER);
                        Thread thr = new Thread(loginProcessor);
                        thr.start();
                        String serverResponse = null;
                        boolean isError = true;
                        try {
                            thr.join();
                            isError = loginProcessor.isError();
                            serverResponse = loginProcessor.getServerMessage();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (!isError) {
                            Log.d("Successful register ", email);
                            Context context = getApplicationContext();
                            Toast toast = Toast.makeText(context, "Please, verify your email.", Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            Log.d("Cannot register: ", email);
                            Context context = getApplicationContext();
                            Toast toast = Toast.makeText(context, serverResponse, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }
            });

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = ((EditText) findViewById(R.id.email)).getText().toString();
                    String password = ((EditText) findViewById(R.id.password)).getText().toString();

                    if (email.equals("") || password.equals("")) {
                        Context context = getApplicationContext();
                        Toast toast = Toast.makeText(context, "Please enter and password", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {

                        LoginProcessor loginProcessor = new LoginProcessor(email, password, LoginProcessor.Type.LOGIN);
                        Thread thr = new Thread(loginProcessor);
                        thr.start();
                        String serverResponse = null;
                        boolean isError = true;
                        try {
                            thr.join();
                            isError = loginProcessor.isError();
                            serverResponse = loginProcessor.getServerMessage();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (!isError) {
                            Log.d("Successful login ", email);
                            SharedPreferences sharedPreferences = getSharedPreferences("JoinMe", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("JoinMeToken", serverResponse);
                            editor.putString("JoinMeUserEmail", email);
                            editor.apply();
                            Intent i = new Intent(getApplicationContext(), Category.class);
                            startActivity(i);
                            finish();
                        } else {
                            Log.d("Cannot login: ", email);
                            Context context = getApplicationContext();
                            Toast toast = Toast.makeText(context, serverResponse, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }
            });
        } else {
            Toast.makeText(SignInActivity.this,"No Internet Connection",Toast.LENGTH_LONG).show();
        }
    }
}