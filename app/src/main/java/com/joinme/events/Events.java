package com.joinme.events;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.joinme.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johnny D on 16.04.2015.
 */
public class Events extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_list);

        RecyclerView eventList = (RecyclerView) findViewById(R.id.eventList);
        eventList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        eventList.setLayoutManager(llm);

        EventAdapter eventAdapter = new EventAdapter(createList());
        eventList.setAdapter(eventAdapter);
    }

    private List<EventInfo> createList() {
        List<EventInfo> result = new ArrayList<EventInfo>();
        EventInfo eventInfo = new EventInfo();
        eventInfo.action_one = "MORE";
        eventInfo.action_two = "JOIN";
        eventInfo.title = "Skate walking";
        eventInfo.creator = "Varnavsky Vadim";
        eventInfo.descriptionTitle = "Tony Hawk";
        eventInfo.description = "I find some friend!";
        eventInfo.membersTitle = "Members";
        List<String> members = new ArrayList();
        members.add("warlen94@mail.ru");
        members.add("sigorilla@mail.ru");
        StringBuffer stringBuffer = new StringBuffer();
        for (String temp : members) {
            stringBuffer.append(temp + "\n");
        }
        eventInfo.members = stringBuffer.toString();
        result.add(eventInfo);

        return result;
    }
}
