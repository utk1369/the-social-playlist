package com.thesocialplaylist.user.music.activity;


import android.app.ProgressDialog;
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

import com.google.api.client.http.HttpStatusCodes;
import com.thesocialplaylist.user.music.R;
import com.thesocialplaylist.user.music.TheSocialPlaylistApplication;
import com.thesocialplaylist.user.music.ViewPagerAdapter;
import com.thesocialplaylist.user.music.activity.musicplayer.MediaPlayerActivity;
import com.thesocialplaylist.user.music.api.UserApi;
import com.thesocialplaylist.user.music.dto.UserDTO;
import com.thesocialplaylist.user.music.dto.UserSearchDTO;
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

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ActionBar actionBar;
    private ImageButton mediaPlayerBtn;

    private UserDTO userDTO;

    public UserDTO getUserDTO() {
        return userDTO;
    }

    public void setUserDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
    }

    @Inject
    Retrofit retrofit;

    private UserApi userApiService;

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

        ((TheSocialPlaylistApplication) getApplication()).getRetrofitComponent().inject(this);

        userApiService = retrofit.create(UserApi.class);
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
        login(intent.getStringExtra("FB_USER_ID"), intent.getStringExtra("FB_USER_NAME"));
        //loginOrRegister(intent.getStringExtra("FB_USER_ID"), intent.getStringExtra("FB_USER_NAME"));
    }

    private void login(final String fbUserId, final String name) {

        final ProgressDialog loading = ProgressDialog.show(MainActivity.this, "Attempting Login.", "Please wait...");
        final UserSearchDTO searchDTO = new UserSearchDTO();
        final UserDTO userDTO = new UserDTO();
        userDTO.setFbId(fbUserId);
        searchDTO.setCriteria(userDTO);
        Call<List<UserDTO>> searchCall = userApiService.searchUser(searchDTO);
        searchCall.enqueue(new Callback<List<UserDTO>>() {
            @Override
            public void onResponse(Call<List<UserDTO>> call, Response<List<UserDTO>> response) {
                loading.dismiss();
                if(response.isSuccessful() && response.body().size() > 0) {
                    List<UserDTO> userList = response.body();
                    setUserDTO(userList.get(0));
                    Log.i("LOGIN", "User found: " + userDTO.getName());
                } else if(response.body().size() == 0 || response.code() == HttpStatusCodes.STATUS_CODE_NOT_FOUND) {
                    Log.i("LOGIN", "User not found! [" +response.errorBody()  + "]");
                    UserDTO newUser = new UserDTO();
                    newUser.setFbId(fbUserId);
                    newUser.setName(name);
                    register(newUser);
                } else {
                    Log.e("LOGIN", "Some error!");
                }
            }
            @Override
            public void onFailure(Call<List<UserDTO>> call, Throwable t) {
                loading.dismiss();
                Log.e("Search call failed for ", searchDTO.getCriteria().getFbId() + "[" + t.getMessage() + "]");
            }
        });
    }

    private void register(final UserDTO userDTO) {
        final ProgressDialog progressInd = ProgressDialog.show(MainActivity.this, "Registering User.", "Please wait...");
        Call<UserDTO> registerCall = userApiService.registerUser(userDTO);
        registerCall.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                progressInd.dismiss();
                if(response.isSuccessful() && response.body().getFbId().equals(userDTO.getFbId())) {
                    Toast.makeText(MainActivity.this, "Welcome to The Social Playlist!", Toast.LENGTH_SHORT).show();
                    setUserDTO(response.body());
                } else {
                    Toast.makeText(MainActivity.this, "Registration failed. Please restart your app", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                progressInd.dismiss();
                Toast.makeText(MainActivity.this, "Registration failed due to some technical error. Please restart your app", Toast.LENGTH_LONG).show();
            }
        });
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
}
