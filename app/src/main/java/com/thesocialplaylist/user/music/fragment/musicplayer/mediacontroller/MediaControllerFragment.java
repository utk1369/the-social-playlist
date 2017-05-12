package com.thesocialplaylist.user.music.fragment.musicplayer.mediacontroller;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.thesocialplaylist.user.music.R;
import com.thesocialplaylist.user.music.dto.SongDTO;
import com.thesocialplaylist.user.music.enums.PlaybackEvent;
import com.thesocialplaylist.user.music.events.models.TrackPlaybackEvent;
import com.thesocialplaylist.user.music.events.models.TracksListUpdateEvent;
import com.thesocialplaylist.user.music.service.MusicService;
import com.thesocialplaylist.user.music.utils.ImageUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.List;

/**
 * Created by user on 16-04-2016.
 */
public class MediaControllerFragment extends Fragment {
    private View fragmentView;
    private ImageView albumArt;
    private TextView songTitle;
    private TextView songArtist;
    private ImageButton playPauseBtn;


    private MusicService musicService;
    private Intent musicServiceIntent;
    private Context appContext;
    private boolean isBoundToService;
    private List<SongDTO> songsList;

    private static final String ARG_SONGS_LIST = "SongsList";

    public static MediaControllerFragment newInstance(List<SongDTO> songsList) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_SONGS_LIST, (Serializable) songsList);
        MediaControllerFragment mediaControllerFragment = new MediaControllerFragment();
        mediaControllerFragment.setArguments(args);
        return mediaControllerFragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        musicServiceIntent = new Intent(appContext, MusicService.class);
        appContext.startService(musicServiceIntent);
    }

    private ServiceConnection musicServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MediaPlayerServiceBinder musicServiceBinder = (MusicService.MediaPlayerServiceBinder) service;
            musicService = musicServiceBinder.getMusicService();
            musicService.setNowPlayingList(songsList);
            populateSongAttributes(musicService.getCurrentPlayingPosition());
            isBoundToService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBoundToService = false;
        }
    };

    private void populateSongAttributes(int currentPlayingPosition) {
        if(songsList == null || songsList.size() == 0)
            return;

        SongDTO nowPlaying = songsList.get(currentPlayingPosition);
        setPlayPauseButton();
        songTitle.setText(nowPlaying.getMetadata().getTitle());
        songArtist.setText(nowPlaying.getMetadata().getArtist());
        ImageUtil.loadAlbumArt(getActivity().getApplicationContext(),
                nowPlaying.getMetadata().getAlbumId(), albumArt);
    }

    private void assignButtonActions() {
        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicService.isPlaying())
                    musicService.pause();
                else
                    musicService.play();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup vg, Bundle savedInstanceState){
        fragmentView =  inflater.inflate(R.layout.media_controller_fragment, vg, false);
        initialize();
        assignButtonActions();
        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        appContext.bindService(musicServiceIntent, musicServiceConn, Context.BIND_AUTO_CREATE);
    }

    private void initialize() {
        songsList = (List<SongDTO>) getArguments().getSerializable(ARG_SONGS_LIST);
        appContext = getActivity().getApplicationContext();
        albumArt = (ImageView) fragmentView.findViewById(R.id.album_art);
        songTitle = (TextView) fragmentView.findViewById(R.id.song_title);
        songArtist = (TextView) fragmentView.findViewById(R.id.song_artist);
        playPauseBtn = (ImageButton) fragmentView.findViewById(R.id.play_pause_btn);
        songTitle.setSelected(true);

        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        Log.i("MEDIACONTROLLERFRAGMENT", "onPause Called.");
        super.onPause();
        appContext.unbindService(musicServiceConn);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //appContext.unbindService(musicServiceConn);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTrackPlaybackEvent(TrackPlaybackEvent trackPlaybackEvent) {
        PlaybackEvent event = trackPlaybackEvent.getPlaybackEvent();
        if(event.equals(PlaybackEvent.NEW_TRACK)) {
            populateSongAttributes(musicService.getCurrentPlayingPosition());
        } else if(event.equals(PlaybackEvent.PAUSE)) {
            playPauseBtn.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        } else if(event.equals(PlaybackEvent.PLAY)) {
            playPauseBtn.setImageResource(R.drawable.ic_pause_white_24dp);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTracksListUpdate(TracksListUpdateEvent event) {
        songsList = event.getSongDTOs();
    }


    private void setPlayPauseButton() {
        playPauseBtn.setImageResource(
                musicService.isPlaying() ?
                        R.drawable.ic_pause_white_24dp : R.drawable.ic_play_arrow_white_24dp);
    }
}
