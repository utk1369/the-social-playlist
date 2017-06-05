package com.thesocialplaylist.user.music.activity;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.thesocialplaylist.user.music.dto.SocialActivityDTO;
import com.thesocialplaylist.user.music.fragment.ActivitiesFragment;
import com.thesocialplaylist.user.music.fragment.ErrorTemplateFragment;
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
import com.thesocialplaylist.user.music.fragment.musicplayer.mediacontroller.MediaControllerFragment;
import com.thesocialplaylist.user.music.manager.MusicLibraryManager;
import com.thesocialplaylist.user.music.manager.UserDataAndRelationsManager;
import com.thesocialplaylist.user.music.sqlitedbcache.model.UserRelCache;
import com.thesocialplaylist.user.music.utils.AppUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    private final int PERMISSIONS_REQUEST_READ_STORAGE = 11;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ActionBar actionBar;
    private ImageButton mediaPlayerBtn;
    private CircularImageView userProfileBtn;

    private FrameLayout mediaControllerFragmentLayout;
    private Fragment mediaControllerFragment;

    private FriendsListFragment friendsListFragment;
    private ActivitiesFragment activitiesFragment;

    @Inject
    public UserDataAndRelationsManager userDataAndRelationsManager;

    private UserApi userApi;

    private MusicLibraryManager musicLibraryManager;

    private UserDTO userDTO;

    private List<SocialActivityDTO> userFeeds;

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
        musicLibraryManager = userDataAndRelationsManager.getMusicLibraryManager();

        //can be used to test the app when there is no user cache available
        //userDataAndRelationsManager.recreateTable();

        //load the cached User Data in the beginning.
        setUserDTO(getUserDetailsFromCache());
        userFeeds = new ArrayList<>();

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

        userProfileBtn = (CircularImageView) findViewById(R.id.user_profile_btn);
        userProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent userProfileIntent = new Intent(MainActivity.this, UserProfileActivity.class);
                userProfileIntent.putExtra("USER_ID", getUserDTO().getId());
                startActivity(userProfileIntent);
            }
        });

        mediaControllerFragmentLayout = (FrameLayout) findViewById(R.id.media_controls_fragment);
        mediaControllerFragmentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MediaPlayerActivity.class);
                startActivity(intent);
            }
        });

        try {
            mediaControllerFragment = MediaControllerFragment.newInstance(musicLibraryManager.getAllSongs());
            AppUtil.replaceFragments(getSupportFragmentManager(), R.id.media_controls_fragment, mediaControllerFragment);
        } catch(SecurityException e) {
            AppUtil.replaceFragments(getSupportFragmentManager(), R.id.media_controls_fragment,
                    ErrorTemplateFragment.newInstance(null, null));
        }

        friendsListFragment = FriendsListFragment.newInstance(
                getUserDTO().getFriends() == null ? new ArrayList<FriendDTO>() : getUserDTO().getFriends()
                , LinearLayoutManager.VERTICAL);

        activitiesFragment = ActivitiesFragment.newInstance(userFeeds);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(getViewPagerAdapter());

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        setScreenDetails();
        login((UserDTO) getIntent().getSerializableExtra("USER_FB_DETAILS"));
    }

    private void checkPermissions() {
        int isPermitted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if(isPermitted != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_READ_STORAGE);
        } else {
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    init();

                } else {
                    init();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

        }
    }

    private ViewPagerAdapter getViewPagerAdapter() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), MainActivity.this);
        //viewPagerAdapter.addFragment(new MediaPlayerFragment(), "Feed");
        viewPagerAdapter.addFragment(friendsListFragment, "Friends");
        viewPagerAdapter.addFragment(activitiesFragment, "Feed");
        return viewPagerAdapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Permission check
        checkPermissions();
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
        List<FriendDTO> friendsList = userDataAndRelationsManager.getAllFriendsDataFromCacheAsList();
        userDTO.setFriends(friendsList);

        return userDTO;
    }

    private void updateAndRefreshUserDetails(UserDTO updatedUserDetails) {
        updateUserProfileInCache(updatedUserDetails);
        UserDTO userDetails = getUserDetailsFromCache();
        setUserDTO(userDetails);
        setScreenDetails();
        friendsListFragment.updateDataSet(userDetails.getFriends());
    }

    private void updateUserFeeds(List<SocialActivityDTO> updatedFeeds) {
        activitiesFragment.updateDataSet(updatedFeeds);
    }

    private void login(final UserDTO userDetails) {
        final ProgressDialog loading = ProgressDialog.show(MainActivity.this, "Please wait", "Refreshing your screen");
        UserLoginRequestDTO userLoginRequestDTO = new UserLoginRequestDTO(userDetails, Arrays.asList(new PopulateDTO("friends.friend", "name fbId imageUrl status")));
        Call<UserDTO> loginCall = userApi.login(true, userLoginRequestDTO);
        Log.i("Login Request payload", new Gson().toJson(userLoginRequestDTO));
        loginCall.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                loading.dismiss();
                if(response.isSuccessful() && response.body() != null) {
                    updateAndRefreshUserDetails(response.body());
                    getFeeds(response.body().getId());
                    Log.i("LOGIN", "User found: " + getUserDTO().getId());
                    Toast.makeText(MainActivity.this, "User found: " + getUserDTO().getId(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("LOGIN", "Some error! [" + response.errorBody().charStream()+ "]");
                    Toast.makeText(MainActivity.this, "User retrieval unsuccessful.", Toast.LENGTH_SHORT).show();

                }
            }
            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(MainActivity.this, "Login call failed.", Toast.LENGTH_SHORT).show();
                Log.e("Login call failed for ", userDetails.getFbId() + "[" + t.getMessage() + "]");
            }
        });
    }

    private void getFeeds(String userId) {
        Call<List<SocialActivityDTO>> feedsCall = userApi.getFeeds(userId);
        feedsCall.enqueue(new Callback<List<SocialActivityDTO>>() {
            @Override
            public void onResponse(Call<List<SocialActivityDTO>> call, Response<List<SocialActivityDTO>> response) {
                List<SocialActivityDTO> feeds = response.body();
                if(feeds == null) {
                    Toast.makeText(MainActivity.this, "Unable to fetch User Feeds.", Toast.LENGTH_LONG).show();
                    Log.e("MAIN_ACTIVITY", "Unable to fetch User Feeds.");
                } else {
                    Log.i("MAIN_ACTIVITY", "Fetched User feeds. Size: " + feeds.size());
                    Toast.makeText(MainActivity.this, "User Feed updated.", Toast.LENGTH_LONG).show();
                    userFeeds = feeds;
                    updateUserFeeds(userFeeds);
                }
            }

            @Override
            public void onFailure(Call<List<SocialActivityDTO>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Unable to fetch User Feeds.", Toast.LENGTH_LONG).show();
                Log.e("MAIN_ACTIVITY", "Unable to fetch User Feeds.");
            }
        });
    }
}
