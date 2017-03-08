package com.thesocialplaylist.user.music.activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.squareup.picasso.Picasso;
import com.thesocialplaylist.user.music.R;
import com.thesocialplaylist.user.music.TheSocialPlaylistApplication;
import com.thesocialplaylist.user.music.ViewPagerAdapter;
import com.thesocialplaylist.user.music.activity.musicplayer.MediaPlayerActivity;
import com.thesocialplaylist.user.music.api.declaration.UserApi;
import com.thesocialplaylist.user.music.dto.FriendDTO;
import com.thesocialplaylist.user.music.dto.PopulateDTO;
import com.thesocialplaylist.user.music.dto.UserDTO;
import com.thesocialplaylist.user.music.dto.UserLoginRequestDTO;
import com.thesocialplaylist.user.music.fragment.FriendsListFragment;
import com.thesocialplaylist.user.music.manager.UserDataAndRelationsManager;
import com.thesocialplaylist.user.music.sqlitedbcache.model.UserRelCache;

import java.util.Arrays;
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
    private ImageButton userProfileBtn;

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

    private void setScreenDetails() {
        UserDTO userDetails = getUserDTO();
        actionBar.setTitle("The Social Playlist");
        Picasso.with(getApplicationContext())
                .load(userDetails.getImageUrl())
                .fit()
                .placeholder(R.drawable.ic_person_black_48dp)
                .error(R.drawable.ic_person_black_48dp)
                .into(userProfileBtn);
    }

    private void init() {

        ((TheSocialPlaylistApplication) getApplication()).getUserDataAndRelationsManagerComponent().inject(this);
        userApi = userDataAndRelationsManager.getUserApi();

        //load the cached User Data in the beginning.
        setUserDTO(getUserDetailsFromCache());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        mediaPlayerBtn = (ImageButton) findViewById(R.id.media_player_btn);
        mediaPlayerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MediaPlayerActivity.class);
                startActivity(intent);
            }
        });

        userProfileBtn = (ImageButton) findViewById(R.id.user_profile_btn);
        userProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent userProfileIntent = new Intent(MainActivity.this, UserProfileActivity.class);
                userProfileIntent.putExtra("USER_ID", getUserDTO().getId());
                startActivity(userProfileIntent);
            }
        });

        setScreenDetails();

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(getViewPagerAdapter());

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        login((UserDTO) getIntent().getSerializableExtra("USER_FB_DETAILS"));
    }

    private ViewPagerAdapter getViewPagerAdapter() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), MainActivity.this);
        //viewPagerAdapter.addFragment(new MediaPlayerFragment(), "Feed");
        viewPagerAdapter.addFragment(FriendsListFragment.newInstance(getUserDTO().getFriends()), "Friends");
        //viewPagerAdapter.addFragment(new MediaPlayerFragment(), "Notifications");
        return viewPagerAdapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void updateUserProfileInCache(UserDTO updatedUserDTO) {
        //update User Details
        userDataAndRelationsManager.saveUserDetailsToCache(updatedUserDTO, UserRelCache.RELATIONSHIP_TYPES.APP_USER);
        //Update Friends Details
        if(updatedUserDTO.getFriends() != null && updatedUserDTO.getFriends().size() > 0) {
            for(FriendDTO friendsDTO: updatedUserDTO.getFriends())
                userDataAndRelationsManager.saveUserDetailsToCache(friendsDTO.getFriend(), UserRelCache.RELATIONSHIP_TYPES.FRIEND);
        }
    }

    private UserDTO getUserDetailsFromCache() {
        UserDTO userDTO = userDataAndRelationsManager.getAppUserDataFromCache();
        if(userDTO == null)
            return new UserDTO();
        List<FriendDTO> friendsList = userDataAndRelationsManager.getAllFriendsDataFromCache();
        userDTO.setFriends(friendsList);

        return userDTO;
    }

    private void updateAndRefreshUserDetails(UserDTO updatedUserDetails) {
        updateUserProfileInCache(updatedUserDetails);
        UserDTO userDetails = getUserDetailsFromCache();
        setUserDTO(userDetails);
        setScreenDetails();
    }

    private void login(final UserDTO userDetails) {
        //final ProgressDialog loading = ProgressDialog.show(MainActivity.this, "Please wait", "Refreshing your screen");
        Snackbar.make(drawerLayout, "Refreshing your screen...", Snackbar.LENGTH_LONG)
                .setAction("Dismiss", null).show();
        UserLoginRequestDTO userLoginRequestDTO = new UserLoginRequestDTO(userDetails, Arrays.asList(new PopulateDTO("friends.friend", "name fbId imageUrl status")));
        Call<UserDTO> loginCall = userApi.login(true, userLoginRequestDTO);
        loginCall.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if(response.isSuccessful() && response.body() != null) {
                    updateAndRefreshUserDetails(response.body());
                    Log.i("LOGIN", "User found: " + getUserDTO().getId());
                } else {
                    Log.e("LOGIN", "Some error! [" + response.errorBody().toString()+ "]");
                }
            }
            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                Log.e("Search call failed for ", userDetails.getFbId() + "[" + t.getMessage() + "]");
            }
        });
    }
}
