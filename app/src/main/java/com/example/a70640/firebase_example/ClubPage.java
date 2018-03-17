package com.example.a70640.firebase_example;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.a70640.firebase_example.Adapter.ClubActivityFlatAdapter;
import com.example.a70640.firebase_example.Fragment.TimelineFragment;
import com.example.a70640.firebase_example.Model.Activity;
import com.example.a70640.firebase_example.Model.Club;
import com.example.a70640.firebase_example.Model.Favorite;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 70640 on 2018/2/12.
 */

public class ClubPage extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    private Club club;
    private ClubActivityFlatAdapter comingEventAdapter,pastEventAdapter;
    private RecyclerView recyclerView1,recyclerView2;
    private List<Activity> comingEventList,pastEventList;
//    private ImageView iconImp;
//    private boolean collectionCheck = true;
    boolean collection=false;
    private static String favorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar();
        initCollapsingToolbar();

        Bundle bundle = getIntent().getExtras();
        TextView name = findViewById(R.id.select_club_name);
        name.setText(bundle.getString("name"));
        TextView slogan = findViewById(R.id.select_club_slogan);
        slogan.setText(bundle.getString("slogan"));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(fabListener);

        if (MainActivity.collectionList.size()==0){
            fab.setImageResource(R.mipmap.ic_favorite_border_white_36dp);
            collection=false;
        }

        for (Club club: MainActivity.collectionList){
            if (club.getDatabaseURL().equals(getIntent().getStringExtra("URL"))) {
                fab.setImageResource(R.mipmap.ic_favorite_white_36dp);
                collection=true;
                break;
            }else {
                fab.setImageResource(R.mipmap.ic_favorite_border_white_36dp);
                collection=false;
            }
        }

//        iconImp = (ImageView) findViewById(R.id.icon_favorite);
//        iconImp.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.icon_tint_selected));
//        iconImp.setImageResource(R.mipmap.ic_favorite_black_24dp);
//        iconImp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
                // Star favorite is clicked,
                // mark the club as collection
//                Message message = messages.get(position);
//                message.setImportant(!message.isImportant());
//                messages.set(position, message);
//                mAdapter.notifyDataSetChanged();
//                if (collectionCheck){
//                    iconImp.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.icon_tint_normal));
//                    iconImp.setImageResource(R.mipmap.ic_favorite_border_black_24dp);
//                    Toast.makeText(getApplicationContext(),"取消最愛",Toast.LENGTH_SHORT).show();
//                } else {
//                    iconImp.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.icon_tint_selected));
//                    iconImp.setImageResource(R.mipmap.ic_favorite_black_24dp);
//                    Toast.makeText(getApplicationContext(),"加入最愛",Toast.LENGTH_SHORT).show();
//                }
//                collectionCheck=!collectionCheck;
//            }
//        });

        recyclerView1 = (RecyclerView) findViewById(R.id.coming_event_rv);
        comingEventList = new ArrayList<>();
        comingEventAdapter = new ClubActivityFlatAdapter(this, comingEventList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView1.setLayoutManager(mLayoutManager);
        recyclerView1.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(0), true));
        recyclerView1.setItemAnimator(new DefaultItemAnimator());
        recyclerView1.setAdapter(comingEventAdapter);
        recyclerView1.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView1 ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent intent = new Intent(getApplicationContext(),EventActivity.class);
                        //new一個Bundle物件，並將要傳遞的資料傳入
                        Bundle bundle = new Bundle();
                        bundle.putString("name", comingEventList.get(position).getName());
                        bundle.putString("cover", comingEventList.get(position).getThumbnail());
                        bundle.putString("eventURL", comingEventList.get(position).getDatabaseURL());
                        bundle.putString("clubName", comingEventList.get(position).getClub().getName());
                        bundle.putString("clubURL", comingEventList.get(position).getClub().getDatabaseURL());

                        intent.putExtras(bundle);
                        getApplicationContext().startActivity(intent);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                    }
                })
        );

        recyclerView2 = (RecyclerView) findViewById(R.id.past_event_rv);
        pastEventList = new ArrayList<>();
        pastEventAdapter = new ClubActivityFlatAdapter(this, pastEventList);

        RecyclerView.LayoutManager mLayoutManager2 = new GridLayoutManager(this, 1);
        recyclerView2.setLayoutManager(mLayoutManager2);
        recyclerView2.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(0), true));
        recyclerView2.setItemAnimator(new DefaultItemAnimator());
        recyclerView2.setAdapter(pastEventAdapter);
        recyclerView2.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView2 ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent intent = new Intent(getApplicationContext(),EventActivity.class);
                        //new一個Bundle物件，並將要傳遞的資料傳入
                        Bundle bundle = new Bundle();
                        bundle.putString("name", pastEventList.get(position).getName());
                        bundle.putString("cover", pastEventList.get(position).getThumbnail());
                        bundle.putString("URL", pastEventList.get(position).getClub().getDatabaseURL());
                        intent.putExtras(bundle);


                        getApplicationContext().startActivity(intent);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                    }
                })
        );

        try {
            Glide.with(this).load(bundle.getString("cover")).crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into((ImageView) findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }

        firebaseFetch();
    }

    View.OnClickListener fabListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (collection) {
                ((FloatingActionButton) findViewById(R.id.fab)).setImageResource(R.mipmap.ic_favorite_border_white_36dp);
                collection = false;

                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_USERS)
                        .child(FirebaseAuth.getInstance().getUid()).child(Constants.DATABASE_PATH_FAVORITE);

                favorite = getIntent().getExtras().getString("URL");
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //iterating through all the values in database
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            if (postSnapshot.getValue(Favorite.class).getDatabaseURL().contains(favorite))
                                postSnapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                ((FloatingActionButton) findViewById(R.id.fab)).setImageResource(R.mipmap.ic_favorite_white_36dp);
                Snackbar.make(view, "加入我的最愛", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                collection = true;

                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_USERS)
                        .child(FirebaseAuth.getInstance().getUid()).child(Constants.DATABASE_PATH_FAVORITE);

                favorite = getIntent().getExtras().getString("URL");
                Favorite favoriteClub = new Favorite(favorite);
                mDatabase.child(mDatabase.push().getKey()).setValue(favoriteClub);
                checkFavorite();
            }
        }
    };

    private void firebaseFetch(){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().
                getReferenceFromUrl(getIntent().getExtras().getString("URL"));

        //adding an event listener to fetch values
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot snapshot) {

                //iterating through all the values in database
                club = snapshot.getValue(Club.class);
                TextView intro = findViewById(R.id.intro_content);
                intro.setText(club.getDescription());
                YouTubePlayerSupportFragment frag =
                        (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.youtube_fragment);
                frag.initialize("AIzaSyDEplrQ_4yqcDTWzYsaT55H4cEkz_09Jkk", new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                        if (!b) {
                            //I assume the below String value is your video id
                            if (club.getVideo()!=null)
                                youTubePlayer.cueVideo(club.getVideo());
                        }
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult error) {
                        if (error.isUserRecoverableError()) {
//                            error.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
                            Log.d(TAG, "onInitializationFailure: if" );
                        } else {
//                            String errorMessage = String.format(getString(R.string.error_player), errorReason.toString());
//                            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                            Log.d(TAG, "onInitializationFailure: else" );
                        }
                    }
                });

                snapshot.child(Constants.DATABASE_PATH_ACTIVITY).getRef()
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                comingEventList.clear();

                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                                    Activity activity = postSnapshot.getValue(Activity.class);
                                    activity.setDatabaseURL(postSnapshot.getRef().toString());
                                    activity.setClub(club);
                                    comingEventList.add(activity);

                                    // refreshing recycler view
                                    comingEventAdapter.notifyDataSetChanged();
                                }

