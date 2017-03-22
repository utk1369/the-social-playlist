package com.thesocialplaylist.user.music.adapters.custom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.thesocialplaylist.user.music.R;
import com.thesocialplaylist.user.music.dto.FriendDTO;
import com.thesocialplaylist.user.music.dto.UserDTO;
import com.thesocialplaylist.user.music.filters.FriendsSearchFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 12-03-2017.
 */

public class FriendsSearchAutoCompleteAdapter extends ArrayAdapter<FriendDTO> {

    private List<FriendDTO> completeFriendsList;

    private List<FriendDTO> filteredList = new ArrayList<>();

    public void setFilteredList(List<FriendDTO> filteredList) {
        this.filteredList = filteredList;
    }

    public FriendsSearchAutoCompleteAdapter(Context context, List<FriendDTO> completeFriendsList) {
        super(context, 0, completeFriendsList);
        this.completeFriendsList = completeFriendsList;
    }

    @Override
    public Filter getFilter() {
        return new FriendsSearchFilter(this, completeFriendsList);
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FriendDTO friendDTO = filteredList.get(position);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.friends_row, parent, false);

        TextView name = (TextView) convertView.findViewById(R.id.name);
        CircularImageView displayPicture = (CircularImageView) convertView.findViewById(R.id.display_picture);
        name.setText(friendDTO.getFriend().getName());
        Picasso.with(getContext())
                .load(friendDTO.getFriend().getImageUrl())
                .fit()
                .placeholder(R.drawable.ic_person_black_48dp)
                .error(R.drawable.ic_person_black_48dp)
                .into(displayPicture);

        return convertView;
    }
}
