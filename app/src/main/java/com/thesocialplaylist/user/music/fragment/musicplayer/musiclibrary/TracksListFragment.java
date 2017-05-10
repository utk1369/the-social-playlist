package com.thesocialplaylist.user.music.fragment.musicplayer.musiclibrary;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thesocialplaylist.user.music.R;
import com.thesocialplaylist.user.music.adapters.recyclerview.MusicLibraryTracksListAdapter;
import com.thesocialplaylist.user.music.adapters.recyclerview.UserProfileTracksListViewAdapter;
import com.thesocialplaylist.user.music.dto.SongDTO;
import com.thesocialplaylist.user.music.dto.UserDTO;
import com.thesocialplaylist.user.music.enums.TracksListMode;

import java.io.Serializable;
import java.util.List;

public class TracksListFragment extends Fragment {

    private View fragmentView;
    private RecyclerView tracksList;
    private MusicLibraryTracksListAdapter musicLibraryTracksListAdapter;
    private UserProfileTracksListViewAdapter userProfileTracksListViewAdapter;

    private List<SongDTO> tracks;
    private UserDTO appUserDetails;

    private Context appContext;
    private LinearLayoutManager layoutManager;

    private MusicLibraryTracksListAdapter.OnTrackItemClickListener onTrackItemClickListener;
    private MusicLibraryTracksListAdapter.OnOptionsButtonClickListener onOptionsButtonClickListener;

    private UserProfileTracksListViewAdapter.OnTrackInfoClickListener onTrackInfoClickListener;
    private UserProfileTracksListViewAdapter.OnLikeButtonClickListener onLikeButtonClickListener;

    private static final String TRACKS_LIST_KEY = "tracksList";
    private static final String TRACKS_LIST_MODE_KEY = "tracksListMode";
    private static final String APP_USER_DETAILS = "appUserDetails";

    private TracksListMode mode;

    public TracksListFragment() {

    }

    public static TracksListFragment newInstance(List<SongDTO> songDTOs, TracksListMode mode) {
        return newInstance(songDTOs, mode, null);
    }

    public static TracksListFragment newInstance(List<SongDTO> songDTOs, TracksListMode mode, UserDTO appUserDetails) {
        TracksListFragment tracksListFragment = new TracksListFragment();
        Bundle args = new Bundle();
        args.putSerializable(TRACKS_LIST_KEY, (Serializable) songDTOs);
        args.putSerializable(TRACKS_LIST_MODE_KEY, mode);
        args.putSerializable(APP_USER_DETAILS, appUserDetails);
        tracksListFragment.setArguments(args);
        return tracksListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_tracks_list, container, false);
        initialize();
        return fragmentView;
    }

    private void initialize() {
        Log.i("trackslist fragment", tracks.size() + "");
        appContext = getActivity().getApplicationContext();
        tracksList = (RecyclerView) fragmentView.findViewById(R.id.tracks_list);

        if(mode == TracksListMode.MUSIC_PLAYER_MODE) {
            musicLibraryTracksListAdapter = new MusicLibraryTracksListAdapter(tracks, appContext, onTrackItemClickListener, onOptionsButtonClickListener);
            tracksList.setAdapter(musicLibraryTracksListAdapter);
        }
        else if(mode == TracksListMode.USER_PROFILE_MODE) {
            userProfileTracksListViewAdapter = new UserProfileTracksListViewAdapter(tracks, appContext,
                    onTrackInfoClickListener, onLikeButtonClickListener, appUserDetails);
            tracksList.setAdapter(userProfileTracksListViewAdapter);
        }

        tracksList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        tracksList.setLayoutManager(layoutManager);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        tracks = (List<SongDTO>) getArguments().getSerializable(TRACKS_LIST_KEY);
        mode = (TracksListMode) getArguments().getSerializable(TRACKS_LIST_MODE_KEY);
        appUserDetails = (UserDTO) getArguments().getSerializable(APP_USER_DETAILS);

        try {
            if(mode == TracksListMode.MUSIC_PLAYER_MODE) {
                onTrackItemClickListener = (MusicLibraryTracksListAdapter.OnTrackItemClickListener) context;
                onOptionsButtonClickListener = (MusicLibraryTracksListAdapter.OnOptionsButtonClickListener) context;
            } else if(mode == TracksListMode.USER_PROFILE_MODE) {
                onTrackInfoClickListener = (UserProfileTracksListViewAdapter.OnTrackInfoClickListener) context;
                onLikeButtonClickListener = (UserProfileTracksListViewAdapter.OnLikeButtonClickListener) context;
            }
        } catch (ClassCastException e) {
            Log.d("TRACKS LIST FRAGMENT", "activity cannot be cast to listeners");
        }
    }

    public void updateDataSet(List<SongDTO> updatedSongsList) {
        if(mode == TracksListMode.MUSIC_PLAYER_MODE) {
            musicLibraryTracksListAdapter.refreshTracksList(updatedSongsList);
        } else if(mode == TracksListMode.USER_PROFILE_MODE) {
            userProfileTracksListViewAdapter.refreshTracksList(updatedSongsList);
        }
    }

    public void updateDataRange(List<SongDTO> updatedSongsList, int start, int end) {
        if(mode == TracksListMode.MUSIC_PLAYER_MODE) {
            musicLibraryTracksListAdapter.refreshTracksListRange(updatedSongsList, start, end);
        } else if(mode == TracksListMode.USER_PROFILE_MODE) {
            userProfileTracksListViewAdapter.refreshTracksListRange(updatedSongsList, start, end);
        }
    }
}
