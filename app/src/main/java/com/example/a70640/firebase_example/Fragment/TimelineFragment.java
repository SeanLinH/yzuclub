package com.example.a70640.firebase_example.Fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.example.a70640.firebase_example.Adapter.CalendarAdapter;
import com.example.a70640.firebase_example.Adapter.ClubActivityAdapter;
import com.example.a70640.firebase_example.Adapter.CollectionAdapter;
import com.example.a70640.firebase_example.Adapter.SamplePagerAdapter;
import com.example.a70640.firebase_example.Constants;
import com.example.a70640.firebase_example.FirebaseUtil;
import com.example.a70640.firebase_example.Model.Activity;
import com.example.a70640.firebase_example.Model.Club;
import com.example.a70640.firebase_example.Model.Collection;
import com.example.a70640.firebase_example.R;
import com.example.a70640.firebase_example.Model.Time;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by 70640 on 2018/2/7.
 */

public class TimelineFragment extends Fragment implements ClubActivityAdapter.ClubActivityAdapterListener{
    private final String TAG = getClass().getSimpleName();
    private List<Activity> activityList;
    private List<Collection> collectionURLs = new ArrayList<>();
    private List<Activity> collectionList;
    private ClubActivityAdapter clubActivityAdapter;
    private CalendarAdapter calendarAdapter;
    private CollectionAdapter collectionActivityAdapter;
    private ShimmerFrameLayout activity_sfl,calendar_sfl,collection_sfl;
    private CompactCalendarView compactCalendar;
    private DatabaseReference clubDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_CLUBS);
    private ValueEventListener valueEventListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle("最新消息");

        View v = getView();
        TabLayout mTabLayout = v.findViewById(R.id.tabs);
        ViewPager mViewPager = v.findViewById(R.id.pager);
        String[] tabsName = getResources().getStringArray(R.array.TabNames);

        final LayoutInflater mInflater = LayoutInflater.from(getContext());

        //Activity
        LinearLayout v1 = (LinearLayout) mInflater.inflate(R.layout.myactivity, null);
        activity_sfl = v1.findViewById(R.id.myactivity_sfl);
        activity_sfl.startShimmerAnimation();
        RecyclerView activity_rv = (RecyclerView) v1.findViewById(R.id.myactivity_rv);
        activityList = new ArrayList<>();
        clubActivityAdapter = new ClubActivityAdapter(getContext(), activityList, this);

        RecyclerView.LayoutManager activity_lm = new GridLayoutManager(getContext(), 1);
        activity_rv.setLayoutManager(activity_lm);
        activity_rv.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(0), true));
        activity_rv.setItemAnimator(new DefaultItemAnimator());
        activity_rv.setAdapter(clubActivityAdapter);

        //Calendar
        RelativeLayout v2 = (RelativeLayout) mInflater.inflate(R.layout.mycalendar, null);

        compactCalendar = (CompactCalendarView) v2.findViewById(R.id.compactcalendar_view);
        compactCalendar.setLocale(TimeZone.getTimeZone("Asia/Taipei"),Locale.TAIWAN);
        compactCalendar.setUseThreeLetterAbbreviation(true);
        compactCalendar.setFirstDayOfWeek(Calendar.SUNDAY);

        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
