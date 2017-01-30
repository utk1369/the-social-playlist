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
import com.thesocialplaylist.user.music.api.declaration.UserApi;
import com.thesocialplaylist.user.music.dto.UserDTO;
import com.thesocialplaylist.user.music.dto.UserSearchDTO;
import com.thesocialplaylist.user.music.fragment.FriendsList;
import com.thesocialplaylist.user.music.manager.UserDataAndRelationsManager;
import com.thesocialplaylist.user.music.sqlitedbcache.model.UserRelCache;

import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ActionBar actionBar;
    private ImageButton mediaPlayerBtn;

    @Inject
    public UserDataAndRelationsManager userDataAndRelationsManager;

    private UserApi userApi;

    private UserDTO userDTO;

    public UserDTO getUserDTO() {
        return userDTO;
    }

    public void setUserDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
    }

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

        ((TheSocialPlaylistApplication) getApplication()).getUserDataAndRelationsManagerComponent().inject(this);

        userApi = userDataAndRelationsManager.getUserApi();
    }

    private ViewPagerAdapter getViewPagerAdapter() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        //viewPagerAdapter.addFragment(new MediaPlayerFragment(), "Feed");
        viewPagerAdapter.addFragment(FriendsList.newInstance((List<UserDTO>) getIntent().getSerializableExtra("FRIENDS_LIST")), "Friends");
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
    }

    private void updateUserProfile(UserDTO userDTO) {
        //update User Details
        userDataAndRelationsManager.saveUserDetailsToCache(userDTO, UserRelCache.RELATIONSHIP_TYPES.APP_USER);
        //Update Friends Details
        if(userDTO.getFriends() != null && userDTO.getFriends().size() > 0) {
            for(UserDTO friend: userDTO.getFriends())
                userDataAndRelationsManager.saveUserDetailsToCache(friend, UserRelCache.RELATIONSHIP_TYPES.FRIEND);
        }
        updateFriendsList((List<UserDTO>) getIntent().getSerializableExtra("FRIENDS_LIST"));
    }

    private void updateFriendsList(List<UserDTO> newFriendsList) {
        //compare the friend list in the local db with friendsList from FB and update the User details in the service accordingly
        List<UserDTO> existingFriends = userDataAndRelationsManager.getAllFriendsData();
        //for()

    }

    private void login(final String fbUserId, final String name) {

        final ProgressDialog loading = ProgressDialog.show(MainActivity.this, "Please wait", "Refreshing your screen");
        final UserSearchDTO searchDTO = new UserSearchDTO();
        final UserDTO userSearchCriteria = new UserDTO();
        userSearchCriteria.setFbId(fbUserId);
        searchDTO.setCriteria(userSearchCriteria);
        Call<List<UserDTO>> searchCall = userApi.searchUser(searchDTO);
        searchCall.enqueue(new Callback<List<UserDTO>>() {
            @Override
            public void onResponse(Call<List<UserDTO>> call, Response<List<UserDTO>> response) {
                loading.dismiss();
                if(response.isSuccessful() && response.body().size() > 0) {
                    List<UserDTO> userList = response.body();
                    setUserDTO(userList.get(0));
                    Log.i("LOGIN", "User found: " + getUserDTO().getId());
                    updateUserProfile(userDTO);
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
        Call<UserDTO> registerCall = userApi.registerUser(userDTO);
        registerCall.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                progressInd.dismiss();
                if(response.isSuccessful() && response.body() != null && response.body().getFbId().equals(userDTO.getFbId())) {
                    Toast.makeText(MainActivity.this, "Welcome to The Social Playlist!", Toast.LENGTH_SHORT).show();
                    setUserDTO(response.body());
                    updateUserProfile(getUserDTO());
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
}
