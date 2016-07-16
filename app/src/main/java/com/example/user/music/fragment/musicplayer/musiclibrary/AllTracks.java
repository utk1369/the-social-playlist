package com.example.user.music.fragment.musicplayer.musiclibrary;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;

import com.example.user.music.R;
import com.example.user.music.TracksListAdapter;
import com.example.user.music.enums.TracksListMode;
import com.example.user.music.models.Song;
import com.example.user.music.utils.AppUtil;
import com.example.user.music.service.MusicService;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.ComponentName;
import android.os.IBinder;

import java.io.Serializable;
import java.util.List;

public class AllTracks extends Fragment {

    private View fragmentView;
    private RecyclerView tracksList;
    private TracksListAdapter adapter;
    private List<Song> tracks;

    private MusicService musicService;
    private Intent musicServiceIntent;
    private Context appContext;
    private boolean isBoundToService;
    private LinearLayoutManager layoutManager;


    private static final String TRACKS_LIST_KEY = "tracksList";
    private static final String TRACKS_LIST_MODE_KEY = "tracksListMode";

    private class musicPlayerModeOnClickListener implements TracksListAdapter.OnRecyclerItemClickListener {
        @Override
        public void onItemClick(View view, int position) {
            musicService.setCurrentPlayingPosition(position);
            adapter.setSelectedRow(position);
            adapter.highlightRow(view, true);
            try {
                musicService.playSong(tracks, position);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public AllTracks() {

    }

    public static AllTracks newInstance(List<Song> songs, TracksListMode mode) {
        AllTracks allTracks = new AllTracks();
        Bundle tracksList = new Bundle();
        tracksList.putSerializable(TRACKS_LIST_KEY, (Serializable) songs);
        tracksList.putSerializable(TRACKS_LIST_MODE_KEY, (Serializable) mode);
        allTracks.setArguments(tracksList);
        return allTracks;
    }

    private ServiceConnection musicServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name,  IBinder service) {
            MusicService.MediaPlayerServiceBinder musicServiceBinder = (MusicService.MediaPlayerServiceBinder) service;
            musicService = musicServiceBinder.getMusicService();
            isBoundToService = true;
            tracksList.setLayoutManager(layoutManager);
        }

        @Override
        public void onServiceDisconnected(android.content.ComponentName name) {
            isBoundToService = false;
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        musicServiceIntent = new Intent(appContext, MusicService.class);
        appContext.bindService(musicServiceIntent, musicServiceConn, Context.BIND_AUTO_CREATE);
        appContext.startService(musicServiceIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_all_tracks, container, false);
        initialize();
        return fragmentView;
    }

    private void initialize() {
        tracks = (List<Song>) getArguments().getSerializable(TRACKS_LIST_KEY);
        TracksListMode mode = (TracksListMode) getArguments().getSerializable(TRACKS_LIST_MODE_KEY);
        Log.i("From fragment", tracks.size() + "");
        appContext = getActivity().getApplicationContext();
        adapter = new TracksListAdapter(tracks, mode, getActivity().getApplicationContext());
        adapter.setOnRecyclerItemClickListener(new musicPlayerModeOnClickListener());

        tracksList = (RecyclerView) fragmentView.findViewById(R.id.tracks_list);
        tracksList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        tracksList.setAdapter(adapter);
    }
}
