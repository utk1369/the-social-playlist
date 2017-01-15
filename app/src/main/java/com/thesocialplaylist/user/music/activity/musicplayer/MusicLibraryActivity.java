package com.thesocialplaylist.user.music.activity.musicplayer;

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
import com.thesocialplaylist.user.music.dto.SongDTO;
import com.thesocialplaylist.user.music.enums.TracksListMode;
import com.thesocialplaylist.user.music.fragment.musicplayer.musiclibrary.AlbumsGridFragment;
import com.thesocialplaylist.user.music.fragment.musicplayer.musiclibrary.ArtistsListFragment;
import com.thesocialplaylist.user.music.fragment.musicplayer.musiclibrary.TracksListFragment;
import com.thesocialplaylist.user.music.R;
import com.thesocialplaylist.user.music.ViewPagerAdapter;
import com.thesocialplaylist.user.music.manager.MusicLibraryManager;

import java.util.List;

import javax.inject.Inject;

public class MusicLibraryActivity extends AppCompatActivity {

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private TabLayout mTabLayout;
    private List<SongDTO> songDTOs;

    @Inject
    MusicLibraryManager musicLibraryManager;

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
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(TracksListFragment.newInstance(
                musicLibraryManager.getAllSongs(), TracksListMode.MUSIC_PLAYER_MODE), "Songs");
        viewPagerAdapter.addFragment(new AlbumsGridFragment(), "Albums");
        viewPagerAdapter.addFragment(new ArtistsListFragment(), "Artists");
        return viewPagerAdapter;
    }
}

