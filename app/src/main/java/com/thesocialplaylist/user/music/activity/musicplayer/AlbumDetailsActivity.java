package com.thesocialplaylist.user.music.activity.musicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.thesocialplaylist.user.music.R;
import com.thesocialplaylist.user.music.TheSocialPlaylistApplication;
import com.thesocialplaylist.user.music.adapters.recyclerview.MusicLibraryTracksListAdapter;
import com.thesocialplaylist.user.music.dto.AlbumDTO;
import com.thesocialplaylist.user.music.dto.SongDTO;
import com.thesocialplaylist.user.music.enums.TracksListMode;
import com.thesocialplaylist.user.music.fragment.musicplayer.musiclibrary.TracksListFragment;
import com.thesocialplaylist.user.music.manager.MusicLibraryManager;
import com.thesocialplaylist.user.music.service.MusicService;
import com.thesocialplaylist.user.music.utils.ImageUtil;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by utk on 23-05-2016.
 */
public class AlbumDetailsActivity extends AppCompatActivity
        implements MusicLibraryTracksListAdapter.OnTrackItemClickListener, MusicLibraryTracksListAdapter.OnOptionsButtonClickListener {

    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private AlbumDTO albumDTO;
    private ImageView albumArt;

    private MusicService musicService;
    private Boolean isBoundToService;
    private Intent musicServiceIntent;

    @Inject
    MusicLibraryManager musicLibraryManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details);
        ((TheSocialPlaylistApplication) getApplication()).getMusicLibraryManagerComponent().inject(this);
        initialize();
    }

    private ServiceConnection musicServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MediaPlayerServiceBinder musicServiceBinder = (MusicService.MediaPlayerServiceBinder) service;
            musicService = musicServiceBinder.getMusicService();
            isBoundToService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBoundToService = false;
        }
    };

    private void initialize() {
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_album_art);
        albumDTO = (AlbumDTO) getIntent().getSerializableExtra("ALBUM_SELECTED");
        albumArt = (ImageView) findViewById(R.id.album_art);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle(albumDTO.getAlbumName());
        prepareTracksFragment();
        ImageUtil.loadAlbumArt(getApplicationContext(), albumDTO.getAlbumId(), albumArt);
        Toast.makeText(AlbumDetailsActivity.this, "AlbumDTO: " + albumDTO.getAlbumName() , Toast.LENGTH_SHORT).show();
    }

    private void prepareTracksFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.tracks_fragment,
                TracksListFragment.newInstance(musicLibraryManager.getAllSongs(MediaStore.Audio.Media.ALBUM_ID + " = ?",
                        new String[]{"" + albumDTO.getAlbumId()}), TracksListMode.MUSIC_PLAYER_MODE),
                "ALBUM_SONGS");
        fragmentTransaction.commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        musicServiceIntent = new Intent(this, MusicService.class);
        startService(musicServiceIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        bindService(musicServiceIntent, musicServiceConn, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause() {
        super.onPause();
        unbindService(musicServiceConn);
    }

    @Override
    public void onOptionsButtonClick(View v, int position, List<SongDTO> tracksList) {

    }

    @Override
    public void onTrackItemClick(View v, int position, List<SongDTO> tracksList) {
        musicService.setCurrentPlayingPosition(position);
        try {
            musicService.playSong(tracksList, position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
