package com.joinme.categories;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.joinme.R;
import com.joinme.events.Events;
import com.joinme.notifier.NotifierProcessor;
import com.joinme.signin.SignInActivity;

/**
 * Created by Johnny D on 11.04.2015.
 */
public class Category extends Activity {

    private String[] mCategoryTitles;
    private int[] mCategoryBackgrounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category);
        mCategoryTitles = getResources().getStringArray(R.array.category_array);
        mCategoryBackgrounds = getResources().getIntArray(R.array.category_color);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.categories);


        LayoutInflater inflater = LayoutInflater.from(this);
        Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
        for (int i = 0; i < mCategoryTitles.length; i++) {
            CardView view = (CardView) inflater.inflate(R.layout.category_list_item,
                    linearLayout, false);
            TextView text = (TextView) view.findViewById(R.id.category_title);
            text.setClickable(true);
            text.setText(mCategoryTitles[i]);
            text.setId(i);
            text.setTypeface(font);
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i;
                    switch (v.getId()) {
                        case 0:
                            i = new Intent(getApplicationContext(), Events.class);
                            i.putExtra("category", "еда");
                            startActivity(i);
                            break;
                        case 1:
                            i = new Intent(getApplicationContext(), Events.class);
                            i.putExtra("category", "спорт");
                            startActivity(i);
                            break;
                        case 2:
                            i = new Intent(getApplicationContext(), Events.class);
                            i.putExtra("category", "учёба");
                            startActivity(i);
                            break;
                        case 3:
                            i = new Intent(getApplicationContext(), Events.class);
                            i.putExtra("category", "фильмы");
                            startActivity(i);
                            break;

                        case 4:
                            i = new Intent(getApplicationContext(), Events.class);
                            i.putExtra("category", "театр");
                            startActivity(i);
                            break;
                    }
                }
            });
            view.setCardBackgroundColor(mCategoryBackgrounds[i]);
            linearLayout.addView(view);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        String[] events;
        String subscriptionAmount;
        String organizerEvent;

        SharedPreferences sharedPreferences = getSharedPreferences("JoinMe", Activity.MODE_PRIVATE);
        String token = sharedPreferences.getString("JoinMeToken", "");
        Log.d("Token in category", token);
        NotifierProcessor notifierProcessor = new NotifierProcessor(token);
        Thread thread = new Thread(notifierProcessor);
        thread.start();

        try {
            thread.join();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        events = notifierProcessor.getEvents();
        subscriptionAmount = notifierProcessor.getSubscriptionAmount();
        organizerEvent = notifierProcessor.getOrganizerEvent();

        String forToast = buildStringForToast(events, subscriptionAmount, organizerEvent);
        Context context = getApplicationContext();
        Log.d("For toast", forToast);
        Toast toast = Toast.makeText(context, forToast, Toast.LENGTH_LONG);
        toast.show();
    }

    private String buildStringForToast(String[] events, String subscriptionAmount, String organizerEvent) {

        if(events.length != 0) {

            StringBuffer allEvents = new StringBuffer();
            for (int i = 0; i < events.length; i++) {
                if (i != events.length - 1) {
                    allEvents.append(events[i] + ", ");
                } else {
                    allEvents.append(events[i]);
                }
            }

            return "Подписок: " + subscriptionAmount + "\nСозданных: " + organizerEvent;
        } else {
            return "Подписок: " + subscriptionAmount + "\nСозданных: " + organizerEvent;
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void signOut(MenuItem item) {
        Log.d("Logout", "CATEGORY");

        SharedPreferences sharedPreferences = getSharedPreferences("JoinMe", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("JoinMeToken", "");
        editor.apply();

        Intent intent = new Intent(Category.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }
}
