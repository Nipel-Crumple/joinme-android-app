package com.joinme.events;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.internal.app.ToolbarActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.joinme.R;
import com.joinme.signin.SignInActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johnny D on 16.04.2015.
 */
public class Events extends Activity implements SwipeRefreshLayout.OnRefreshListener {

    SwipeRefreshLayout mSwipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        onRefresh();

    }


    private List<EventInfo> createList(JSONObject json, String currentUserEmail) {

        List<EventInfo> result = new ArrayList<EventInfo>();
        try {
            JSONArray array = json.getJSONArray("events");
            for (int i = 0; i < array.length(); i++) {
                List<String> members = new ArrayList();
                JSONObject object = array.getJSONObject(i);
                Log.d("collecting data", "" + "\n");
                EventInfo eventInfo = new EventInfo();
                eventInfo.title = object.getString("title");
                Log.d("Title",eventInfo.title);

                eventInfo.creator = object.getJSONObject("author").getString("username");
                Log.d("Author",eventInfo.creator);

                eventInfo.imageUrl = object.getJSONObject("author").getString("photo");
                Log.d("Photo:", eventInfo.imageUrl);

                eventInfo.descriptionTitle = "Описание";
                Log.d("Description title", eventInfo.descriptionTitle);

                eventInfo.description = object.getString("description");
                Log.d("Description", eventInfo.description);

                eventInfo.eventId = object.getString("id");
                Log.d("EventId", eventInfo.eventId);

                JSONArray memberArrayJson = object.getJSONArray("members");

                Log.d("NUMBER OF MEMBERS: ", String.valueOf(memberArrayJson.length() + 1));
                for (int j = 0; j < memberArrayJson.length(); j++) {
                    members.add(memberArrayJson.getJSONObject(j).getString("username"));
                    Log.d("member #" + j, members.get(j));
                }

                StringBuffer stringBuffer = new StringBuffer();
                for (String temp : members) {
                    stringBuffer.append(temp + "\n");
                }

                Log.d("String of members:", stringBuffer.toString());

                eventInfo.membersTitle = "Участники";
                eventInfo.members = stringBuffer.toString();
                eventInfo.action_one = "ПОДРОБНЕЕ";
                eventInfo.action_two = "ПРИСОЕДИНИТЬСЯ";

                if (members.contains(currentUserEmail)) {
                    eventInfo.action_two = "ПОКИНУТЬ";
                } else if (eventInfo.creator.equals(currentUserEmail)){
                    eventInfo.action_two = "УДАЛИТЬ";
                } else {
                    eventInfo.action_two = "ПРИСОЕДИНИТЬСЯ";
                }

                result.add(eventInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public void onRefresh() {
        // говорим о том, что собираемся начать
        Toast.makeText(this, R.string.refresh_started, Toast.LENGTH_SHORT).show();
        SharedPreferences sharedPreferences = getSharedPreferences("JoinMe", Activity.MODE_PRIVATE);
        final String userEmail = sharedPreferences.getString("JoinMeUserEmail", "");
        final String token = sharedPreferences.getString("JoinMeToken", "");
        Bundle bundle = getIntent().getExtras();
        EventProcessor proc = new EventProcessor(bundle, token);
        Thread thr = new Thread(proc);
        thr.start();

        RecyclerView eventList = (RecyclerView) findViewById(R.id.eventList);
        eventList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        eventList.setLayoutManager(llm);

        try {
            thr.join();
            Log.d("Server ", proc.getJsonResponse().toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JSONObject jsonResponse = proc.getJsonResponse();
        List<EventInfo> list = createList(jsonResponse, userEmail);

        final EventAdapter eventAdapter = new EventAdapter(list, getApplicationContext(), token, this);
        eventList.setAdapter(eventAdapter);
        // начинаем показывать прогресс
        mSwipeRefreshLayout.setRefreshing(true);
        // ждем 3 секунды и прячем прогресс
        mSwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
                // говорим о том, что собираемся закончить
//                Toast.makeText(Events.this, R.string.refresh_finished, Toast.LENGTH_SHORT).show();
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void signOut(MenuItem item) {
        Log.d("Logout", "EVENT");

        SharedPreferences sharedPreferences = getSharedPreferences("JoinMe", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("JoinMeToken", "");
        editor.apply();

        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(intent);
        finish();
    }
}
