package com.example.a70640.firebase_example.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.example.a70640.firebase_example.EventActivity;
import com.example.a70640.firebase_example.ClubPage;
import com.example.a70640.firebase_example.Model.Activity;
import com.example.a70640.firebase_example.R;
import com.example.a70640.firebase_example.Model.Time;

import java.util.List;

/**
 * Created by min on 2018/2/21.
 */

public class ClubActivityAdapter extends RecyclerView.Adapter<ClubActivityAdapter.MyViewHolder>{
    private final String TAG = getClass().getSimpleName();

    private Context mContext;
    private List<Activity> activityList;
    private ClubActivityAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView clubName,actName,date,time,location;
        private ImageView thumbnail;
        private ToggleButton collection;

        public MyViewHolder(View view) {
            super(view);
            clubName = (TextView) view.findViewById(R.id.club_act_activity_club_name);
            actName = (TextView) view.findViewById(R.id.club_act_activity_name);
            date = (TextView) view.findViewById(R.id.club_act_activity_date);
            time = (TextView) view.findViewById(R.id.club_act_activity_time);
            location = (TextView) view.findViewById(R.id.club_act_activity_location);
            thumbnail = (ImageView) view.findViewById(R.id.club_act_activity_thumbnail);
            collection= (ToggleButton) view.findViewById(R.id.club_act_activity_collection);
        }
    }

    public ClubActivityAdapter(Context mContext, List<Activity> activityList, ClubActivityAdapterListener listener) {
        this.mContext = mContext;
        this.activityList = activityList;
        this.listener = listener;
    }

    @Override
    public ClubActivityAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.club_activity_item, parent, false);

        return new ClubActivityAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ClubActivityAdapter.MyViewHolder holder, final int position) {

        final Activity activity = activityList.get(position);
        holder.clubName.setText(activity.getClub().getName());
        holder.actName.setText(activity.getName());
        holder.date.setText(Time.getDate(activity.getDate()));
        holder.time.setText(activity.getTime());
        holder.location.setText(activity.getLocation());
        holder.collection.setChecked(activity.getChecked());
        holder.collection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked)
                    listener.onAddToCollectionSelected(position);
                else {
                    listener.onDeleteCollectionSelected(position);
                }
            }
        });

        Glide.with(mContext).load(activity.getThumbnail()).into(holder.thumbnail);

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,EventActivity.class);
                //new一個Bundle物件，並將要傳遞的資料傳入
                Bundle bundle = new Bundle();
                bundle.putString("name",activity.getName());
                bundle.putString("cover",activity.getThumbnail());
                bundle.putString("eventURL",activity.getDatabaseURL());

                intent.putExtras(bundle);

                mContext.startActivity(intent);
            }
        });

        holder.clubName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,ClubPage.class);
                //new一個Bundle物件，並將要傳遞的資料傳入
                Bundle bundle = new Bundle();
                bundle.putString("name",activity.getClub().getName());
                bundle.putString("slogan","slogan");
                bundle.putString("cover",activity.getClub().getThumbnail());
                bundle.putString("URL",activity.getClub().getDatabaseURL());

                intent.putExtras(bundle);

                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    public interface ClubActivityAdapterListener {
        void onAddToCollectionSelected(int position);
        void onDeleteCollectionSelected(int position);
    }

}
