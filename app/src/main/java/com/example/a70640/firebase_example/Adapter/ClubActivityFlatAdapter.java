package com.example.a70640.firebase_example.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.a70640.firebase_example.Model.Activity;
import com.example.a70640.firebase_example.R;
import com.example.a70640.firebase_example.Model.Time;

import java.util.List;

/**
 * Created by min on 2018/2/19.
 */

public class ClubActivityFlatAdapter extends RecyclerView.Adapter<ClubActivityFlatAdapter.EmptyViewHolder>{
    private final String TAG = getClass().getSimpleName();
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_EVENT = 1;
    private Context mContext;
    private List<Activity> activityList;

    public class MyViewHolder extends EmptyViewHolder {
        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.club_act_flat_activity_name);
            date = (TextView) view.findViewById(R.id.club_act_flat_activity_date);
            time = (TextView) view.findViewById(R.id.club_act_flat_activity_time);
            location = (TextView) view.findViewById(R.id.club_act_flat_activity_location);
            thumbnail = (ImageView) view.findViewById(R.id.club_act_flat_activity_thumbnail);
        }
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder {
        private TextView empty;
        public TextView name,date,time,location;
        public ImageView thumbnail;

        public EmptyViewHolder(View view) {
            super(view);
            empty = (TextView) view.findViewById(R.id.coming_empty_view);
        }
    }

    public ClubActivityFlatAdapter(Context mContext, List<Activity> activityList) {
        this.mContext = mContext;
        this.activityList = activityList;
    }

    @Override
    public ClubActivityFlatAdapter.EmptyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_EVENT) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.club_activity_item_flat, parent, false);
            return new ClubActivityFlatAdapter.MyViewHolder(itemView);
        } else{
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.empty_event, parent, false);
            return new ClubActivityFlatAdapter.MyViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(final ClubActivityFlatAdapter.EmptyViewHolder holder, int position) {
        if (getItemViewType(0) == VIEW_TYPE_EVENT) {
            Activity activity = activityList.get(position);
            holder.name.setText(activity.getName());
            holder.date.setText(Time.getDate(activity.getDate()));
            holder.time.setText(activity.getTime());
            holder.location.setText(activity.getLocation());

            Glide.with(mContext)
                    .load(activity.getThumbnail())
                    .into(holder.thumbnail);
        }  else if (getItemViewType(0) == VIEW_TYPE_EMPTY) {

        }

    }

    @Override
    public int getItemCount() {
        if(activityList.size() == 0)
            return 1;
        else
            return activityList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (activityList.size() == 0)
            return VIEW_TYPE_EMPTY;
        else
            return VIEW_TYPE_EVENT;
    }
}
