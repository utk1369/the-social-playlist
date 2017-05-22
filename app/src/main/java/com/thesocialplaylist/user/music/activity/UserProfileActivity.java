package com.thesocialplaylist.user.music.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.thesocialplaylist.user.music.R;
import com.thesocialplaylist.user.music.TheSocialPlaylistApplication;
import com.thesocialplaylist.user.music.ViewPagerAdapter;
import com.thesocialplaylist.user.music.adapters.recyclerview.UserProfileTracksListViewAdapter;
import com.thesocialplaylist.user.music.api.declaration.UserApi;
import com.thesocialplaylist.user.music.dto.PopulateDTO;
import com.thesocialplaylist.user.music.dto.SocialActivityDTO;
import com.thesocialplaylist.user.music.dto.SongDTO;
import com.thesocialplaylist.user.music.dto.UserDTO;
import com.thesocialplaylist.user.music.dto.UserProfileRequestDTO;
import com.thesocialplaylist.user.music.enums.TracksListMode;
import com.thesocialplaylist.user.music.fragment.ActivitiesFragment;
import com.thesocialplaylist.user.music.fragment.FriendsListFragment;
import com.thesocialplaylist.user.music.fragment.musicplayer.musiclibrary.TracksListFragment;
import com.thesocialplaylist.user.music.manager.UserDataAndRelationsManager;
import com.thesocialplaylist.user.music.utils.AppUtil;
import com.thesocialplaylist.user.music.utils.CollectionUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileActivity extends AppCompatActivity
        implements UserProfileTracksListViewAdapter.OnLikeButtonClickListener, UserProfileTracksListViewAdapter.OnTrackInfoClickListener,
        UserProfileTracksListViewAdapter.OnLikeButtonLongClickListener {

    private ActionBar actionBar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TextView userName;
    private TextView userStatus;
    private CircularImageView userImg;

    private UserDTO userDetails;
    private List<SocialActivityDTO> userActivities;

    private ProgressDialog loading;

    private TracksListFragment tracksListFragment;
    private FriendsListFragment friendsListFragment;
    private ActivitiesFragment activitiesFragment;
    private UserDTO appUserDetails;

    public void setUserDetails(UserDTO userDetails) {
        this.userDetails = userDetails;
    }

    public void setUserActivities(List<SocialActivityDTO> userActivities) {
        this.userActivities = userActivities;
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
    }

    private void init() {

        ((TheSocialPlaylistApplication) getApplication()).getUserDataAndRelationsManagerComponent().inject(this);
        userApi = userDataAndRelationsManager.getUserApi();

        String userId = getIntent().getStringExtra("USER_ID");
        getUserProfile(userId);
        getUserActivities(userId);

        actionBar = getSupportActionBar();
        actionBar.setTitle("User Profile");
        appUserDetails = userDataAndRelationsManager.getAppUserDataFromCache();

        userName = (TextView) findViewById(R.id.user_name);
        userStatus = (TextView) findViewById(R.id.user_status);
        userImg = (CircularImageView) findViewById(R.id.user_img);

        loading = ProgressDialog.show(UserProfileActivity.this, "Please wait", "Fetching User Profile");
    }

    private void getUserActivities(final String userId) {
        SocialActivityDTO searchPayload = new SocialActivityDTO();
        searchPayload.setPostedBy(userId);
        Call<List<SocialActivityDTO>> activitiesFetchCall = userApi.searchActivities(searchPayload);
        activitiesFetchCall.enqueue(new Callback<List<SocialActivityDTO>>() {
            @Override
            public void onResponse(Call<List<SocialActivityDTO>> call, Response<List<SocialActivityDTO>> response) {
                List<SocialActivityDTO> activities = response.body();
                if(activities == null) {
                    loading.dismiss();
                    Toast.makeText(UserProfileActivity.this, "Unable to fetch User Activities.", Toast.LENGTH_LONG).show();
                    Log.e("USER_PROFILE_ACTIVITY", "Unable to fetch User Activities.");
                } else {
                    Log.i("USER_PROFILE_ACTIVITY", "Fetched User activities. Size: " + activities.size());
                    setUserActivities(activities);
                    if(userDetails != null)
                        refreshScreen();
                }
            }

            @Override
            public void onFailure(Call<List<SocialActivityDTO>> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(UserProfileActivity.this, "Unable to fetch User Activities.", Toast.LENGTH_LONG).show();
                Log.e("USER_PROFILE_ACTIVITY", "Unable to fetch User Activities.");
            }
        });
    }

    private void getUserProfile(String userId) {
        UserProfileRequestDTO userProfileRequestDTO = new UserProfileRequestDTO();
        userProfileRequestDTO.setPopulate(Arrays.asList(new PopulateDTO("friends.friend", "name fbId imageUrl status")));
        Call<UserDTO> profileCall = userApi.getProfile(userId, userProfileRequestDTO);
        profileCall.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                UserDTO userProfile = response.body();
                if(userProfile == null) {
                    loading.dismiss();
                    Toast.makeText(UserProfileActivity.this, "Unable to fetch User Profile.", Toast.LENGTH_LONG).show();
                    Log.e("USER_PROFILE_ACTIVITY", "Unable to fetch User Profile");
                } else {
                    Log.i("USER_PROFILE_ACTIVITY", "Fetched User profile. UserId: " + userProfile.getId());
                    setUserDetails(userProfile);
                    if(userActivities != null)
                        refreshScreen();
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                loading.dismiss();
                Log.e("USER_PROFILE_ACTIVITY", "Call to fetch User Profile failed");
            }
        });
    }

    public void likeUnlikeSong(String likedBy, String songOwnedBy, final int idxSongLiked) {
        SongDTO songLiked = userDetails.getSongs().get(idxSongLiked);
        if(songLiked.getLikes() == null)
            songLiked.setLikes(new ArrayList<String>());

        if(songLiked.getLikes().contains(likedBy))
            songLiked.setLikes(CollectionUtil.findAndRemoveFromList(songLiked.getLikes(), likedBy));
        else
            songLiked.getLikes().add(likedBy);

        userDataAndRelationsManager.saveSongsToServer(songOwnedBy, Arrays.asList(songLiked), new Callback<List<SongDTO>>() {
            @Override
            public void onResponse(Call<List<SongDTO>> call, Response<List<SongDTO>> response) {
                if(response.body() != null) {
                    Log.i("USER_PROFILE_ACTIVITY", "Liked/Unliked.");
                    tracksListFragment.updateDataRange(response.body(), idxSongLiked, idxSongLiked);
                } else {
                    Log.i("USER_PROFILE_ACTIVITY", "Like/Unlike failed");
                    Toast.makeText(UserProfileActivity.this, "Failed to update.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SongDTO>> call, Throwable t) {
                Log.i("USER_PROFILE_ACTIVITY", "Like/Unlike failed");
                Toast.makeText(UserProfileActivity.this, "Failed to update.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateViewsForSong(final int idxSongLiked) {
        SongDTO songViewed = userDetails.getSongs().get(idxSongLiked);
        songViewed.setViews(songViewed.getViews() + 1);

        userDataAndRelationsManager.saveSongsToServer(userDetails.getId(), Arrays.asList(songViewed), new Callback<List<SongDTO>>() {
            @Override
            public void onResponse(Call<List<SongDTO>> call, Response<List<SongDTO>> response) {
                if(response.body() != null) {
                    Log.i("USER_PROFILE_ACTIVITY", "Song View updated");
                    tracksListFragment.updateDataRange(response.body(), idxSongLiked, idxSongLiked);
                } else {
                    Log.i("USER_PROFILE_ACTIVITY", "Song View update failed");
                    //Toast.makeText(UserProfileActivity.this, "Failed to update.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SongDTO>> call, Throwable t) {
                Log.i("USER_PROFILE_ACTIVITY", "Song View updated failed");
                //Toast.makeText(UserProfileActivity.this, "Failed to update.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshScreen() {

        tracksListFragment = TracksListFragment.newInstance(userDetails.getSongs(), TracksListMode.USER_PROFILE_MODE, appUserDetails);
        friendsListFragment = FriendsListFragment.newInstance(userDetails.getFriends(), LinearLayoutManager.VERTICAL);
        activitiesFragment = ActivitiesFragment.newInstance(userActivities);

        loading.dismiss();
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
        viewPagerAdapter.addFragment(friendsListFragment, "Friends");
        viewPagerAdapter.addFragment(tracksListFragment, "Songs");
        viewPagerAdapter.addFragment(activitiesFragment, "Activities");
        return viewPagerAdapter;
    }

    @Override
    public void onLikeButtonClick(int position, List<SongDTO> tracksList) {
        Toast.makeText(UserProfileActivity.this, "Connecting to server...", Toast.LENGTH_SHORT).show();
        likeUnlikeSong(appUserDetails.getId(), userDetails.getId(), position);
    }

    @Override
    public void onTrackInfoClick(int position, List<SongDTO> tracksList) {
        Toast.makeText(UserProfileActivity.this, "Searching in youtube...", Toast.LENGTH_SHORT).show();
        updateViewsForSong(position);
        AppUtil.searchSongOnYoutube(tracksList.get(position).getMetadata(), this);
    }

    @Override
    public void onLikeButtonLongClick(int position, List<SongDTO> tracksList) {
        List<String> usersWhoLikedTheSong = tracksList.get(position).getLikes();
        Intent usersListIntent = new Intent(this, UserListActivity.class);
        usersListIntent.putStringArrayListExtra("userIds", (ArrayList<String>) usersWhoLikedTheSong);
        usersListIntent.putExtra("titleKey", "Liked By");
        startActivity(usersListIntent);
    }
}
