package com.thesocialplaylist.user.music.activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.mikhaellopez.circularimageview.CircularImageView;
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
import com.thesocialplaylist.user.music.utils.AppUtil;

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
    private CircularImageView userProfileBtn;

    private FriendsListFragment friendsListFragment;

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
        /*Toast.makeText(this, "Friends Size: " + (getUserDTO().getFriends() == null ?
                "null": getUserDTO().getFriends().size() + ""), Toast.LENGTH_SHORT).show();*/
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

        userProfileBtn = (CircularImageView) findViewById(R.id.user_profile_btn);
        userProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent userProfileIntent = new Intent(MainActivity.this, UserProfileActivity.class);
                userProfileIntent.putExtra("USER_ID", getUserDTO().getId());
                startActivity(userProfileIntent);
            }
        });
        friendsListFragment = FriendsListFragment.newInstance(getUserDTO().getFriends(), LinearLayoutManager.VERTICAL);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(getViewPagerAdapter());

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        setScreenDetails();
        login((UserDTO) getIntent().getSerializableExtra("USER_FB_DETAILS"));
    }

    private ViewPagerAdapter getViewPagerAdapter() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), MainActivity.this);
        //viewPagerAdapter.addFragment(new MediaPlayerFragment(), "Feed");
        viewPagerAdapter.addFragment(friendsListFragment, "Friends");
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
        List<FriendDTO> friendsList = userDataAndRelationsManager.getAllFriendsDataFromCacheAsList();
        userDTO.setFriends(friendsList);

        return userDTO;
    }

    private void updateAndRefreshUserDetails(UserDTO updatedUserDetails) {
        updateUserProfileInCache(updatedUserDetails);
        UserDTO userDetails = getUserDetailsFromCache();
        setUserDTO(userDetails);
        setScreenDetails();
        FriendsListFragment frag = (FriendsListFragment) viewPager.getAdapter().instantiateItem(viewPager, 0);
        friendsListFragment.updateDataSet(userDetails.getFriends());
    }

    private void login(final UserDTO userDetails) {
        final ProgressDialog loading = ProgressDialog.show(MainActivity.this, "Please wait", "Refreshing your screen");
        UserLoginRequestDTO userLoginRequestDTO = new UserLoginRequestDTO(userDetails, Arrays.asList(new PopulateDTO("friends.friend", "name fbId imageUrl status")));
        Call<UserDTO> loginCall = userApi.login(true, userLoginRequestDTO);
        loginCall.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                loading.dismiss();
                if(response.isSuccessful() && response.body() != null) {
                    updateAndRefreshUserDetails(response.body());
                    Log.i("LOGIN", "User found: " + getUserDTO().getId());
                    Toast.makeText(MainActivity.this, "User found: " + getUserDTO().getId(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("LOGIN", "Some error! [" + response.errorBody().toString()+ "]");
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
}
