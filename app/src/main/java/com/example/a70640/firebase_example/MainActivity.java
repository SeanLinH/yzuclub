package com.example.a70640.firebase_example;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.a70640.firebase_example.Fragment.AllClubFragment;
import com.example.a70640.firebase_example.Fragment.HistoryFragment;
import com.example.a70640.firebase_example.Fragment.LifeFragment;
import com.example.a70640.firebase_example.Fragment.NotificationsFragment;
import com.example.a70640.firebase_example.Fragment.TimelineFragment;
import com.example.a70640.firebase_example.Model.Club;
import com.example.a70640.firebase_example.Model.Favorite;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private final String TAG = getClass().getSimpleName();
    public static List<Club> collectionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        favoriteClubFetch();

        TimelineFragment timelineFragment = new TimelineFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_fragment, timelineFragment)
                .commit();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.nav_notification).setActionView(R.layout.menu_dot);
        navigationView.setNavigationItemSelectedListener(this);

        ImageView imageView = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.img_profile);
        TextView userName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.name);
        userName.setText(getIntent().getStringExtra("username"));
        TextView userEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.email);
        userEmail.setText(getIntent().getStringExtra("userEmail"));
        try {
            Glide.with(getApplicationContext()).load(getIntent().getData().toString()).crossFade()
                    .thumbnail(0.5f)
                    .bitmapTransform(new CircleTransform(this))
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
        } catch (Exception e) {
        }
    }

    private void favoriteClubFetch() {

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_USERS)
                .child(FirebaseAuth.getInstance().getUid()).child(Constants.DATABASE_PATH_FAVORITE);

        mDatabase.addValueEventListener(new ValueEventListener() {

            private List<Favorite> favoriteList = new ArrayList<>();

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                favoriteList.clear();
                collectionList.clear();
                //iterating through all the values in database
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Favorite favorite = postSnapshot.getValue(Favorite.class);
                    favoriteList.add(favorite);
                }

                NavigationView navigationView = findViewById(R.id.nav_view);
                final MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_favorite_clubs);
                menuItem.getSubMenu().clear();

                for (int i=0; i<favoriteList.size(); i++){
                    //adding an event listener to fetch values
                    final int final_i = i;
                    FirebaseDatabase.getInstance().getReferenceFromUrl(favoriteList.get(i).getDatabaseURL()).
                            addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            //iterating through all the values in database
                            final Club club = snapshot.getValue(Club.class);
                            club.setDatabaseURL(snapshot.getRef().toString());
                            collectionList.add(club);
                            menuItem.getSubMenu().add(0, final_i,Menu.NONE, club.getName())
                                    .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                        @Override
                                        public boolean onMenuItemClick(MenuItem menuItem) {

                                            Intent intent = new Intent(getApplicationContext(),ClubPage.class);
                                            //new一個Bundle物件，並將要傳遞的資料傳入
                                            Bundle bundle = new Bundle();
                                            bundle.putString("name",club.getName());
                                            bundle.putString("slogan","slogan");
                                            bundle.putString("cover",club.getThumbnail());
                                            bundle.putString("URL",club.getDatabaseURL());

                                            intent.putExtras(bundle);

                                            startActivity(intent);

                                            return false;
                                        }
                                    });

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
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

//        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        FragmentManager fragmentManager = getFragmentManager();

        int id = item.getItemId();

        if (id == R.id.nav_timeline) {
            TimelineFragment timelineFragment = new TimelineFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, timelineFragment)
                    .commit();
        } else if (id == R.id.nav_club) {
            AllClubFragment allClubFragment = new AllClubFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, allClubFragment)
                    .commit();
        } else if (id == R.id.nav_notification) {
            NotificationsFragment notificationsFragment = new NotificationsFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, notificationsFragment)
                    .commit();
        }
//        else if (id == R.id.nav_history) {
//            HistoryFragment historyFragment = new HistoryFragment();
//            fragmentManager.beginTransaction()
//                    .replace(R.id.main_fragment, historyFragment)
//                    .commit();
//        }
        else if (id == R.id.nav_life_notebook) {
            LifeFragment lifeFragment = new LifeFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, lifeFragment)
                    .commit();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_contact_us) {

        } else if (id == R.id.nav_setting) {
            Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.manage_AIESEC) {
            Intent intent = new Intent(MainActivity.this,ManageClub.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
