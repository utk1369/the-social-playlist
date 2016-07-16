package com.example.user.music.fragment.musicplayer.mediacontroller;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
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

import com.example.user.music.R;
import com.example.user.music.models.Song;
import com.example.user.music.service.MusicService;
import com.example.user.music.utils.AppUtil;

import java.io.FileNotFoundException;
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
    private List<Song> songsList;


    @Override
    public void onStart() {
        super.onStart();
        musicServiceIntent = new Intent(appContext, MusicService.class);
        appContext.bindService(musicServiceIntent, musicServiceConn, Context.BIND_AUTO_CREATE);
        appContext.startService(musicServiceIntent);
    }

    private ServiceConnection musicServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MediaPlayerServiceBinder musicServiceBinder = (MusicService.MediaPlayerServiceBinder) service;
            musicService = musicServiceBinder.getMusicService();
            songsList = AppUtil.getSongsAsList(getActivity().getApplicationContext(), null, null);
            musicService.setNowPlayingList(songsList);
            initializeFragment();
            isBoundToService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBoundToService = false;
        }
    };

    private void initializeFragment() {
        if(musicService == null)
            return;

        populateSongAttributes(musicService.getCurrentPlayingPosition());
        if(musicService.isPlaying())
            playPauseBtn.setImageResource(R.drawable.ic_pause_circle_outline_white_24dp);
        else
            playPauseBtn.setImageResource(R.drawable.ic_play_circle_outline_white_24dp);
    }

    private void populateSongAttributes(int currentPlayingPosition) {
        Song nowPlaying = songsList.get(currentPlayingPosition);
        songTitle.setText(nowPlaying.getName());
        songArtist.setText(nowPlaying.getArtist());
        try {
            Log.i("ALBUM ID", nowPlaying.getAlbumId()+"");
            Bitmap albumImg = AppUtil.getAlbumArt(appContext, nowPlaying.getAlbumId(), albumArt.getHeight(), albumArt.getWidth());
            if(albumImg != null)
                albumArt.setImageBitmap(albumImg);
        } catch (FileNotFoundException e) {
            Log.i("No bitmap available", "");
            albumArt.setImageBitmap(AppUtil.getDefaultAlbumArt(appContext));
            //e.printStackTrace();
        }
    }

    private void assignButtonActions() {
        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] images = new int[]{R.drawable.ic_play_circle_outline_white_24dp, R.drawable.ic_pause_circle_outline_white_24dp};
                int imageResourceIdx = musicService.playOrPauseAction();
                playPauseBtn.setImageResource(images[imageResourceIdx]);
            }
        });
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
        //Toast.makeText(appContext, "Resuming Playback in Fragment", Toast.LENGTH_SHORT).show();
        super.onResume();
        initializeFragment();
    }

    private void initialize() {
        appContext = getActivity().getApplicationContext();
        albumArt = (ImageView) fragmentView.findViewById(R.id.album_art);
        songTitle = (TextView) fragmentView.findViewById(R.id.song_title);
        songArtist = (TextView) fragmentView.findViewById(R.id.song_artist);
        playPauseBtn = (ImageButton) fragmentView.findViewById(R.id.play_pause_btn);
        songTitle.setSelected(true);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        appContext.unbindService(musicServiceConn);
    }
}
