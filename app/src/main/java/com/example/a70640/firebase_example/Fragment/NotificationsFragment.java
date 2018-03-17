package com.example.a70640.firebase_example.Fragment;

import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.a70640.firebase_example.Adapter.NotificationAdapter;
import com.example.a70640.firebase_example.Model.Notification;
import com.example.a70640.firebase_example.MyApplication;
import com.example.a70640.firebase_example.R;
import com.example.a70640.firebase_example.Recipe;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by min on 2018/2/19.
 */

public class NotificationsFragment extends Fragment {
    private final String TAG = getClass().getSimpleName();
    private NotificationAdapter notificationAdapter;
    private ArrayList<Notification> notificationList;
    private ShimmerFrameLayout shimmerFrameLayout;

    private static final String URL = "https://firebasestorage.googleapis.com/v0/b/fir-example-b9977.appspot.com/o/notification.json?alt=media&token=0d0835aa-7760-425f-8b78-0b488b7c8330";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.nav_notification);

        View v = getView();
        shimmerFrameLayout = v.findViewById(R.id.shimmer_view_container);
        shimmerFrameLayout.startShimmerAnimation();

        RecyclerView notification_rv = (RecyclerView) v.findViewById(R.id.recycler_view);
        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(getContext(), notificationList);

        RecyclerView.LayoutManager activity_lm = new GridLayoutManager(getContext(), 1);
        notification_rv.setLayoutManager(activity_lm);
        notification_rv.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(0), true));
        notification_rv.setItemAnimator(new DefaultItemAnimator());
        notification_rv.setAdapter(notificationAdapter);

        fetchNotification();
    }

    private void fetchNotification() {
        JsonArrayRequest request = new JsonArrayRequest(URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null) {
                            Toast.makeText(getContext(), "Couldn't fetch the menu! Pleas try again.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        Log.d(TAG, "onResponse: " + response.toString());
                        List<Notification> notifications = new Gson().fromJson(response.toString(), new TypeToken<List<Notification>>() {
                        }.getType());

                        // adding recipes to cart list
                        notificationList.clear();
                        notificationList.addAll(notifications);

                        shimmerFrameLayout.stopShimmerAnimation();
                        shimmerFrameLayout.setVisibility(View.GONE);

                        // refreshing recycler view
                        notificationAdapter.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
