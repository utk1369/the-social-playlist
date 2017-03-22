package com.thesocialplaylist.user.music.activity.musicplayer.youtube;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.thesocialplaylist.user.music.R;
import com.thesocialplaylist.user.music.dto.ExternalLinksDTO;
import com.thesocialplaylist.user.music.fragment.musicplayer.youtube.YouTubePlayerWindowFragment;
import com.thesocialplaylist.user.music.fragment.musicplayer.youtube.YoutubeSearchFragment;
import com.thesocialplaylist.user.music.utils.AppUtil;

import java.io.IOException;

public class YoutubeLinker extends AppCompatActivity implements YoutubeSearchFragment.OnListItemClickListener {

    private String androidApiKey;
    private String browserApiKey;
    private String keyword;
    private String songId;
    private String linkedVideoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_linker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initialize();
        populateFragments();
    }

    private void initialize() {
        try {
            androidApiKey = AppUtil.getProperty("youtube.android.api.key", getApplicationContext());
            browserApiKey = AppUtil.getProperty("youtube.browser.api.key", getApplicationContext());
            keyword = getIntent().getStringExtra("KEYWORD");
            songId = getIntent().getStringExtra("SONG_ID");
            linkedVideoId = getIntent().getStringExtra(("LINKED_VIDEO_ID"));
            Log.i("YOUTUBE_LINK", "Linked id: " + linkedVideoId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void populatePlayerFragment(String videoId) {
        if(videoId == null)
            return;
        YouTubePlayerWindowFragment playerWindowFragment = YouTubePlayerWindowFragment
                .newInstance(videoId, androidApiKey);
        AppUtil.replaceFragments(getSupportFragmentManager(), R.id.player_window, playerWindowFragment);
    }

    private void populateSearchFragment(Long noOfResults) {
        YoutubeSearchFragment youtubeSearchFragment = YoutubeSearchFragment.newInstance(keyword, noOfResults, browserApiKey);
        AppUtil.replaceFragments(getSupportFragmentManager(), R.id.search_results, youtubeSearchFragment);
    }

    private void populateFragments() {
        populatePlayerFragment(linkedVideoId);
        populateSearchFragment(20L);
    }

    @Override
    public void onListItemSelected(ExternalLinksDTO selectedVideo) {
        populatePlayerFragment(selectedVideo.getId());
    }
}