//                                if (comingEventList.isEmpty()) {
//                                    recyclerView1.setVisibility(View.GONE);
//                                    comingEmpty.setVisibility(View.VISIBLE);
//                                }
//                                else {
//                                    recyclerView1.setVisibility(View.VISIBLE);
//                                    comingEmpty.setVisibility(View.GONE);
//                                }
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

    private void checkFavorite() {

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_USERS)
                .child(FirebaseAuth.getInstance().getUid()).child(Constants.DATABASE_PATH_FAVORITE);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                //iterating through all the values in database
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (postSnapshot.getValue(Favorite.class).getDatabaseURL().contains(favorite))
                        i++;
                    if (i>=2)
                        postSnapshot.getRef().removeValue();
                }
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.club_activity_menu, menu);
//        return true;
//    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        for (Club club: MainActivity.collectionList){
//            if (club.getDatabaseURL().equals(getIntent().getStringExtra("URL"))) {
//                menu.findItem(R.id.action_collection).setIcon(getResources().getDrawable(R.mipmap.ic_favorite_white_36dp));
//                collection=true;
//                break;
//            }else {
//                menu.findItem(R.id.action_collection).setIcon(getResources().getDrawable(R.mipmap.ic_favorite_border_white_36dp));
//                collection=false;
//            }
//        }
//        return super.onPrepareOptionsMenu(menu);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
//            case R.id.action_collection:
//                if(collection){
//                    //change your view
//                    item.setIcon(getResources().getDrawable(R.mipmap.ic_favorite_border_white_36dp));
//                    collection=false;
//                }else{
//                    //change your view
//                    item.setIcon(getResources().getDrawable(R.mipmap.ic_favorite_white_36dp));
//                    collection=true;
//                    Toast.makeText(getApplicationContext(),"加入最愛",Toast.LENGTH_LONG).show();
//                }
//
//                return true;
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

//    @Override
//    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
//        if (!b) {
//            //I assume the below String value is your video id
//            if (club.getVideo()!=null)
//                youTubePlayer.cueVideo(club.getVideo());
//        }
//    }
//
//    @Override
//    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult error) {
//            if (error.isUserRecoverableError()) {
////                error.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
//                Log.d(TAG, "onInitializationFailure: if" );
//            } else {
////                String errorMessage = String.format(getString(R.string.error_player), errorReason.toString());
////                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
//                Log.d(TAG, "onInitializationFailure: else" );
//            }
//    }

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
