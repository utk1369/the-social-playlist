package com.example.user.music.activity.musicplayer;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;

import com.example.user.music.enums.TracksListMode;
import com.example.user.music.fragment.musicplayer.musiclibrary.AllAlbums;
import com.example.user.music.fragment.musicplayer.musiclibrary.AllArtists;
import com.example.user.music.fragment.musicplayer.musiclibrary.AllTracks;
import com.example.user.music.R;
import com.example.user.music.ViewPagerAdapter;
import com.example.user.music.models.Song;
import com.example.user.music.utils.AppUtil;

import java.util.List;

public class MusicLibraryActivity extends AppCompatActivity {

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private TabLayout mTabLayout;
    private List<Song> songs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_library);
        initialize();

        // Set up the ViewPager with the sections adapter.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void initialize() {
        songs = AppUtil.getSongsAsList(getApplicationContext(), null, null);
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
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(AllTracks.newInstance(AppUtil.getSongsAsList(getApplicationContext(), null, null), TracksListMode.MUSIC_PLAYER_MODE), "Songs");
        viewPagerAdapter.addFragment(new AllAlbums(), "Albums");
        viewPagerAdapter.addFragment(new AllArtists(), "Artists");
        return viewPagerAdapter;
    }
}

