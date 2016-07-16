package com.example.user.music.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.user.music.MediaPlayerFragment;
import com.example.user.music.R;
import com.example.user.music.ViewPagerAdapter;
import com.example.user.music.activity.musicplayer.MediaPlayerActivity;
import com.example.user.music.fragment.FriendsList;
import com.example.user.music.models.AppHttpResponse;
import com.example.user.music.models.HttpResponseHandler;
import com.example.user.music.utils.NetworkUtil;

import android.widget.ImageButton;

import org.json.JSONArray;
import org.json.JSONException;


public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ActionBar actionBar;
    private ImageButton mediaPlayerBtn;
    private String accessToken;

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("The Social Playlist");

        mediaPlayerBtn = (ImageButton) findViewById(R.id.media_player_btn);
        mediaPlayerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MediaPlayerActivity.class);
                startActivity(intent);
            }
        });

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(getViewPagerAdapter());

        tabLayout =(TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    }

    private ViewPagerAdapter getViewPagerAdapter() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new MediaPlayerFragment(), "Player1");
        viewPagerAdapter.addFragment(FriendsList.newInstance(getIntent().getStringExtra("FRIENDS_LIST")), "Friends");
        viewPagerAdapter.addFragment(new MediaPlayerFragment(), "Player3");
        return viewPagerAdapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        Intent intent = getIntent();
        accessToken = intent.getStringExtra("accessToken");
        checkUserStatus(intent.getStringExtra("FB_USER_ID"));
        JSONArray friendsList = null;
        try {
            friendsList = new JSONArray(intent.getStringExtra("FRIENDS_LIST"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("Friends List: ", friendsList.toString());
    }

    private void checkUserStatus(String fbUserId) {
        NetworkUtil networkUtil = new NetworkUtil(MainActivity.this);
        networkUtil.setHttpResponseHandler(new HttpResponseHandler() {
            @Override
            public void handleHttpResponse(Object result) {
                AppHttpResponse response = (AppHttpResponse) result;
                Log.i("Response in Main", response.getData().toString());
            }
        });
        String url = "http://192.168.0.104:3000/users/search";
        networkUtil.execute(url);
    }
}
