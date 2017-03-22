package com.thesocialplaylist.user.music.fragment.musicplayer.musiclibrary;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;

import com.thesocialplaylist.user.music.R;
import com.thesocialplaylist.user.music.dto.SongDTO;
import com.thesocialplaylist.user.music.events.models.TrackPlaybackEvent;
import com.thesocialplaylist.user.music.adapters.recyclerview.OnRecyclerItemClickListener;
import com.thesocialplaylist.user.music.adapters.recyclerview.TracksListAdapter;
import com.thesocialplaylist.user.music.enums.PlaybackEvent;
import com.thesocialplaylist.user.music.enums.TracksListMode;
import com.thesocialplaylist.user.music.service.MusicService;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.ComponentName;
import android.os.IBinder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.List;

public class TracksListFragment extends Fragment {

    private View fragmentView;
    private RecyclerView tracksList;
    private TracksListAdapter adapter;
    private List<SongDTO> tracks;

    private MusicService musicService;
    private Intent musicServiceIntent;
    private Context appContext;
    private boolean isBoundToService;
    private LinearLayoutManager layoutManager;

    private static final String TRACKS_LIST_KEY = "tracksList";
    private static final String TRACKS_LIST_MODE_KEY = "tracksListMode";

    private class musicPlayerModeOnClickListener implements OnRecyclerItemClickListener {
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

    public TracksListFragment() {

    }

    public static TracksListFragment newInstance(List<SongDTO> songDTOs, TracksListMode mode) {
        TracksListFragment tracksListFragment = new TracksListFragment();
        Bundle tracksList = new Bundle();
        tracksList.putSerializable(TRACKS_LIST_KEY, (Serializable) songDTOs);
        tracksList.putSerializable(TRACKS_LIST_MODE_KEY, (Serializable) mode);
        tracksListFragment.setArguments(tracksList);
        return tracksListFragment;
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
        public void onServiceDisconnected(ComponentName name) {
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
        fragmentView = inflater.inflate(R.layout.fragment_tracks_list, container, false);
        initialize();
        return fragmentView;
    }

    private void initialize() {
        tracks = (List<SongDTO>) getArguments().getSerializable(TRACKS_LIST_KEY);
        TracksListMode mode = (TracksListMode) getArguments().getSerializable(TRACKS_LIST_MODE_KEY);
        Log.i("From fragment", tracks.size() + "");
        appContext = getActivity().getApplicationContext();
        adapter = new TracksListAdapter(tracks, mode, getActivity().getApplicationContext());

        if(mode.equals(TracksListMode.MUSIC_PLAYER_MODE))
            adapter.setOnRecyclerItemClickListener(new musicPlayerModeOnClickListener());

        tracksList = (RecyclerView) fragmentView.findViewById(R.id.tracks_list);
        tracksList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        tracksList.setAdapter(adapter);

        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        appContext.unbindService(musicServiceConn);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTrackPlayback(TrackPlaybackEvent trackPlaybackEvent) {
        PlaybackEvent event = trackPlaybackEvent.getPlaybackEvent();
        if(event.equals(PlaybackEvent.NEW_TRACK)) {
            adapter.setSelectedRow(musicService.getCurrentPlayingPosition());
            adapter.notifyItemChanged(musicService.getCurrentPlayingPosition());
        }
    }
}
