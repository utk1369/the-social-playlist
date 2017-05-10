package com.thesocialplaylist.user.music.activity.musicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;

import com.thesocialplaylist.user.music.TheSocialPlaylistApplication;
import com.thesocialplaylist.user.music.adapters.recyclerview.MusicLibraryTracksListAdapter;
import com.thesocialplaylist.user.music.dto.SongDTO;
import com.thesocialplaylist.user.music.enums.TracksListMode;
import com.thesocialplaylist.user.music.fragment.musicplayer.musiclibrary.AlbumsGridFragment;
import com.thesocialplaylist.user.music.fragment.musicplayer.musiclibrary.ArtistsListFragment;
import com.thesocialplaylist.user.music.fragment.musicplayer.musiclibrary.TracksListFragment;
import com.thesocialplaylist.user.music.R;
import com.thesocialplaylist.user.music.ViewPagerAdapter;
import com.thesocialplaylist.user.music.manager.MusicLibraryManager;
import com.thesocialplaylist.user.music.service.MusicService;

import java.util.List;

import javax.inject.Inject;

public class MusicLibraryActivity extends AppCompatActivity
        implements MusicLibraryTracksListAdapter.OnTrackItemClickListener, MusicLibraryTracksListAdapter.OnOptionsButtonClickListener {

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private TabLayout mTabLayout;
    private List<SongDTO> songDTOs;

    private MusicService musicService;
    private Boolean isBoundToService;
    private Intent musicServiceIntent;

    @Inject
    MusicLibraryManager musicLibraryManager;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_library);
        // Set up the ViewPager with the sections adapter.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ((TheSocialPlaylistApplication) getApplication()).getMusicLibraryManagerComponent().inject(this);
        initialize();
    }

    private void initialize() {
        songDTOs = musicLibraryManager.getAllSongs();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Music Library");

        mViewPager = (ViewPager) findViewById(R.id.library_view_pager);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(getViewPagerAdapter());

        mTabLayout = (TabLayout) findViewById(R.id.music_library_tabs);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private ViewPagerAdapter getViewPagerAdapter() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), MusicLibraryActivity.this);
        viewPagerAdapter.addFragment(TracksListFragment.newInstance(
                musicLibraryManager.getAllSongs(), TracksListMode.MUSIC_PLAYER_MODE), "Songs");
        viewPagerAdapter.addFragment(new AlbumsGridFragment(), "Albums");
        viewPagerAdapter.addFragment(new ArtistsListFragment(), "Artists");
        return viewPagerAdapter;
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

