package com.joinme.events;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFlat;
import com.joinme.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<EventInfo> eventList;
    private  EventViewHolder eventViewHolder;
    private Context context;
    private String token;
    private Activity activity;

    public EventAdapter(List<EventInfo> eventList, Context context, String token, Activity activity) {
        this.eventList = eventList;
        this.context = context;
        this.token = token;
        this.activity = activity;
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    @Override
    public void onBindViewHolder(EventViewHolder eventViewHolder, int i) {
        final EventInfo eventInfo = eventList.get(i);
        eventViewHolder.vTitle.setText(eventInfo.title);
        eventViewHolder.vCreator.setText(eventInfo.creator);
        String imageUrl = eventInfo.imageUrl;
        if (imageUrl != null && !imageUrl.equals("")) {
            Picasso.with(context).load(imageUrl).into(eventViewHolder.vCreatorAvatar);
        }
        eventViewHolder.vDescriptionTitle.setText(eventInfo.descriptionTitle);
        eventViewHolder.vDescription.setText(eventInfo.description);
        eventViewHolder.vMembersTitle.setText(eventInfo.membersTitle);
        eventViewHolder.vMembersList.setText(eventInfo.members);
        eventViewHolder.vActionOne.setText(eventInfo.action_one);
        eventViewHolder.vActionOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://joinmipt.com/event/" + eventInfo.eventId + "/"));
                activity.startActivity(browserIntent);
            }
        });
        eventViewHolder.vActionTwo.setText(eventInfo.action_two);
        eventViewHolder.vActionTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String action = eventInfo.action_two;
                EventProcessor ep = new EventProcessor(token, action, eventInfo.eventId);
                new Thread(ep).start();
                try {
                    Log.d("Before sleep", "");
                    Thread.sleep(500);
                    Log.d("After Sleep", "");
                    ((Events) activity).onRefresh();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        eventViewHolder.vActionThree.setText(eventInfo.action_three);
        eventViewHolder.vActionThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double longitude = eventInfo.longitude;
                double lattitude = eventInfo.lattitude;
                String uriGM = String.format(Locale.ENGLISH, "google.navigation:q=%.6f,%.6f&mode=w",
                        lattitude,
                        longitude,
                        eventInfo.title);
                Intent intentGM = new Intent(Intent.ACTION_VIEW, Uri.parse(uriGM));

                intentGM.setPackage("com.google.android.apps.maps");
                if (intentGM.resolveActivity(activity.getPackageManager()) != null) {
                    activity.startActivity(intentGM);
                } else {
                    Toast.makeText(activity, "Warning! " + "There is no GoogleMaps in your Android", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.event_card, viewGroup, false);

        eventViewHolder = new EventViewHolder(itemView);
        return eventViewHolder;
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {
        protected TextView vTitle;
        protected CircleImageView vCreatorAvatar;
        protected TextView vCreator;
        protected TextView vDescriptionTitle;
        protected TextView vDescription;
        protected TextView vMembersTitle;
        protected TextView vMembersList;
        protected ButtonFlat vActionOne;
        protected ButtonFlat vActionTwo;
        protected ButtonFlat vActionThree;

        public EventViewHolder(View v) {
            super(v);
            vTitle =  (TextView) v.findViewById(R.id.title);
            vCreatorAvatar = (CircleImageView) v.findViewById(R.id.avatar);
            vCreator = (TextView) v.findViewById(R.id.creator);
            vDescriptionTitle = (TextView)  v.findViewById(R.id.descriptiontitle);
            vDescription = (TextView) v.findViewById(R.id.description);
            vMembersTitle = (TextView) v.findViewById(R.id.memberstitle);
            vMembersList = (TextView) v.findViewById(R.id.members);
            vActionOne = (ButtonFlat) v.findViewById(R.id.action1);
            vActionTwo = (ButtonFlat) v.findViewById(R.id.action2);
            vActionThree = (ButtonFlat) v.findViewById(R.id.action3);
        }
    }
}