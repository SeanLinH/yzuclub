package com.example.a70640.firebase_example.Adapter;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.a70640.firebase_example.Model.Club;
import com.example.a70640.firebase_example.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 70640 on 2018/2/8.
 */

public class ClubsAdapter extends RecyclerView.Adapter<ClubsAdapter.MyViewHolder> implements Filterable {
    private String TAG = getClass().getSimpleName();
    private Context mContext;
    private List<Club> clubList;
    private List<Club> clubListFiltered;
    private ClubAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView thumbnail , overflow;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.club_title);
            thumbnail = (ImageView) view.findViewById(R.id.club_thumbnail);
            overflow = (ImageView) view.findViewById(R.id.club_overflow);
        }
    }


    public ClubsAdapter(Context mContext, List<Club> clubList, ClubAdapterListener listener) {
        this.mContext = mContext;
        this.clubList = clubList;
        this.listener = listener;
        this.clubListFiltered = clubList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.club_cardview, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Club club = clubListFiltered.get(position);
        holder.title.setText(club.getName());

        // loading album cover using Glide library
        Glide.with(mContext).load(club.getThumbnail()).into(holder.thumbnail);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow, position);
            }
        });


        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // send selected club in callback
                listener.onClubSelected(club,holder);
            }
        });
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view,int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_club, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position));
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        int position;

        public MyMenuItemClickListener(int position) {
            this.position = position;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_favourite:
                    listener.onAddToFavoriteSelected(position);
                    return true;
                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return clubListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    clubListFiltered = clubList;
                } else {
                    List<Club> filteredList = new ArrayList<>();
                    for (Club row : clubList) {
//                         name match condition. this might differ depending on your requirement
//                         here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    clubListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = clubListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                clubListFiltered = (ArrayList<Club>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ClubAdapterListener {
        void onAddToFavoriteSelected(int position);

        void onClubSelected(Club club,MyViewHolder holder);
    }
}
