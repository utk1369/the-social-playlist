package com.example.user.music.activity.musicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.music.service.MusicService;
import com.example.user.music.R;
import com.example.user.music.models.Song;
import com.example.user.music.utils.AlbumArtAsyncLoader;
import com.example.user.music.utils.AsyncTaskResponseHandler;

import java.io.IOException;
import java.util.List;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

public class MediaPlayerActivity extends android.support.v7.app.AppCompatActivity  {
    private TextView songTitle;
    private TextView artist;
    private ImageButton browseSongs;
    private ImageButton playPauseBtn;
    private ImageButton skipNextBtn;
    private ImageButton skipPrevBtn;
    private ImageButton nowPlayingPlaylistBtn;
    private ImageView thumbnail;
    private List<Song> songsList;
    private ActionBar actionBar;
    private Toolbar toolbar;

    private MusicService musicService;
    private Intent musicSrvIntent;
    private boolean isBoundToService = false;
    private Song nowPlaying;


    @Override
    protected void onStart() {
        super.onStart();
        musicSrvIntent = new Intent(this, MusicService.class);
        bindService(musicSrvIntent, musicserviceConnection, Context.BIND_AUTO_CREATE);
        startService(musicSrvIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        initialize();
        assignButtonActions();
    }

    private void initialize() {
        songTitle = (TextView) findViewById(R.id.song_title);
        artist = (TextView) findViewById(R.id.song_artist);
        browseSongs = (ImageButton) findViewById(R.id.browse);
        playPauseBtn = (ImageButton) findViewById(R.id.play_pause_btn);
        skipNextBtn = (ImageButton) findViewById(R.id.next_btn);
        skipPrevBtn = (ImageButton) findViewById(R.id.prev_btn);
        nowPlayingPlaylistBtn = (ImageButton) findViewById(R.id.playlist);
        thumbnail = (ImageView) findViewById(R.id.thumbnail);
        songTitle.setSelected(true);

        toolbar = (Toolbar) findViewById(R.id.media_player_toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Music Player");
    }

    private ServiceConnection musicserviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MediaPlayerServiceBinder serviceBinder = (MusicService.MediaPlayerServiceBinder) service;
            musicService = serviceBinder.getMusicService();
            //songsList = (musicService.getNowPlayingList() == null) ? AppUtil.getSongsAsList(getApplicationContext(), null, null) : musicService.getNowPlayingList();
            //musicService.setNowPlayingList(songsList);
            initializeMediaPlayer();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBoundToService = false;
        }
    };

    private void initializeMediaPlayer() {
        if(musicService == null)
            return;
        songsList = musicService.getNowPlayingList();
        populateSongAttributes(musicService.getCurrentPlayingPosition());
        if(musicService.isPlaying())
            playPauseBtn.setImageResource(R.drawable.ic_pause_white_24dp);
        else
            playPauseBtn.setImageResource(R.drawable.ic_play_arrow_white_24dp);
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeMediaPlayer();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //mediaPlayer.release();
        unbindService(musicserviceConnection);
    }

    private void assignButtonActions() {
        browseSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent libraryIntent = new Intent(MediaPlayerActivity.this, MusicLibraryActivity.class);
                startActivity(libraryIntent);
            }
        });
        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] images = new int[]{R.drawable.ic_play_arrow_white_24dp, R.drawable.ic_pause_white_24dp};
                int imageResourceIdx = musicService.playOrPauseAction();
                playPauseBtn.setImageResource(images[imageResourceIdx]);
            }
        });
        skipNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    musicService.playNextSong();
                    populateSongAttributes(musicService.getCurrentPlayingPosition());
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MediaPlayerActivity.this, "Error playing next song!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        skipPrevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    musicService.playPrevSong();
                    populateSongAttributes(musicService.getCurrentPlayingPosition());
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MediaPlayerActivity.this, "Error playing next song!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        nowPlayingPlaylistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playlistIntent = new Intent(MediaPlayerActivity.this, PlaylistActivity.class);
                startActivity(playlistIntent);
            }
        });

    }

    private void populateSongAttributes(int position) {
        if(songsList == null) {
            songTitle.setText("Select a Song!");
            artist.setText("");
            return;
        }
        nowPlaying = songsList.get(position);
        songTitle.setText(nowPlaying.getName());
        artist.setText(nowPlaying.getArtist());
        loadAlbumArt();
    }

    private void loadAlbumArt() {
        int imageViewHt = thumbnail.getHeight(); int imageViewWd = thumbnail.getWidth();

        AlbumArtAsyncLoader albumArtAsyncLoader = new AlbumArtAsyncLoader();
        albumArtAsyncLoader.setAsyncTaskResponseHandler(new AsyncTaskResponseHandler() {
            @Override
            public void onResponseFromAsyncTask(Object[] params) {
                thumbnail.setImageBitmap((Bitmap) params[0]);
            }
        });
        Toast.makeText(MediaPlayerActivity.this," Width: " + imageViewWd + "Ht: " + imageViewHt, Toast.LENGTH_SHORT).show();
        albumArtAsyncLoader.execute(new Object[]{this, nowPlaying.getAlbumId(), imageViewHt, imageViewWd});
    }

    @Override
    public void onWindowFocusChanged(boolean focus) {
        super.onWindowFocusChanged(focus);
        loadAlbumArt();
    }
}
