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
import com.thesocialplaylist.user.music.dto.FriendDTO;
import com.thesocialplaylist.user.music.recyclerview.adapters.FriendsListAdapter;
import com.thesocialplaylist.user.music.recyclerview.adapters.OnRecyclerItemClickListener;

import java.io.Serializable;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsListFragment extends Fragment {

    private static final String ARG_FRIENDS_LIST = "FRIENDS_LIST";

    private List<FriendDTO> friendsList;
    private RecyclerView friendsListRecyclerView;
    private View fragmentView;
    private Context appContext;
    private FriendsListAdapter adapter;

    public FriendsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param friendsList List of friends passed from the activity.
     * @return A new instance of fragment FriendsListFragment.
     */

    public static FriendsListFragment newInstance(List<FriendDTO> friendsList) {
        FriendsListFragment fragment = new FriendsListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_FRIENDS_LIST, (Serializable) friendsList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            friendsList = (List<FriendDTO>) getArguments().getSerializable(ARG_FRIENDS_LIST);
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
        friendsListRecyclerView = (RecyclerView) fragmentView.findViewById(R.id.friends_list);
        appContext = getActivity().getApplicationContext();

        adapter = new FriendsListAdapter(friendsList, appContext);
        adapter.setFriendsListItemClickListener(new OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent userProfileIntent = new Intent(appContext, UserProfileActivity.class);
                userProfileIntent.putExtra("USER_ID", friendsList.get(position).getFriend().getId());
                startActivity(userProfileIntent);

            }
        });
        friendsListRecyclerView.setAdapter(adapter);

        LinearLayoutManager friendsListLayoutManager = new LinearLayoutManager(appContext);
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
