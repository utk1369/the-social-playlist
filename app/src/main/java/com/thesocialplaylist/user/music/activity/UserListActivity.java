package com.thesocialplaylist.user.music.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.thesocialplaylist.user.music.R;
import com.thesocialplaylist.user.music.TheSocialPlaylistApplication;
import com.thesocialplaylist.user.music.dto.FriendDTO;
import com.thesocialplaylist.user.music.dto.UserDTO;
import com.thesocialplaylist.user.music.fragment.FriendsListFragment;
import com.thesocialplaylist.user.music.manager.UserDataAndRelationsManager;
import com.thesocialplaylist.user.music.utils.AppUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class UserListActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FriendsListFragment friendsListFragment;

    private List<String> userIds;
    private String activityTitle;

    private final String USER_IDS_KEY = "userIds";
    private final String TITLE_KEY = "titleKey";


    @Inject
    UserDataAndRelationsManager userDataAndRelationsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        initialize();
    }

    private void initialize() {
        ((TheSocialPlaylistApplication) getApplication()).getUserDataAndRelationsManagerComponent().inject(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userIds = getIntent().getStringArrayListExtra(USER_IDS_KEY);
        activityTitle = getIntent().getStringExtra(TITLE_KEY);

        getSupportActionBar().setTitle(activityTitle);

        List<FriendDTO> friends = translateUserIdsToFriendDTOs(userIds);
        friendsListFragment = FriendsListFragment.newInstance(friends, LinearLayoutManager.VERTICAL);
        AppUtil.replaceFragments(getSupportFragmentManager(), R.id.users_list_fragment, friendsListFragment);
    }

    private List<FriendDTO> translateUserIdsToFriendDTOs(List<String> userIds) {
        List<FriendDTO> friendDTOs = new ArrayList<>();

        Map<String, UserDTO> allUsersInCache = userDataAndRelationsManager.getAllUsersFromCacheAsMap();
        for(String userId: userIds) {
            if(allUsersInCache.containsKey(userId)) {
                FriendDTO friend = new FriendDTO();
                friend.setFriend(allUsersInCache.get(userId));
                friend.setFbId(allUsersInCache.get(userId).getFbId());
                friendDTOs.add(friend);
            }
        }
        return friendDTOs;
    }

}
