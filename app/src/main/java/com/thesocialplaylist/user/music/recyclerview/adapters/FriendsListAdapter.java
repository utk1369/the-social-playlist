package com.thesocialplaylist.user.music.recyclerview.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thesocialplaylist.user.music.R;
import com.thesocialplaylist.user.music.dto.UserDTO;

import java.util.List;

/**
 * Created by user on 26-07-2016.
 */
public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.FriendsListRowHolder> {

    private List<UserDTO> friendsList;
    private Context appContext;

    public FriendsListAdapter(List<UserDTO> friendsList, Context context) {
        this.friendsList = friendsList;
        this.appContext = context;
    }

    @Override
    public FriendsListRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_row, parent, false);
        return new FriendsListRowHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FriendsListRowHolder holder, int position) {
        holder.name.setText(friendsList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    public class FriendsListRowHolder extends RecyclerView.ViewHolder {
        private ImageView displayPicture;
        private TextView name;
        private TextView extraInfo;

        public FriendsListRowHolder(View itemView) {
            super(itemView);
            displayPicture = (ImageView) itemView.findViewById(R.id.display_picture);
            name = (TextView) itemView.findViewById(R.id.name);
            extraInfo = (TextView) itemView.findViewById(R.id.extra_info);
        }
    }
}
