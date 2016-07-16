package com.example.user.music.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.music.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendsList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsList extends Fragment {

    private static final String ARG_FRIENDS_LIST = "FRIENDS_LIST";

    private JSONArray friendsList;

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

    public static FriendsList newInstance(String friendsList) {
        FriendsList fragment = new FriendsList();
        Bundle args = new Bundle();
        args.putString(ARG_FRIENDS_LIST, friendsList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {
                friendsList = new JSONArray(getArguments().getString(ARG_FRIENDS_LIST));
                Log.i("Frm FriendsListFragment", friendsList.toString(2));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initializeFragment();
        return inflater.inflate(R.layout.fragment_friends_list, container, false);
    }

    private void initializeFragment() {

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
