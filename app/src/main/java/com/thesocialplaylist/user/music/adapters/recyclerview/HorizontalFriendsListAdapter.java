package com.thesocialplaylist.user.music.adapters.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.thesocialplaylist.user.music.R;
import com.thesocialplaylist.user.music.dto.FriendDTO;

import java.util.List;

/**
 * Created by user on 22-03-2017.
 */

public class HorizontalFriendsListAdapter extends RecyclerView.Adapter<HorizontalFriendsListAdapter.FriendsListElementHolder> {

    private List<FriendDTO> friendsList;
    private Context appContext;
    private OnRecyclerItemClickListener friendsListItemClickListener;

    public HorizontalFriendsListAdapter(List<FriendDTO> friendsList, Context context) {
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
    public FriendsListElementHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_element, parent, false);
        return new FriendsListElementHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FriendsListElementHolder holder, int position) {
        holder.name.setText(friendsList.get(position).getFriend().getName());
        Picasso.with(appContext)
                .load(friendsList.get(position).getFriend().getImageUrl())
                .fit()
                .placeholder(R.drawable.ic_person_black_48dp)
                .error(R.drawable.ic_person_black_48dp)
                .into(holder.displayPic);
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    public class FriendsListElementHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private CircularImageView displayPic;

        private TextView name;

        public FriendsListElementHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            displayPic = (CircularImageView) itemView.findViewById(R.id.display_pic);
            name = (TextView) itemView.findViewById(R.id.friend_name);
        }

        @Override
        public void onClick(View view) {
            friendsListItemClickListener.onItemClick(view, getAdapterPosition());
        }
    }
}
