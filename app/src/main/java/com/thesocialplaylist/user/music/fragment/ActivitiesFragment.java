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
import com.thesocialplaylist.user.music.TheSocialPlaylistApplication;
import com.thesocialplaylist.user.music.activity.UserProfileActivity;
import com.thesocialplaylist.user.music.adapters.recyclerview.ActivityListAdapter;
import com.thesocialplaylist.user.music.adapters.recyclerview.HorizontalFriendsListAdapter;
import com.thesocialplaylist.user.music.adapters.recyclerview.OnRecyclerItemClickListener;
import com.thesocialplaylist.user.music.adapters.recyclerview.VerticalFriendsListAdapter;
import com.thesocialplaylist.user.music.api.declaration.UserApi;
import com.thesocialplaylist.user.music.dto.FriendDTO;
import com.thesocialplaylist.user.music.dto.SocialActivityDTO;
import com.thesocialplaylist.user.music.manager.UserDataAndRelationsManager;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;

/**
 * Created by user on 24-03-2017.
 */

public class ActivitiesFragment extends Fragment {

    private static final String ARG_ACTIVITIES_LIST = "ACTIVITIES_LIST";

    private List<SocialActivityDTO> socialActivityDTOs;
    private RecyclerView activitiesListRecyclerView;
    private View fragmentView;
    private Context appContext;

    @Inject
    UserDataAndRelationsManager userDataAndRelationsManager;

    private ActivityListAdapter adapter;

    public ActivitiesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param socialActivityDTOs List of activities passed from the activity.
     * @return A new instance of fragment ActivitiesFragment.
     */

    public static ActivitiesFragment newInstance(List<SocialActivityDTO> socialActivityDTOs) {
        ActivitiesFragment fragment = new ActivitiesFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ACTIVITIES_LIST, (Serializable) socialActivityDTOs);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            socialActivityDTOs = (List<SocialActivityDTO>) getArguments().getSerializable(ARG_ACTIVITIES_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_activities_list, container, false);
        initializeFragment();
        return fragmentView;
    }

    private void initializeFragment() {
        ((TheSocialPlaylistApplication) getActivity().getApplication()).getUserDataAndRelationsManagerComponent().inject(this);

        activitiesListRecyclerView = (RecyclerView) fragmentView.findViewById(R.id.activities_list_recycler_view);
        appContext = getActivity().getApplicationContext();

        adapter = new ActivityListAdapter(socialActivityDTOs,
                userDataAndRelationsManager.getAllUsersFromCacheAsMap(), getActivity().getApplicationContext());

        LinearLayoutManager activitiesListLayoutManager = new LinearLayoutManager(appContext);
        activitiesListLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        activitiesListRecyclerView.setAdapter(adapter);
        activitiesListRecyclerView.setLayoutManager(activitiesListLayoutManager);
    }

    public void updateDataSet(List<SocialActivityDTO> updatedListOfActivities) {
        this.socialActivityDTOs = updatedListOfActivities;
        adapter.updateDataSet(updatedListOfActivities);
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
