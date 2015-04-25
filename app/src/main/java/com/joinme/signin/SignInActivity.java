package com.joinme.signin;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.joinme.R;
import com.joinme.processors.LoginProcessor;
import com.joinme.SuccessLogin;

/**
 * Created by Johnny D on 11.04.2015.
 */
public class SignInActivity extends AccountAuthenticatorActivity {

    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";

    public final static String PARAM_USER_PASS = "USER_PASS";

    private AccountManager mAccountManager;
    private String mAccountName;
    private String mAccountType;
    private String mAuthType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAccountManager = AccountManager.get(getBaseContext());
        mAccountName = getIntent().getStringExtra(ARG_ACCOUNT_NAME);
        mAccountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);
        mAuthType = getIntent().getStringExtra(ARG_AUTH_TYPE);
        //set view login.xml
        setContentView(R.layout.login);
    }

    public void onRegisterClick(View v) {
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
            Intent i = new Intent(getApplicationContext(), SuccessLogin.class);
            startActivity(i);
        } else {
            Log.d("Cannot login: ", email);
        }
    }


    public void onLoginClick(View v) {
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
            Intent i = new Intent(getApplicationContext(), SuccessLogin.class);
            startActivity(i);
        } else {
            Log.d("Cannot login: ", email);
        }
    }
}


