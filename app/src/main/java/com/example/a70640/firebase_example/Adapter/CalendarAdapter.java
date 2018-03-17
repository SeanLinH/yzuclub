package com.example.a70640.firebase_example.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.a70640.firebase_example.ClubPage;
import com.example.a70640.firebase_example.EventActivity;
import com.example.a70640.firebase_example.Model.Activity;
import com.example.a70640.firebase_example.R;
import com.example.a70640.firebase_example.Model.Time;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by min on 2018/2/26.
 */

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.MyViewHolder> implements Filterable {
    private String TAG = getClass().getSimpleName();
    private Context mContext;
    private List<Activity> activityList;
    private List<Activity> activityListFiltered;
    private CalendarAdapter.CalendarAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView clubName,actName,date,time,location;
        private ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            clubName = (TextView) view.findViewById(R.id.collection_club_name);
            actName = (TextView) view.findViewById(R.id.collection_name);
            date = (TextView) view.findViewById(R.id.collection_date);
            time = (TextView) view.findViewById(R.id.collection_time);
            location = (TextView) view.findViewById(R.id.collection_location);
            thumbnail = (ImageView) view.findViewById(R.id.collection_thumbnail);

        }
    }


    public CalendarAdapter(Context mContext, List<Activity> activityList, CalendarAdapter.CalendarAdapterListener listener) {
        this.mContext = mContext;
        this.activityList = activityList;
        this.listener = listener;
        this.activityListFiltered = activityList;
    }

    @Override
    public CalendarAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.collection_item, parent, false);

        return new CalendarAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CalendarAdapter.MyViewHolder holder, final int position) {
        final Activity activity = activityListFiltered.get(position);
        holder.clubName.setText(activity.getClub().getName());
        holder.actName.setText(activity.getName());
        holder.date.setText(Time.getDate(activity.getDate()));
        holder.time.setText(activity.getTime());
        holder.location.setText(activity.getLocation());

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
        return activityListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                Log.d(TAG, "performFiltering: " + activityList.size());
                if (charString.isEmpty()) {
                    activityListFiltered.clear();
                } else {
                    List<Activity> filteredList = new ArrayList<>();
                    for (Activity row : activityList) {

//                         name match condition. this might differ depending on your requirement
//                         here we are looking for date match
                        if (Time.getDate(row.getDate().toLowerCase()).contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    activityListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = activityListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                Log.d(TAG, "performFiltering: hi");
                activityListFiltered = (ArrayList<Activity>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface CalendarAdapterListener {
    }
}