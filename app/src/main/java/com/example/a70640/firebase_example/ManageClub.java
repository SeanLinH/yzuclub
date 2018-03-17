package com.example.a70640.firebase_example;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.a70640.firebase_example.Adapter.ClubActivityFlatAdapter;
import com.example.a70640.firebase_example.Model.Activity;
import com.example.a70640.firebase_example.Model.Club;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by min on 2018/2/26.
 */

public class ManageClub extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    private ClubActivityFlatAdapter clubActivityFlatAdapter;
    private List<Activity> comingEventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.manage_club);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        setupActionBar();
        initCollapsingToolbar();

        RecyclerView recyclerView1 = (RecyclerView) findViewById(R.id.coming_event_rv);
        comingEventList = new ArrayList<>();
        clubActivityFlatAdapter = new ClubActivityFlatAdapter(this, comingEventList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView1.setLayoutManager(mLayoutManager);
        recyclerView1.addItemDecoration(new ClubPage.GridSpacingItemDecoration(1, dpToPx(0), true));
        recyclerView1.setItemAnimator(new DefaultItemAnimator());
        recyclerView1.setAdapter(clubActivityFlatAdapter);
        recyclerView1.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView1 ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent intent = new Intent(getApplicationContext(),EventActivity.class);
                        //new一個Bundle物件，並將要傳遞的資料傳入
                        Bundle bundle = new Bundle();
                        bundle.putString("name", comingEventList.get(position).getName());
                        bundle.putString("slogan","slogan");
                        bundle.putString("cover", comingEventList.get(position).getThumbnail());
                        bundle.putString("eventURL", comingEventList.get(position).getDatabaseURL());

                        intent.putExtras(bundle);
                        getApplicationContext().startActivity(intent);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        Intent intent = new Intent(getApplicationContext(),EditEvent.class);
                        //new一個Bundle物件，並將要傳遞的資料傳入
                        Bundle bundle = new Bundle();
                        bundle.putString("name", comingEventList.get(position).getName());
                        bundle.putString("slogan","slogan");
                        bundle.putString("cover", comingEventList.get(position).getThumbnail());
                        bundle.putString("eventURL", comingEventList.get(position).getDatabaseURL());

                        intent.putExtras(bundle);
                        getApplicationContext().startActivity(intent);
                    }
                })
        );

        firebaseFetch();
    }

    private void firebaseFetch(){
        DatabaseReference mDatabase = FirebaseUtil.getBaseRef().getDatabase().getReferenceFromUrl(
                FirebaseUtil.getCurrentUserRef().child("clubAdmin").toString());
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseDatabase.getInstance().getReferenceFromUrl(dataSnapshot.getValue().toString())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final Club club = dataSnapshot.getValue(Club.class);
                                TextView intro = findViewById(R.id.intro_content);
                                intro.setText(club.getDescription());
                                TextView name = findViewById(R.id.select_club_name);
                                name.setText(club.getName());
                                TextView slogan = findViewById(R.id.select_club_slogan);
                                slogan.setText("slogan");
                                try {
                                    Glide.with(getApplicationContext()).load(club.getThumbnail()).crossFade()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL).into((ImageView) findViewById(R.id.backdrop));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                dataSnapshot.child(Constants.DATABASE_PATH_ACTIVITY).getRef()
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                comingEventList.clear();

                                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                                                    Activity activity = postSnapshot.getValue(Activity.class);
                                                    activity.setClub(club);
                                                    activity.setDatabaseURL(postSnapshot.getRef().toString());
                                                    comingEventList.add(activity);

                                                    // refreshing recycler view
                                                    clubActivityFlatAdapter.notifyDataSetChanged();
                                                }
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

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manage_club_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        } else if (id == R.id.create_club_activity){
            Intent intent = new Intent(ManageClub.this,CreateActivity.class);
            startActivity(intent);
        } else if (id == R.id.edit_club_page){
            Intent intent = new Intent(ManageClub.this,EditClubPage.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {

        final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle("管理社團");
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

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