//                Toast.makeText(getContext(), Time.getDate(String.valueOf(dateClicked.getTime())), Toast.LENGTH_SHORT).show();
                calendarAdapter.getFilter().filter(Time.getDate(String.valueOf(dateClicked.getTime())));
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
            }
        });

        calendar_sfl = v2.findViewById(R.id.mycalendar_sfl);
        calendar_sfl.startShimmerAnimation();
        RecyclerView calendar_srv = (RecyclerView) v2.findViewById(R.id.mycalendar_rv);
        calendarAdapter = new CalendarAdapter(getContext(), activityList, new CalendarAdapter.CalendarAdapterListener() {
        });

        RecyclerView.LayoutManager calendar_lm = new GridLayoutManager(getContext(), 1);
        calendar_srv.setLayoutManager(calendar_lm);
        calendar_srv.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(0), true));
        calendar_srv.setItemAnimator(new DefaultItemAnimator());
        calendar_srv.setAdapter(calendarAdapter);

        //Collection
        final LinearLayout v3 = (LinearLayout) mInflater.inflate(R.layout.mycollection, null);
        collection_sfl = v3.findViewById(R.id.mycollection_sfl);
        collection_sfl.startShimmerAnimation();
        RecyclerView collection_rv = (RecyclerView) v3.findViewById(R.id.mycollection_rv);
        collectionList = new ArrayList<>();
        collectionActivityAdapter = new CollectionAdapter(getContext(), collectionList, new CollectionAdapter.CollectionAdapterListener() {
            @Override
            public void onAddToCollectionSelected(int position) {

            }

            @Override
            public void onDeleteCollectionSelected(final int position) {
//                final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_USERS)
//                        .child(FirebaseAuth.getInstance().getUid()).child(Constants.DATABASE_PATH_COLLECTION);
//                final String collection = collectionList.get(position).getDatabaseURL();
//
//                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                            if (postSnapshot.getValue(Collection.class).getDatabaseURL().contains(collection)) {
//                                postSnapshot.getRef().removeValue();
//                                break;
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
            }
        });

        RecyclerView.LayoutManager collection_lm = new GridLayoutManager(getContext(), 1);
        collection_rv.setLayoutManager(collection_lm);
        collection_rv.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(0), true));
        collection_rv.setItemAnimator(new DefaultItemAnimator());
        collection_rv.setAdapter(collectionActivityAdapter);


        ScrollView sv1 = (ScrollView) mInflater.inflate(R.layout.scrollview, null);
        sv1.addView(v1);
        ScrollView sv2 = (ScrollView) mInflater.inflate(R.layout.scrollview, null);
        sv2.addView(v2);
        ScrollView sv3 = (ScrollView) mInflater.inflate(R.layout.scrollview, null);
        sv3.addView(v3);

        List<View> viewList = new ArrayList<>();
        viewList.add(sv1);
        viewList.add(sv2);
        viewList.add(sv3);
        mViewPager.setAdapter( new SamplePagerAdapter(tabsName, viewList));
        mViewPager.addOnPageChangeListener(mPCL); // 設定Listener
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorPrimary));

        activityFetch();
        collectionFetch();

    }

    ViewPager.OnPageChangeListener mPCL = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            switch (position){
                case 1:
                    if (calendarAdapter.getItemCount()==activityList.size()){
                        calendarAdapter.getFilter().filter(Time.getDate(String.valueOf(
                                Calendar.getInstance(TimeZone.getTimeZone("Asia/Taipei"), Locale.TAIWAN).getTimeInMillis())));
                    }
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void activityFetch(){
//        clubDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_CLUBS);

        //adding an event listener to fetch values
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                compactCalendar.removeAllEvents();
                activityList.clear();

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

                                        for (Collection collectionURL : collectionURLs) {
                                            if (dataSnapshot1.getRef().toString().contains(collectionURL.getDatabaseURL())) {
                                                activity.setChecked(true);
                                                break;
                                            }
                                        }

                                        activityList.add(activity);
                                        Event event = new Event(Color.RED, Long.parseLong(activity.getDate()));
                                        compactCalendar.addEvent(event, false);
                                        // refreshing recycler view
                                        clubActivityAdapter.notifyDataSetChanged();
                                        calendarAdapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }
                // stop animating Shimmer and hide the layout
                activity_sfl.stopShimmerAnimation();
                activity_sfl.setVisibility(View.GONE);
                calendar_sfl.stopShimmerAnimation();
                calendar_sfl.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
//        clubDatabase.addListenerForSingleValueEvent(valueEventListener);
    }

    private void collectionFetch() {
        final DatabaseReference mDatabase = FirebaseUtil.getCurrentUserRef().child(Constants.DATABASE_PATH_COLLECTION);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                collectionURLs.clear();
                collectionList.clear();
                clubDatabase.removeEventListener(valueEventListener);

                final ArrayList<DatabaseReference> snapshotList = new ArrayList<>();
                //iterating through all the values in database
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Collection collection = postSnapshot.getValue(Collection.class);
                    snapshotList.add(postSnapshot.getRef());
                    collectionURLs.add(collection);
                }

                for (int i=0; i<collectionURLs.size(); i++){
                    //adding an event listener to fetch values
                    final int finalI = i;
                    FirebaseDatabase.getInstance().getReferenceFromUrl(collectionURLs.get(i).getDatabaseURL())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(final DataSnapshot snapshot) {
                                    //iterating through all the values in database
                                    if (snapshot.getValue()==null) {
                                        //當社團移除活動時，使用者的收藏清單也要跟著移除
                                        snapshotList.get(finalI).removeValue();
                                        return;
                                    }
                                    final Activity activity = snapshot.getValue(Activity.class);

                                    FirebaseDatabase.getInstance().getReferenceFromUrl(snapshot.getRef().getParent().getParent().toString())
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    final Club club = dataSnapshot.getValue(Club.class);
                                                    club.setDatabaseURL(snapshot.getRef().getParent().getParent().toString());
                                                    activity.setClub(club);
                                                    activity.setChecked(true);
                                                    activity.setDatabaseURL(collectionURLs.get(finalI).getDatabaseURL());
                                                    collectionList.add(activity);
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                }
                clubDatabase.addListenerForSingleValueEvent(valueEventListener);
                collectionActivityAdapter.notifyDataSetChanged();

                // stop animating Shimmer and hide the layout
                collection_sfl.stopShimmerAnimation();
                collection_sfl.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onAddToCollectionSelected(final int position) {
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_USERS)
                .child(FirebaseAuth.getInstance().getUid()).child(Constants.DATABASE_PATH_COLLECTION);
        final String collection = activityList.get(position).getDatabaseURL();

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<String> strings = new ArrayList<>();

                if (dataSnapshot.getChildrenCount() == 0)
                    mDatabase.child(mDatabase.push().getKey()).setValue(new Collection(collection));
                else {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                        strings.add(postSnapshot.getValue(Collection.class).getDatabaseURL());
                }

                if (!(strings.size() ==0) && !strings.contains(collection))
                    mDatabase.child(mDatabase.push().getKey()).setValue(new Collection(collection));

                collectionActivityAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDeleteCollectionSelected(int position) {
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_USERS)
                .child(FirebaseAuth.getInstance().getUid()).child(Constants.DATABASE_PATH_COLLECTION);
        final String collection = activityList.get(position).getDatabaseURL();

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (postSnapshot.getValue(Collection.class).getDatabaseURL().contains(collection))
                        postSnapshot.getRef().removeValue();
                }

                collectionActivityAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}
