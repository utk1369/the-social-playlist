package com.example.user.music.activity.musicplayer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.user.music.R;
import com.example.user.music.enums.TracksListMode;
import com.example.user.music.fragment.musicplayer.musiclibrary.AllTracks;
import com.example.user.music.models.Album;
import com.example.user.music.utils.AlbumArtAsyncLoader;
import com.example.user.music.utils.AppUtil;
import com.example.user.music.utils.AsyncTaskResponseHandler;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

/**
 * Created by utk on 23-05-2016.
 */
public class AlbumDetailsActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private Album album;
    private ImageView albumArt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details);
        initialize();

    }

    private void initialize() {
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_album_art);
        album = (Album) getIntent().getSerializableExtra("ALBUM_SELECTED");
        albumArt = (ImageView) findViewById(R.id.album_art);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle(album.getAlbumName());
        prepareTracksFragment();
        Toast.makeText(AlbumDetailsActivity.this, "Album: " + album.getAlbumName() , Toast.LENGTH_SHORT).show();
    }

    private void prepareTracksFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.tracks_fragment,
                AllTracks.newInstance(AppUtil.getSongsAsList(getApplicationContext(),
                        MediaStore.Audio.Media.ALBUM_ID + " = ?", new String[] {"" + album.getAlbumId()}), TracksListMode.MUSIC_PLAYER_MODE),
                "ALBUM_SONGS");
        fragmentTransaction.commit();

    }

    @Override
    public void onWindowFocusChanged(boolean focus) {
        super.onWindowFocusChanged(focus);
        int imageViewHt = albumArt.getHeight(); int imageViewWd = albumArt.getWidth();
        Toast.makeText(AlbumDetailsActivity.this, "Ht: " + imageViewHt + "Wd: " + imageViewWd, Toast.LENGTH_SHORT).show();

        AlbumArtAsyncLoader albumArtAsyncLoader = new AlbumArtAsyncLoader();
        albumArtAsyncLoader.setAsyncTaskResponseHandler(new AsyncTaskResponseHandler() {
            @Override
            public void onResponseFromAsyncTask(Object[] params) {
                Log.i("Bitmap", params[0].toString());
                albumArt.setImageBitmap((Bitmap) params[0]);
            }
        });
        albumArtAsyncLoader.execute(new Object[]{getApplicationContext(), album.getAlbumId(), imageViewHt, imageViewWd});
    }

}
