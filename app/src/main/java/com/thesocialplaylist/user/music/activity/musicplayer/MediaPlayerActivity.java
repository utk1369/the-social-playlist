package com.thesocialplaylist.user.music.activity.musicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.thesocialplaylist.user.music.R;
import com.thesocialplaylist.user.music.TheSocialPlaylistApplication;
import com.thesocialplaylist.user.music.dto.SongDTO;
import com.thesocialplaylist.user.music.enums.PlaybackEvent;
import com.thesocialplaylist.user.music.enums.SocialActivityType;
import com.thesocialplaylist.user.music.events.models.TrackPlaybackEvent;
import com.thesocialplaylist.user.music.events.models.TracksListUpdateEvent;
import com.thesocialplaylist.user.music.manager.MusicLibraryManager;
import com.thesocialplaylist.user.music.service.MusicService;
import com.thesocialplaylist.user.music.utils.AppUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

public class MediaPlayerActivity extends AppCompatActivity {
    private TextView songTitle;
    private TextView artist;
    private TextView album;
    private ImageButton browseSongs;
    private ImageButton playPauseBtn;
    private ImageButton skipNextBtn;
    private ImageButton skipPrevBtn;
    private ImageButton nowPlayingPlaylistBtn;
    private ImageView thumbnail;
    private List<SongDTO> songsList;
    private ActionBar actionBar;
    private Toolbar toolbar;
    private ImageButton searchBtn;
    private Button dedicateBtn;
    private Button shareBtn;
    private Button recommendBtn;
    private RatingBar ratingBar;
    //private TextView youtubeTitle;

    private MusicService musicService;
    private Intent musicSrvIntent;
    private boolean isBoundToService = false;
    private SongDTO nowPlaying;

    @Inject
    MusicLibraryManager musicLibraryManager;

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
        album = (TextView) findViewById(R.id.song_album);
        browseSongs = (ImageButton) findViewById(R.id.browse);
        playPauseBtn = (ImageButton) findViewById(R.id.play_pause_btn);
        skipNextBtn = (ImageButton) findViewById(R.id.next_btn);
        skipPrevBtn = (ImageButton) findViewById(R.id.prev_btn);
        nowPlayingPlaylistBtn = (ImageButton) findViewById(R.id.playlist);
        thumbnail = (ImageView) findViewById(R.id.thumbnail);
        dedicateBtn = (Button) findViewById(R.id.dedicate);
        shareBtn = (Button) findViewById(R.id.share);
        recommendBtn = (Button) findViewById(R.id.recommend);
        ratingBar = (RatingBar) findViewById(R.id.rating);

        dedicateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSongShareIntent(nowPlaying, SocialActivityType.DEDICATE);
            }
        });
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSongShareIntent(nowPlaying, SocialActivityType.SHARE);
            }
        });
        recommendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSongShareIntent(nowPlaying, SocialActivityType.RECOMMEND);
            }
        });

        /*ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                ratingBar.setRating(v);
                nowPlaying.setRating((double) v);
                musicLibraryManager.saveSongDetailsToCache(nowPlaying);
                EventBus.getDefault().post(new SyncTracksEvent());
            }
        });*/

        songTitle.setSelected(true);

        toolbar = (Toolbar) findViewById(R.id.media_player_toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Music Player");

        EventBus.getDefault().register(this);
        ((TheSocialPlaylistApplication) getApplication()).getMusicLibraryManagerComponent().inject(this);
    }

    private ServiceConnection musicserviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MediaPlayerServiceBinder serviceBinder = (MusicService.MediaPlayerServiceBinder) service;
            musicService = serviceBinder.getMusicService();
            songsList = musicService.getNowPlayingList();
            populateSongAttributes(musicService.getCurrentPlayingPosition());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBoundToService = false;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if(musicService != null)
            populateSongAttributes(musicService.getCurrentPlayingPosition());
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //mediaPlayer.release();
        unbindService(musicserviceConnection);
        EventBus.getDefault().unregister(this);
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
                if (musicService.isPlaying())
                    musicService.pause();
                else
                    musicService.play();
            }
        });
        skipNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    musicService.playNextSong();
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
        /*Intent intent = new Intent(Intent.ACTION_SEARCH);
        intent.setPackage("com.google.android.youtube");
        intent.putExtra("query", nowPlaying.getMetadata().getTitle() + " " + nowPlaying.getMetadata().getArtist());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);*/
    }

    private void populateSongAttributes(int position) {
        if(songsList == null) {
            songTitle.setText("Select a SongDTO!");
            artist.setText("");
            album.setText("");
            return;
        }

        nowPlaying = musicLibraryManager.getSongDetails(songsList.get(position).getMetadata().getId());
        songTitle.setText(nowPlaying.getMetadata().getTitle());
        artist.setText(nowPlaying.getMetadata().getArtist());
        album.setText(nowPlaying.getMetadata().getAlbum());
        ratingBar.setRating(nowPlaying.getRating() == null ? (float) 0.0 : nowPlaying.getRating().floatValue());

        Log.i("MediaPlayer", "Hits: " + nowPlaying.getHits());
        Log.i("MediaPlayer", "Song Id: " + nowPlaying.getMetadata().getId());
        AppUtil.loadAlbumArt(getApplicationContext(), nowPlaying.getMetadata().getAlbumId(), thumbnail);

        setPlayPauseButton();
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

    private void sendSongShareIntent(SongDTO songDTO, SocialActivityType activityType) {
        Intent shareIntent = new Intent(MediaPlayerActivity.this, SongShareActivity.class);
        shareIntent.putExtra("SONG_TO_SHARE", songDTO);
        shareIntent.putExtra("ACTIVITY_TYPE", activityType);
        startActivity(shareIntent);
    }
}