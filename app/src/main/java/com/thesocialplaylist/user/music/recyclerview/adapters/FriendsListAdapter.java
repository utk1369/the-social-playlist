package com.thesocialplaylist.user.music.recyclerview.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.thesocialplaylist.user.music.R;
import com.thesocialplaylist.user.music.activity.UserProfileActivity;
import com.thesocialplaylist.user.music.dto.FriendDTO;
import com.thesocialplaylist.user.music.dto.UserDTO;

import java.io.Serializable;
import java.util.List;

/**
 * Created by user on 26-07-2016.
 */
public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.FriendsListRowHolder> {

    private List<FriendDTO> friendsList;
    private Context appContext;
    private OnRecyclerItemClickListener friendsListItemClickListener;

    public FriendsListAdapter(List<FriendDTO> friendsList, Context context) {
        this.friendsList = friendsList;
        this.appContext = context;
    }

    public void setFriendsList(List<FriendDTO> friendsList) {
        this.friendsList = friendsList;
    }

    public void setFriendsListItemClickListener(OnRecyclerItemClickListener friendsListItemClickListener) {
        this.friendsListItemClickListener = friendsListItemClickListener;
    }

    @Override
    public FriendsListRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_row, parent, false);
        return new FriendsListRowHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FriendsListRowHolder holder, int position) {
        holder.name.setText(friendsList.get(position).getFriend().getName());
        holder.status.setText(friendsList.get(position).getFriend().getStatus());
        Picasso.with(appContext)
                .load(friendsList.get(position).getFriend().getImageUrl())
                .fit()
                .placeholder(R.drawable.ic_person_black_48dp)
                .error(R.drawable.ic_person_black_48dp)
                .into(holder.displayPicture);
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    public class FriendsListRowHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CircularImageView displayPicture;
        private TextView name;
        private TextView status;

        public FriendsListRowHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            displayPicture = (CircularImageView) itemView.findViewById(R.id.display_picture);
            name = (TextView) itemView.findViewById(R.id.name);
            status = (TextView) itemView.findViewById(R.id.status);
        }

        @Override
        public void onClick(View view) {
            Log.i("Friends List Item no. " + getAdapterPosition(), "Clicked");
            friendsListItemClickListener.onItemClick(view, getAdapterPosition());
        }
    }
}
