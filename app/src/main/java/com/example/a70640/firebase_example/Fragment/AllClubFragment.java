package com.example.a70640.firebase_example.Fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.a70640.firebase_example.Adapter.ClubsAdapter;
import com.example.a70640.firebase_example.ClubPage;
import com.example.a70640.firebase_example.Constants;
import com.example.a70640.firebase_example.Model.Club;
import com.example.a70640.firebase_example.Model.Favorite;
import com.example.a70640.firebase_example.MyApplication;
import com.example.a70640.firebase_example.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 70640 on 2018/2/4.
 */

public class AllClubFragment extends Fragment implements ClubsAdapter.ClubAdapterListener {
    private String TAG = getClass().getSimpleName();

    private ClubsAdapter adapter;
    private List<Club> clubList;
    private ShimmerFrameLayout mShimmerViewContainer;
    private FirebaseRecyclerAdapter<Club, ClubsAdapter.MyViewHolder> mAdapter;
    private SearchView searchView;

    private static String favorite;
    private static final String CLUBURL = "https://firebasestorage.googleapis.com/v0/b/fir-example-b9977.appspot.com/o/Clubs%2Fallclubs.json?alt=media&token=10019bcd-4e17-46b3-81fa-cf0e02ea8ee0";

    public AllClubFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_all_club, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {

        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.fragment_club_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                adapter.getFilter().filter(query);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                // Do Fragment menu item stuff here
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle("全部社團");

        View v = getView();

        mShimmerViewContainer = v.findViewById(R.id.shimmer_view_container);
        mShimmerViewContainer.startShimmerAnimation();

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.club_recycler_view);
        clubList = new ArrayList<>();
        adapter = new ClubsAdapter(getContext(), clubList,this);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        // click listener
//        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
//            @Override
//            public void onClick(View view, int position) {
//                Club club = clubList.get(position);
//                Toast.makeText(getContext(), club.getName() + " is selected!", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onLongClick(View view, int position) {
//
//            }
//        }));

        firebaseFetch();
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
    public void onAddToFavoriteSelected(final int position) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_USERS)
                .child(FirebaseAuth.getInstance().getUid()).child(Constants.DATABASE_PATH_FAVORITE);

        favorite = clubList.get(position).getDatabaseURL();
        Favorite favoriteClub = new Favorite(favorite);
        mDatabase.child(mDatabase.push().getKey()).setValue(favoriteClub);
        checkFavorite();

        Snackbar.make(getView(), "加入我的最愛", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    @Override
    public void onClubSelected(Club club, ClubsAdapter.MyViewHolder holder) {
        Intent intent = new Intent(getActivity().getApplicationContext(),ClubPage.class);
        //new一個Bundle物件，並將要傳遞的資料傳入
        Bundle bundle = new Bundle();
        bundle.putString("name",club.getName());
        bundle.putString("slogan","slogan");
        bundle.putString("cover",club.getThumbnail());
        bundle.putString("URL",club.getDatabaseURL());
        intent.putExtras(bundle);

        Pair<View,String> pair1 = Pair.create((View) holder.thumbnail,"myImage");
        Pair<View,String> pair2 = Pair.create((View) holder.title,"myName");
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),pair1,pair2);

        startActivity(intent,optionsCompat.toBundle());
    }

    private void firebaseFetch(){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_CLUBS);

        //adding an event listener to fetch values
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                clubList.clear();
                //iterating through all the values in database
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Club club = postSnapshot.getValue(Club.class);
                    club.setDatabaseURL(postSnapshot.getRef().toString());
                    clubList.add(club);
                }
                // refreshing recycler view
                adapter.notifyDataSetChanged();

                // stop animating Shimmer and hide the layout
                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * method make volley network call and parses json
     */
    private void fetchClubs() {
        JsonArrayRequest request = new JsonArrayRequest(CLUBURL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "onResponse: " +response);
                        if (response == null) {
                            Toast.makeText(getContext(), "Couldn't fetch the clubs! Pleas try again.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        List<Club> clubs = new Gson().fromJson(response.toString(), new TypeToken<List<Club>>() {
                        }.getType());

                        // adding clubs to clubList
                        clubList.clear();
                        clubList.addAll(clubs);

                        // refreshing recycler view
                        adapter.notifyDataSetChanged();

                        // stop animating Shimmer and hide the layout
                        mShimmerViewContainer.stopShimmerAnimation();
                        mShimmerViewContainer.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        MyApplication.getInstance().addToRequestQueue(request);
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
