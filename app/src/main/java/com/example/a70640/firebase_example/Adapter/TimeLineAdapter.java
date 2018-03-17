package com.example.a70640.firebase_example.Adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.a70640.firebase_example.Model.Activity;
import com.example.a70640.firebase_example.Model.OrderStatus;
import com.example.a70640.firebase_example.Model.Orientation;
import com.example.a70640.firebase_example.Model.Time;
import com.example.a70640.firebase_example.Model.TimeLineModel;
import com.example.a70640.firebase_example.R;
import com.example.a70640.firebase_example.utils.DateTimeUtils;
import com.example.a70640.firebase_example.utils.VectorDrawableUtils;
import com.github.vipulasri.timelineview.TimelineView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by min on 2018/3/11.
 */

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineAdapter.MyViewHolder> {

    private List<TimeLineModel> mFeedList;
    private List<Activity> eventList;
    private Context mContext;
    private Orientation mOrientation;
    private boolean mWithLinePadding;
    private LayoutInflater mLayoutInflater;

    public class MyViewHolder extends RecyclerView.ViewHolder {

//        @BindView(R.id.text_timeline_date)
//        TextView mDate;
//        @BindView(R.id.text_timeline_title)
//        TextView mMessage;
        @BindView(R.id.time_marker)
        TimelineView mTimelineView;

        @BindView(R.id.club_act_flat_activity_name)
        TextView name;
        @BindView(R.id.club_act_flat_activity_date)
        TextView date;
        @BindView(R.id.club_act_flat_activity_time)
        TextView time;
        @BindView(R.id.club_act_flat_activity_location)
        TextView location;
        @BindView(R.id.club_act_flat_activity_thumbnail)
        ImageView thumbnail;

        public MyViewHolder(View view, int viewType) {
            super(view);

            ButterKnife.bind(this, view);
            mTimelineView.initLine(viewType);
        }
    }

    public TimeLineAdapter(List<Activity> eventList, List<TimeLineModel> feedList, Orientation orientation, boolean withLinePadding) {
        this.eventList = eventList;
        mFeedList = feedList;
        mOrientation = orientation;
        mWithLinePadding = withLinePadding;
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position,getItemCount());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        mLayoutInflater = LayoutInflater.from(mContext);
        View view;

//        if(mOrientation == Orientation.HORIZONTAL) {
//            view = mLayoutInflater.inflate(mWithLinePadding ? R.layout.item_timeline_horizontal_line_padding : R.layout.item_timeline_horizontal, parent, false);
//        } else {
            view = mLayoutInflater.inflate(mWithLinePadding ? R.layout.item_timeline_line_padding : R.layout.item_timeline, parent, false);
//        }

//        view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.club_activity_item_flat, parent, false);

        return new MyViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        TimeLineModel timeLineModel = mFeedList.get(position);

        if(timeLineModel.getStatus() == OrderStatus.INACTIVE) {
            holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_inactive, android.R.color.darker_gray));
        } else if(timeLineModel.getStatus() == OrderStatus.ACTIVE) {
            holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_active, R.color.colorPrimary));
        } else {
            holder.mTimelineView.setMarker(ContextCompat.getDrawable(mContext, R.drawable.ic_marker), ContextCompat.getColor(mContext, R.color.colorPrimary));
        }

//        if(!timeLineModel.getDate().isEmpty()) {
//            holder.mDate.setVisibility(View.VISIBLE);
//            holder.mDate.setText(DateTimeUtils.parseDateTime(timeLineModel.getDate(), "yyyy-MM-dd HH:mm", "hh:mm a, dd-MMM-yyyy"));
//        }
//        else
//            holder.mDate.setVisibility(View.GONE);

//        holder.mMessage.setText(timeLineModel.getMessage());

        Activity activity = eventList.get(position);
        holder.name.setText(activity.getName());
        holder.date.setText(Time.getDate(activity.getDate()));
        holder.time.setText(activity.getTime());
        holder.location.setText(activity.getLocation());

        Glide.with(mContext)
                .load(activity.getThumbnail())
                .into(holder.thumbnail);

    }

    @Override
    public int getItemCount() {
        return (eventList!=null? eventList.size():0);
    }

}