package com.thesocialplaylist.user.music.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thesocialplaylist.user.music.dto.UserDTO;
import com.thesocialplaylist.user.music.recyclerview.adapters.FriendsListAdapter;
import com.thesocialplaylist.user.music.R;

import java.io.Serializable;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendsList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsList extends Fragment {

    private static final String ARG_FRIENDS_LIST = "FRIENDS_LIST";

    private List<UserDTO> friendsList;
    private RecyclerView friendsListRecyclerView;
    private View fragmentView;
    private Context appContext;

    public FriendsList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param friendsList List of friends passed from the activity.
     * @return A new instance of fragment FriendsList.
     */

    public static FriendsList newInstance(List<UserDTO> friendsList) {
        FriendsList fragment = new FriendsList();
        Bundle args = new Bundle();
        args.putSerializable(ARG_FRIENDS_LIST, (Serializable) friendsList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            friendsList = (List<UserDTO>) getArguments().getSerializable(ARG_FRIENDS_LIST);
            Log.i("Frm FriendsListFragment", friendsList.get(0).getName());
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

        FriendsListAdapter adapter = new FriendsListAdapter(friendsList, appContext);
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
