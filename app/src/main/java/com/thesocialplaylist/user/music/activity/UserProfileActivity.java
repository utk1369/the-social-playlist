package com.thesocialplaylist.user.music.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.thesocialplaylist.user.music.R;
import com.thesocialplaylist.user.music.TheSocialPlaylistApplication;
import com.thesocialplaylist.user.music.ViewPagerAdapter;
import com.thesocialplaylist.user.music.api.declaration.UserApi;
import com.thesocialplaylist.user.music.dto.PopulateDTO;
import com.thesocialplaylist.user.music.dto.UserDTO;
import com.thesocialplaylist.user.music.dto.UserProfileRequestDTO;
import com.thesocialplaylist.user.music.enums.TracksListMode;
import com.thesocialplaylist.user.music.fragment.FriendsListFragment;
import com.thesocialplaylist.user.music.fragment.musicplayer.musiclibrary.TracksListFragment;
import com.thesocialplaylist.user.music.manager.UserDataAndRelationsManager;

import java.util.Arrays;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TextView userName;
    private TextView userStatus;
    private CircularImageView userImg;

    private UserDTO userDetails;

    public void setUserDetails(UserDTO userDetails) {
        this.userDetails = userDetails;
    }

    @Inject
    UserDataAndRelationsManager userDataAndRelationsManager;

    private UserApi userApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        init();
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    private void init() {

        ((TheSocialPlaylistApplication) getApplication()).getUserDataAndRelationsManagerComponent().inject(this);
        userApi = userDataAndRelationsManager.getUserApi();
        actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");

        userName = (TextView) findViewById(R.id.user_name);
        userStatus = (TextView) findViewById(R.id.user_status);
        userImg = (CircularImageView) findViewById(R.id.user_img);

        String userId = getIntent().getStringExtra("USER_ID");
        getUserProfile(userId);
    }

    private void getUserProfile(String userId) {
        final ProgressDialog loading = ProgressDialog.show(UserProfileActivity.this, "Please wait", "Fetching User Profile");

        UserProfileRequestDTO userProfileRequestDTO = new UserProfileRequestDTO();
        userProfileRequestDTO.setPopulate(Arrays.asList(new PopulateDTO("friends.friend", "name fbId imageUrl status")));
        Call<UserDTO> profileCall = userApi.getProfile(userId, userProfileRequestDTO);
        profileCall.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                loading.dismiss();
                UserDTO userProfile = response.body();
                if(userProfile == null) {
                    Log.e("USER_PROFILE_ACTIVITY", "Unable to fetch User Profile");
                } else {
                    loading.dismiss();
                    Log.i("USER_PROFILE_ACTIVITY", "Fetched User profile. UserId: " + userProfile.getId());
                    setUserDetails(userProfile);
                    refreshScreen();
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                Log.e("USER_PROFILE_ACTIVITY", "Call to fetch User Profile failed");
            }
        });
    }

    private void refreshScreen() {
        userName.setText(userDetails.getName());
        userStatus.setText(userDetails.getStatus());
        Picasso.with(getApplicationContext())
                .load(userDetails.getImageUrl())
                .fit()
                .placeholder(R.drawable.ic_person_black_48dp)
                .error(R.drawable.ic_person_black_48dp)
                .into(userImg);

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(getViewPagerAdapter());

        tabLayout =(TabLayout) findViewById(R.id.profile_tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    private ViewPagerAdapter getViewPagerAdapter() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), UserProfileActivity.this);
        viewPagerAdapter.addFragment(FriendsListFragment.newInstance(userDetails.getFriends(), LinearLayoutManager.VERTICAL), "Friends");
        viewPagerAdapter.addFragment(TracksListFragment.newInstance(userDetails.getSongs(), TracksListMode.USER_PROFILE_MODE), "Songs");

        //viewPagerAdapter.addFragment(FriendsListFragment.newInstance(new ArrayList<FriendDTO>()), "Activities");
        return viewPagerAdapter;
    }

}
