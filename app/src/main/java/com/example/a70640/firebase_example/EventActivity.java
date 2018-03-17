package com.example.a70640.firebase_example;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.a70640.firebase_example.Fragment.TimelineFragment;
import com.example.a70640.firebase_example.Model.Activity;
import com.example.a70640.firebase_example.Model.Collection;
import com.example.a70640.firebase_example.Model.Time;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by min on 2018/2/23.
 */

public class EventActivity extends AppCompatActivity {
    private static final String TAG = EventActivity.class.getSimpleName();
    boolean collection=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Bundle bundle = getIntent().getExtras();

        setSupportActionBar(toolbar);
        setupActionBar();
        initCollapsingToolbar();

        try {
            Glide.with(this).load(bundle.getString("cover")).crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into((ImageView) findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }
        firebaseFetch();
    }

    private void firebaseFetch(){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().
                getReferenceFromUrl(getIntent().getExtras().getString("eventURL"));

        //adding an event listener to fetch values
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Activity activity = snapshot.getValue(Activity.class);
                ((TextView) findViewById(R.id.event_name)).setText(activity.getName());
                ((TextView) findViewById(R.id.event_host)).setText(getIntent().getStringExtra("clubName"));
                ((TextView) findViewById(R.id.event_time)).setText(
                        Time.getDate(activity.getDate())+" "+ activity.getTime());
                ((TextView) findViewById(R.id.event_location)).setText(activity.getLocation());

                ((TextView) findViewById(R.id.event_intro_content)).setText(activity.getDescription());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: hi");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {

        menu.findItem(R.id.action_collection).setIcon(getResources().getDrawable(R.mipmap.ic_favorite_border_white_36dp));
        collection=false;

        DatabaseReference mDatabase = FirebaseUtil.getCurrentUserRef().child(Constants.DATABASE_PATH_COLLECTION);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Collection event = postSnapshot.getValue(Collection.class);
                    if (event.getDatabaseURL().equals(getIntent().getStringExtra("eventURL"))) {
                        menu.findItem(R.id.action_collection).setIcon(getResources().getDrawable(R.mipmap.ic_favorite_white_36dp));
                        collection=true;
                        break;
                    }else {
                        menu.findItem(R.id.action_collection).setIcon(getResources().getDrawable(R.mipmap.ic_favorite_border_white_36dp));
                        collection=false;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final String mColleciton = getIntent().getExtras().getString("eventURL");

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_collection:
                if(collection){
                    //change your view
                    item.setIcon(getResources().getDrawable(R.mipmap.ic_favorite_border_white_36dp));
                    collection=false;

                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_USERS)
                            .child(FirebaseAuth.getInstance().getUid()).child(Constants.DATABASE_PATH_COLLECTION);

                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //iterating through all the values in database
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                if (postSnapshot.getValue(Collection.class).getDatabaseURL().contains(mColleciton))
                                    postSnapshot.getRef().removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else{
                    //change your view
                    item.setIcon(getResources().getDrawable(R.mipmap.ic_favorite_white_36dp));
                    collection=true;
                    Snackbar.make(this.findViewById(R.id.action_collection), "加入我的收藏", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();

                    final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_USERS)
                            .child(FirebaseAuth.getInstance().getUid()).child(Constants.DATABASE_PATH_COLLECTION);

                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            List<String> strings = new ArrayList<>();

                            if (dataSnapshot.getChildrenCount() == 0)
                                mDatabase.child(mDatabase.push().getKey()).setValue(new Collection(mColleciton));
                            else {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                                    strings.add(postSnapshot.getValue(Collection.class).getDatabaseURL());
                            }

                            if (!(strings.size() ==0) && !strings.contains(collection))
                                mDatabase.child(mDatabase.push().getKey()).setValue(new Collection(mColleciton));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                return true;
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

        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            Bundle bundle = getIntent().getExtras();
            String name = bundle.getString("name");

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(name);
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
