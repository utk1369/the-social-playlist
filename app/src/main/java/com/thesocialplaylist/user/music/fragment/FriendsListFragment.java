package com.thesocialplaylist.user.music.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thesocialplaylist.user.music.R;
import com.thesocialplaylist.user.music.activity.UserProfileActivity;
import com.thesocialplaylist.user.music.adapters.recyclerview.HorizontalFriendsListAdapter;
import com.thesocialplaylist.user.music.dto.FriendDTO;
import com.thesocialplaylist.user.music.adapters.recyclerview.VerticalFriendsListAdapter;
import com.thesocialplaylist.user.music.adapters.recyclerview.OnRecyclerItemClickListener;

import java.io.Serializable;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsListFragment extends Fragment {

    public enum FriendsListFragmentMode {
        VERTICAL,
        HORIZONTAL
    }

    private static final String ARG_FRIENDS_LIST = "FRIENDS_LIST";
    private static final String ARG_LIST_ORIENTATION = "ORIENTATION";

    private List<FriendDTO> friendsList;
    private int listOrientation;
    private RecyclerView friendsListRecyclerView;
    private View fragmentView;
    private Context appContext;
    private VerticalFriendsListAdapter verticalFriendsListAdapter;
    private HorizontalFriendsListAdapter horizontalFriendsListAdapter;

    public FriendsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param friendsList List of friends passed from the activity.
     * @param listOrientation
     * @return A new instance of fragment FriendsListFragment.
     */

    public static FriendsListFragment newInstance(List<FriendDTO> friendsList, int listOrientation) {
        FriendsListFragment fragment = new FriendsListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_FRIENDS_LIST, (Serializable) friendsList);
        args.putInt(ARG_LIST_ORIENTATION, listOrientation);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            friendsList = (List<FriendDTO>) getArguments().getSerializable(ARG_FRIENDS_LIST);
            listOrientation = getArguments().getInt(ARG_LIST_ORIENTATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_friends_list, container, false);
        initializeFragment();
        return fragmentView;
    }

    private void initializeFragment() {
        friendsListRecyclerView = (RecyclerView) fragmentView.findViewById(R.id.friends_list_recycler_view);
        appContext = getActivity().getApplicationContext();

        if(listOrientation == LinearLayoutManager.VERTICAL) {
            verticalFriendsListAdapter = new VerticalFriendsListAdapter(friendsList, appContext);
            verticalFriendsListAdapter.setFriendsListItemClickListener(new OnRecyclerItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent userProfileIntent = new Intent(appContext, UserProfileActivity.class);
                    userProfileIntent.putExtra("USER_ID", friendsList.get(position).getFriend().getId());
                    startActivity(userProfileIntent);
                }
            });
            friendsListRecyclerView.setAdapter(verticalFriendsListAdapter);
        } else {
            horizontalFriendsListAdapter = new HorizontalFriendsListAdapter(friendsList, appContext);
            friendsListRecyclerView.setAdapter(horizontalFriendsListAdapter);
        }
        LinearLayoutManager friendsListLayoutManager = new LinearLayoutManager(appContext);
        friendsListLayoutManager.setOrientation(listOrientation);
        friendsListRecyclerView.setLayoutManager(friendsListLayoutManager);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i("entered OnAttach", "");
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
