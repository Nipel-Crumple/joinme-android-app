package com.joinme.events;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.joinme.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johnny D on 16.04.2015.
 */
public class Events extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_list);

        SharedPreferences sharedPreferences = getSharedPreferences("JoinMe", Activity.MODE_PRIVATE);
        final String token = sharedPreferences.getString("JoinMeToken", "");
        final String userEmail = sharedPreferences.getString("JoinMeUserEmail", "");

        Log.d("JoinMeUserEmail", userEmail);
        Log.d("JoinMeToken", token);

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

        final EventAdapter eventAdapter = new EventAdapter(list, getApplicationContext(), token);
        eventList.setAdapter(eventAdapter);
    }


    private List<EventInfo> createList(JSONObject json, String currentUserEmail) {

        List<EventInfo> result = new ArrayList<EventInfo>();
        List<String> members = new ArrayList();
        try {
            JSONArray array = json.getJSONArray("events");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                Log.d("collecting data", "" + "\n");
                EventInfo eventInfo = new EventInfo();
                eventInfo.title = object.getString("title");
                Log.d("Title",eventInfo.title);

                eventInfo.creator = object.getJSONObject("author").getString("username");
                Log.d("Author",eventInfo.creator);

                eventInfo.imageUrl = object.getJSONObject("author").getString("photo");
                Log.d("Photo:", eventInfo.imageUrl);

                eventInfo.descriptionTitle = "Description";
                Log.d("Description title", eventInfo.descriptionTitle);

                eventInfo.description = object.getString("description");
                Log.d("Description", eventInfo.description);

                eventInfo.eventId = object.getString("id");
                Log.d("EventId", eventInfo.eventId);

                JSONArray memberArrayJson = object.getJSONArray("members");

                for (int j = 0; j < memberArrayJson.length(); j++) {
                    members.add(memberArrayJson.getJSONObject(j).getString("username"));
                    Log.d("member #" + j, members.get(j));
                }

                StringBuffer stringBuffer = new StringBuffer();
                for (String temp : members) {
                    stringBuffer.append(temp + "\n");
                    Log.d("String of members:", stringBuffer.toString());
                }

                eventInfo.membersTitle = "Members";
                eventInfo.members = stringBuffer.toString();
                eventInfo.action_one = "MORE";
                eventInfo.action_two = "JOIN";

                if (members.contains(currentUserEmail)) {
                    eventInfo.action_two = "LEAVE";
                } else if (eventInfo.creator.equals(currentUserEmail)){
                    eventInfo.action_two = "DELETE";
                } else {
                    eventInfo.action_two = "JOIN";
                }

                result.add(eventInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }
}
