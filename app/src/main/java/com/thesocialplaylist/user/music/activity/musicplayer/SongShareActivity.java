package com.thesocialplaylist.user.music.activity.musicplayer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.thesocialplaylist.user.music.R;
import com.thesocialplaylist.user.music.TheSocialPlaylistApplication;
import com.thesocialplaylist.user.music.adapters.custom.FriendsSearchAutoCompleteAdapter;
import com.thesocialplaylist.user.music.api.declaration.UserApi;
import com.thesocialplaylist.user.music.dto.FriendDTO;
import com.thesocialplaylist.user.music.dto.SocialActivityDTO;
import com.thesocialplaylist.user.music.dto.SongDTO;
import com.thesocialplaylist.user.music.dto.UserDTO;
import com.thesocialplaylist.user.music.enums.SocialActivityDomain;
import com.thesocialplaylist.user.music.enums.SocialActivityType;
import com.thesocialplaylist.user.music.fragment.FriendsListFragment;
import com.thesocialplaylist.user.music.manager.UserDataAndRelationsManager;
import com.thesocialplaylist.user.music.utils.AppUtil;
import com.thesocialplaylist.user.music.utils.TextUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongShareActivity extends AppCompatActivity {

    private TextView songTitle;

    private TextView songArtist;

    private TextView songAlbum;

    private EditText caption;

    private TextView recipientSelectionHeader;

    private SongDTO songToShare;

    private SocialActivityType socialActivityType;

    private SocialActivityDomain socialActivityDomain;

    private AutoCompleteTextView friendsSearchBar;

    private FrameLayout selectedRecipientsFragment;

    private List<FriendDTO> selectedFriends = new ArrayList<>();

    private UserApi userApi;

    @Inject
    UserDataAndRelationsManager userDataAndRelationsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_share);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        init();
    }

    public void init() {
        ((TheSocialPlaylistApplication) getApplication()).getUserDataAndRelationsManagerComponent().inject(this);
        Intent songShareIntent = getIntent();
        songToShare = (SongDTO) songShareIntent.getSerializableExtra("SONG_TO_SHARE");
        socialActivityType = (SocialActivityType) songShareIntent.getSerializableExtra("ACTIVITY_TYPE");
        userApi = userDataAndRelationsManager.getUserApi();

        final UserDTO userDetails = userDataAndRelationsManager.getAppUserDataFromCache();

        songTitle = (TextView) findViewById(R.id.song_title);
        songArtist = (TextView) findViewById(R.id.song_artist);
        songAlbum = (TextView) findViewById(R.id.song_album);
        caption = (EditText) findViewById(R.id.caption);
        recipientSelectionHeader =  (TextView) findViewById(R.id.recipient_header);
        friendsSearchBar = (AutoCompleteTextView) findViewById(R.id.auto_complete_friends_search);
        selectedRecipientsFragment = (FrameLayout) findViewById(R.id.selected_recipients_fragment);

        getSupportActionBar().setTitle(TextUtil.convertTextToTitleCase(socialActivityType.toString()));
        songTitle.setText(songToShare.getMetadata().getTitle());
        songArtist.setText(songToShare.getMetadata().getArtist());
        songAlbum.setText(songToShare.getMetadata().getAlbum());
        FriendsSearchAutoCompleteAdapter adapter = new FriendsSearchAutoCompleteAdapter(getApplicationContext(),
                userDataAndRelationsManager.getAllFriendsDataFromCache());
        friendsSearchBar.setAdapter(adapter);
        friendsSearchBar.setThreshold(1);
        friendsSearchBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedFriends.add((FriendDTO) adapterView.getItemAtPosition(i));
                friendsSearchBar.setText("");
                Log.i("SONG_SHARE_ACTIVITY", ((FriendDTO) adapterView.getItemAtPosition(i)).getFriend().getName() + " was selected");

                //TODO: Use notifyDataSetChanged instead of replacing the fragment every time.
                AppUtil.replaceFragments(getSupportFragmentManager(), R.id.selected_recipients_fragment,
                        FriendsListFragment.newInstance(selectedFriends, LinearLayoutManager.HORIZONTAL));
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SocialActivityDTO socialActivityDTO = new SocialActivityDTO();
                socialActivityDTO.setPostedBy(userDetails.getId());
                socialActivityDTO.setSongMetadata(songToShare.getMetadata());
                socialActivityDTO.setActivityType(socialActivityType);
                socialActivityDTO.setDomain(socialActivityDomain);
                socialActivityDTO.setCaption(caption.getText().toString());
                List<String> recipientUserIds = new ArrayList<String>();
                for(FriendDTO selectedFriend: selectedFriends) {
                    recipientUserIds.add(selectedFriend.getFriend().getId());
                }
                socialActivityDTO.setRecipientUserIds(recipientUserIds);
                submitActivity(socialActivityDTO);
            }
        });
    }

    private void submitActivity(SocialActivityDTO socialActivityDTO) {
        final ProgressDialog loading = ProgressDialog.show(SongShareActivity.this, "Please wait", "Sharing your activity");
        Call<SongDTO> activitySaveCall = userApi.linkSongToActivity(socialActivityDTO);
        activitySaveCall.enqueue(new Callback<SongDTO>() {
            @Override
            public void onResponse(Call<SongDTO> call, Response<SongDTO> response) {
                loading.dismiss();
                if(response.isSuccessful()) {
                    Log.i("SONG_SHARE_ACTIVITY", "Successfully shared: " + response.body().getMetadata().getTitle());
                    Toast.makeText(SongShareActivity.this, "The song was successfully shared.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SongShareActivity.this, "An Error occurred. Please try again.", Toast.LENGTH_LONG).show();
                }
                finish();
            }

            @Override
            public void onFailure(Call<SongDTO> call, Throwable t) {
                Toast.makeText(SongShareActivity.this, "An Error occurred. Please try again.", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    public void setVisibilityMode(View v) {
        boolean checked = ((RadioButton) v).isChecked();
        if(checked) {
            if(v.getId() == R.id.radio_public)
                socialActivityDomain = SocialActivityDomain.PUBLIC;
            else if(v.getId() == R.id.radio_recipients_only)
                socialActivityDomain = SocialActivityDomain.PRIVATE;
        }
        Log.i("SONG_SHARE_ACTIVITY",  "Domain: " + socialActivityDomain.toString());
    }

}
