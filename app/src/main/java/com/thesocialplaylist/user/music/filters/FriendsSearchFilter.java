package com.thesocialplaylist.user.music.filters;

import android.widget.Filter;

import com.thesocialplaylist.user.music.adapters.custom.FriendsSearchAutoCompleteAdapter;
import com.thesocialplaylist.user.music.dto.FriendDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 12-03-2017.
 */

public class FriendsSearchFilter extends Filter {

    private FriendsSearchAutoCompleteAdapter adapter;

    private List<FriendDTO> completeFriendsList;

    private List<FriendDTO> filteredFriendsList;

    public FriendsSearchFilter(FriendsSearchAutoCompleteAdapter adapter, List<FriendDTO> completeFriendsList) {
        this.adapter = adapter;
        this.completeFriendsList = completeFriendsList;
        this.filteredFriendsList = new ArrayList<>();
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        filteredFriendsList.clear();
        FilterResults filterResults = new FilterResults();
        if(charSequence == null ||
                charSequence.length() == 0) {
            filteredFriendsList.addAll(completeFriendsList);
        } else {
            String filterPattern = charSequence.toString().toLowerCase().trim();
            for(FriendDTO friendDTO: completeFriendsList) {
                if(friendDTO.getFriend().getName().toLowerCase().contains(filterPattern))
                    filteredFriendsList.add(friendDTO);
            }
        }
        filterResults.values = filteredFriendsList;
        filterResults.count = filteredFriendsList.size();
        return filterResults;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        adapter.setFilteredList((List<FriendDTO>) filterResults.values);
        adapter.notifyDataSetChanged();
    }
}
