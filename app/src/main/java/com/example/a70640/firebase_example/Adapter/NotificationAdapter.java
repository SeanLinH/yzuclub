package com.example.a70640.firebase_example.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.a70640.firebase_example.CircleTransform;
import com.example.a70640.firebase_example.Model.Activity;
import com.example.a70640.firebase_example.Model.Notification;
import com.example.a70640.firebase_example.R;

import java.util.List;

/**
 * Created by min on 2018/3/5.
 */

public class NotificationAdapter  extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder>{
    private final String TAG = getClass().getSimpleName();

    private Context mContext;
    private List<Notification> notificationList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView clubName,content,timestamp;
        private ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            clubName = (TextView) view.findViewById(R.id.from);
            content = (TextView) view.findViewById(R.id.content);
            timestamp = (TextView) view.findViewById(R.id.timestamp);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        }
    }

    public NotificationAdapter(Context mContext, List<Notification> notificationList) {
        this.mContext = mContext;
        this.notificationList = notificationList;
    }

    @Override
    public NotificationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_list_row, parent, false);

        return new NotificationAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final NotificationAdapter.MyViewHolder holder, final int position) {
        final Notification notification = notificationList.get(position);

        holder.clubName.setText(notification.getClub());
        holder.content.setText(notification.getContent());
        holder.timestamp.setText(notification.getTimestamp());

        Glide.with(mContext).load(notification.getThumbnail()).crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(mContext))
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

}