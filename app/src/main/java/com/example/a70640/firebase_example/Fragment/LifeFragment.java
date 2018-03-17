package com.example.a70640.firebase_example.Fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.a70640.firebase_example.Adapter.LifeSlideAdapter;
import com.example.a70640.firebase_example.Adapter.TimeLineAdapter;
import com.example.a70640.firebase_example.Constants;
import com.example.a70640.firebase_example.EventActivity;
import com.example.a70640.firebase_example.Model.Activity;
import com.example.a70640.firebase_example.Model.Club;
import com.example.a70640.firebase_example.Model.Collection;
import com.example.a70640.firebase_example.Model.OrderStatus;
import com.example.a70640.firebase_example.Model.Orientation;
import com.example.a70640.firebase_example.Model.TimeLineModel;
import com.example.a70640.firebase_example.R;
import com.example.a70640.firebase_example.RecyclerItemClickListener;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by min on 2018/2/19.
 */

public class LifeFragment extends Fragment {
    private final String TAG = getClass().getSimpleName();

    private RecyclerView mRecyclerView;
    private TimeLineAdapter mTimeLineAdapter;
    private List<TimeLineModel> mDataList = new ArrayList<>();
    private List<Activity> eventList;
    private ValueEventListener valueEventListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_life1, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.nav_life_notebook);

        View v = getView();

        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(getLinearLayoutManager());
        mRecyclerView.setHasFixedSize(true);

        initView();
    }

    private LinearLayoutManager getLinearLayoutManager() {
//        if (mOrientation == Orientation.HORIZONTAL) {
//            return new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
//        } else {
            return new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
//        }
    }

    private void initView() {
        setDataListItems();

        eventList = new ArrayList<>();
        mTimeLineAdapter = new TimeLineAdapter(eventList, mDataList, Orientation.VERTICAL, false);
        mRecyclerView.setAdapter(mTimeLineAdapter);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), mRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent intent = new Intent(getContext(),EventActivity.class);
                        //new一個Bundle物件，並將要傳遞的資料傳入
                        Bundle bundle = new Bundle();
                        bundle.putString("name", eventList.get(position).getName());
                        bundle.putString("cover", eventList.get(position).getThumbnail());
                        bundle.putString("eventURL", eventList.get(position).getDatabaseURL());
                        bundle.putString("clubName", eventList.get(position).getClub().getName());
                        bundle.putString("clubURL", eventList.get(position).getClub().getDatabaseURL());

                        intent.putExtras(bundle);
                        getContext().startActivity(intent);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                    }
                })
        );

        firebaseFetch();
    }

    private void firebaseFetch(){
        DatabaseReference clubDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_CLUBS);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                eventList.clear();

                //iterating through all the values in database
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    final Club club = postSnapshot.getValue(Club.class);
                    club.setDatabaseURL(postSnapshot.getRef().toString());

                    postSnapshot.child(Constants.DATABASE_PATH_ACTIVITY).getRef()
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                        Activity activity = dataSnapshot1.getValue(Activity.class);
                                        activity.setDatabaseURL(dataSnapshot1.getRef().toString());
                                        activity.setClub(club);

                                        eventList.add(activity);

                                        // refreshing recycler view
                                        mTimeLineAdapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        clubDatabase.addListenerForSingleValueEvent(valueEventListener);
    }

    private void setDataListItems(){
        mDataList.add(new TimeLineModel("Item successfully delivered", "", OrderStatus.INACTIVE));
        mDataList.add(new TimeLineModel("Courier is out to delivery your order", "2017-02-12 08:00", OrderStatus.ACTIVE));
        mDataList.add(new TimeLineModel("Item has reached courier facility at New Delhi", "2017-02-11 21:00", OrderStatus.COMPLETED));
    }

}
