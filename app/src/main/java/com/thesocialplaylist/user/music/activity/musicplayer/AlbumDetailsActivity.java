package com.thesocialplaylist.user.music.activity.musicplayer;

import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.thesocialplaylist.user.music.R;
import com.thesocialplaylist.user.music.TheSocialPlaylistApplication;
import com.thesocialplaylist.user.music.dto.AlbumDTO;
import com.thesocialplaylist.user.music.enums.TracksListMode;
import com.thesocialplaylist.user.music.fragment.musicplayer.musiclibrary.TracksListFragment;
import com.thesocialplaylist.user.music.manager.MusicLibraryManager;
import com.thesocialplaylist.user.music.utils.AppUtil;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import javax.inject.Inject;

/**
 * Created by utk on 23-05-2016.
 */
public class AlbumDetailsActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private AlbumDTO albumDTO;
    private ImageView albumArt;

    @Inject
    MusicLibraryManager musicLibraryManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details);
        ((TheSocialPlaylistApplication) getApplication()).getMusicLibraryManagerComponent().inject(this);
        initialize();
    }

    private void initialize() {
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_album_art);
        albumDTO = (AlbumDTO) getIntent().getSerializableExtra("ALBUM_SELECTED");
        albumArt = (ImageView) findViewById(R.id.album_art);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle(albumDTO.getAlbumName());
        prepareTracksFragment();
        AppUtil.loadAlbumArt(getApplicationContext(), albumDTO.getAlbumId(), albumArt);
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
}
