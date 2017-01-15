package com.thesocialplaylist.user.music.activity;


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

import com.thesocialplaylist.user.music.R;
import com.thesocialplaylist.user.music.ViewPagerAdapter;
import com.thesocialplaylist.user.music.activity.musicplayer.MediaPlayerActivity;
import com.thesocialplaylist.user.music.dto.UserDTO;
import com.thesocialplaylist.user.music.fragment.FriendsList;
import com.thesocialplaylist.user.music.dto.AppHttpResponse;
import com.thesocialplaylist.user.music.dto.HttpResponseHandler;
import com.thesocialplaylist.user.music.utils.AppUtil;
import com.thesocialplaylist.user.music.manager.HttpTaskManager;

import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ActionBar actionBar;
    private ImageButton mediaPlayerBtn;

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
        //viewPagerAdapter.addFragment(new MediaPlayerFragment(), "Feed");
        viewPagerAdapter.addFragment(FriendsList.newInstance(getFriendsList(getIntent().getStringExtra("FRIENDS_LIST"))), "Friends");
        //viewPagerAdapter.addFragment(new MediaPlayerFragment(), "Notifications");
        return viewPagerAdapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        Intent intent = getIntent();
        loginOrRegister(intent.getStringExtra("FB_USER_ID"), intent.getStringExtra("FB_USER_NAME"));
    }

    private List<UserDTO> getFriendsList(String friendsList) {
        JSONArray friendsListJsonArray = null;
        try {
            friendsListJsonArray = new JSONArray(friendsList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("Friends List: ", friendsListJsonArray.toString());
        List<UserDTO> friends = new ArrayList<>();
        for(int i=0; i<friendsListJsonArray.length(); i++) {
            try {
                JSONObject friend = friendsListJsonArray.getJSONObject(i);
                UserDTO usr = new UserDTO();
                usr.setName(friend.getString("name"));
                friends.add(usr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return friends;
    }

    private void loginOrRegister(final String fbUserId, final String fbUserName) {
        String searchUrl = null;
        try {
            searchUrl = AppUtil.getProperty("api.base.url", getApplicationContext()) + "users/search";
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpTaskManager httpTaskManager = new HttpTaskManager(MainActivity.this);
        httpTaskManager.setHttpResponseHandler(new HttpResponseHandler() {
            @Override
            public void handleHttpResponse(Object result) {
                AppHttpResponse response = (AppHttpResponse) result;
                if(response != null && response.getData() != null) {
                    JSONArray responseData = null;
                    try {
                        responseData = new JSONArray(response.getData().toString());
                        if(responseData.length() == 0) {
                            registerUser(fbUserId, fbUserName);
                        } else {
                            JSONObject userData = (JSONObject) responseData.get(0);
                            Log.i("User Data", userData.toString());
                            Toast.makeText(MainActivity.this, "Welcome Back Buddy!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        JSONObject searchCriteria = new JSONObject();
        JSONObject searchPayload =  new JSONObject();;
        try {
            searchCriteria.put("fbId", fbUserId);
            searchPayload.put("criteria", searchCriteria);
            Log.i("Payload", searchPayload.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpTaskManager.execute(searchUrl, searchPayload.toString());
    }

    private void registerUser(String fbUserId, String fbUserName) {
        String registrationUrl = null;
        try {
            registrationUrl = AppUtil.getProperty("api.base.url", getApplicationContext()) + "users/create";
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpTaskManager httpTaskManager = new HttpTaskManager(MainActivity.this);
        httpTaskManager.setHttpResponseHandler(new HttpResponseHandler() {
            @Override
            public void handleHttpResponse(Object result) {
                AppHttpResponse response = (AppHttpResponse) result;
                if(response != null && response.getData() != null) {
                    JSONArray responseData = null;
                    try {
                        JSONArray userDataArr = new JSONArray(response.getData().toString());
                        JSONObject userData = userDataArr.getJSONObject(0);
                        Toast.makeText(MainActivity.this, "Welcome Buddy!", Toast.LENGTH_SHORT).show();
                        Log.i("New User Created", userData.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        JSONObject searchPayload =  new JSONObject();;
        try {
            searchPayload.put("fbId", fbUserId);
            searchPayload.put("name", fbUserName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpTaskManager.execute(registrationUrl, searchPayload.toString());
    }
}
