package com.joinme.events;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonFlat;
import com.joinme.R;
import com.squareup.picasso.Picasso;

import android.net.Uri;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<EventInfo> eventList;
    private  EventViewHolder eventViewHolder;
    private Context context;
    private String token;

    public EventAdapter(List<EventInfo> eventList, Context context, String token) {
        this.eventList = eventList;
        this.context = context;
        this.token = token;
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
        eventViewHolder.vActionTwo.setText(eventInfo.action_two);
        eventViewHolder.vActionTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String action = eventInfo.action_two;
                EventProcessor ep = new EventProcessor(token, action, eventInfo.eventId);
                new Thread(ep).start();
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
        }
    }
}