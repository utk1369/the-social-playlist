package com.thesocialplaylist.user.music.activity.musicplayer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;
import com.thesocialplaylist.user.music.R;
import com.thesocialplaylist.user.music.TheSocialPlaylistApplication;
import com.thesocialplaylist.user.music.adapters.custom.FriendsSearchAutoCompleteAdapter;
import com.thesocialplaylist.user.music.api.declaration.UserApi;
import com.thesocialplaylist.user.music.dto.FriendDTO;
import com.thesocialplaylist.user.music.dto.LinkDTO;
import com.thesocialplaylist.user.music.dto.SocialActivityDTO;
import com.thesocialplaylist.user.music.dto.SongDTO;
import com.thesocialplaylist.user.music.dto.UserDTO;
import com.thesocialplaylist.user.music.enums.ActivitySourceType;
import com.thesocialplaylist.user.music.enums.SocialActivityDomain;
import com.thesocialplaylist.user.music.enums.SocialActivityType;
import com.thesocialplaylist.user.music.fragment.FriendsListFragment;
import com.thesocialplaylist.user.music.manager.UserDataAndRelationsManager;
import com.thesocialplaylist.user.music.utils.AppUtil;
import com.thesocialplaylist.user.music.utils.ImageUtil;
import com.thesocialplaylist.user.music.utils.TextUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongShareActivity extends AppCompatActivity {

    private CardView selectedFriendsCard;

    private CardView songMetadatCard;

    private CardView externalLinkCard;

    private TextView songTitle;

    private TextView songArtist;

    private TextView songAlbum;

    private EditText caption;

    private TextView recipientSelectionHeader;

    private RadioButton radioPublic;

    private RadioButton radioRecipientsOnly;

    private RadioButton radioDedicate;

    private RadioButton radioRecommend;

    private RadioButton radioShare;

    private SongDTO songToShare;

    private ActivitySourceType activitySourceType;

    private TextView previewTitle;

    private ImageView previewImg;

    private TextView previewDesc;

    private SocialActivityType socialActivityType;

    private String sourceApp;

    private ImageButton submit;

    private SocialActivityDomain socialActivityDomain;

    private AutoCompleteTextView friendsSearchBar;

    private FrameLayout selectedRecipientsFragment;

    private List<FriendDTO> selectedFriends = new ArrayList<>();

    private UserApi userApi;

    private TextCrawler textCrawler;

    @Inject
    UserDataAndRelationsManager userDataAndRelationsManager;

    private String linkUrl;

    private LinkDTO linkDTO;

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
        activitySourceType = (ActivitySourceType) songShareIntent.getSerializableExtra("ACTIVITY_SOURCE");
        if(activitySourceType == null)
            activitySourceType = ActivitySourceType.EXTERNAL;
        userApi = userDataAndRelationsManager.getUserApi();

        sourceApp = songShareIntent.getPackage();
        Log.i("SONG_SHARE_ACTIVITY", "Called by package: " + sourceApp);

        final UserDTO userDetails = userDataAndRelationsManager.getAppUserDataFromCache();

        selectedFriendsCard = (CardView) findViewById(R.id.select_friends_card);
        songMetadatCard = (CardView) findViewById(R.id.song_metadata_card);
        externalLinkCard = (CardView) findViewById(R.id.external_link_card);

        songTitle = (TextView) findViewById(R.id.song_title);
        songArtist = (TextView) findViewById(R.id.song_artist);
        songAlbum = (TextView) findViewById(R.id.song_album);

        previewTitle = (TextView) findViewById(R.id.preview_title);
        previewImg = (ImageView) findViewById(R.id.preview_img);
        previewDesc = (TextView) findViewById(R.id.preview_desc);

        caption = (EditText) findViewById(R.id.caption);
        recipientSelectionHeader =  (TextView) findViewById(R.id.recipient_header);
        friendsSearchBar = (AutoCompleteTextView) findViewById(R.id.auto_complete_friends_search);
        selectedRecipientsFragment = (FrameLayout) findViewById(R.id.selected_recipients_fragment);
        radioPublic = (RadioButton) findViewById(R.id.radio_public);
        radioRecipientsOnly = (RadioButton) findViewById(R.id.radio_recipients_only);
        radioDedicate = (RadioButton) findViewById(R.id.dedicate);
        radioRecommend = (RadioButton) findViewById(R.id.recommend);
        radioShare = (RadioButton) findViewById(R.id.share);

        textCrawler = new TextCrawler();

        if(socialActivityType != null) {
            prepareLayout();
        }

        if(activitySourceType == ActivitySourceType.EXTERNAL) {
            songMetadatCard.setVisibility(View.GONE);
            externalLinkCard.setVisibility(View.VISIBLE);
            linkUrl = songShareIntent.getExtras().getString(Intent.EXTRA_TEXT);
            textCrawler.makePreview(new LinkPreviewCallback() {
                @Override
                public void onPre() {
                    Toast.makeText(SongShareActivity.this, "Loading: " + linkUrl, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPos(SourceContent sourceContent, boolean b) {
                    Toast.makeText(SongShareActivity.this, "Title: " + sourceContent.getTitle(), Toast.LENGTH_SHORT).show();
                    linkDTO = new LinkDTO();
                    linkDTO.setUrl(linkUrl);
                    linkDTO.setPreviewTitle(sourceContent.getTitle());
                    previewTitle.setText(sourceContent.getTitle());
                    Toast.makeText(SongShareActivity.this, "Description: " + sourceContent.getDescription(), Toast.LENGTH_SHORT).show();
                    if(sourceContent.getImages() != null && sourceContent.getImages().size() > 0) {
                        Toast.makeText(SongShareActivity.this, "Image(0): " + sourceContent.getImages().get(0), Toast.LENGTH_SHORT).show();
                        ImageUtil.loadImageUsingPicasso(getApplicationContext(),
                                Uri.parse(sourceContent.getImages().get(0)), previewImg);
                        linkDTO.setPreviewImage(sourceContent.getImages().get(0));
                    }
                    previewDesc.setText(sourceContent.getDescription());
                    linkDTO.setPreviewDesc(sourceContent.getDescription());
                }
            }, linkUrl);

        } else {
            if(songToShare != null && songToShare.getMetadata() != null) {
                songTitle.setText(songToShare.getMetadata().getTitle());
                songArtist.setText(songToShare.getMetadata().getArtist());
                songAlbum.setText(songToShare.getMetadata().getAlbum());
            }
        }

        FriendsSearchAutoCompleteAdapter adapter = new FriendsSearchAutoCompleteAdapter(getApplicationContext(),
                userDataAndRelationsManager.getAllFriendsDataFromCacheAsList());
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

        submit = (ImageButton) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SocialActivityDTO socialActivityDTO = new SocialActivityDTO();
                socialActivityDTO.setPostedBy(userDetails.getId());
                if(songToShare != null)
                    socialActivityDTO.setSongMetadata(songToShare.getMetadata());
                socialActivityDTO.setLink(linkDTO);
                socialActivityDTO.setSource(activitySourceType);
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

    private void prepareLayout() {
        getSupportActionBar().setTitle(TextUtil.convertTextToTitleCase(socialActivityType.toString()));
        if(socialActivityType.equals(SocialActivityType.SHARE)) {
            radioShare.setChecked(true);
            selectedFriendsCard.setVisibility(View.GONE);
            radioRecipientsOnly.setVisibility(View.GONE);
            radioPublic.setChecked(true);
        } else if(socialActivityType.equals(SocialActivityType.DEDICATE)) {
            radioDedicate.setChecked(true);
            selectedFriendsCard.setVisibility(View.VISIBLE);
            radioRecipientsOnly.setVisibility(View.VISIBLE);
            radioRecipientsOnly.setChecked(true);
        } else if(socialActivityType.equals(SocialActivityType.RECOMMEND)) {
            radioRecommend.setChecked(true);
            selectedFriendsCard.setVisibility(View.VISIBLE);
            radioRecipientsOnly.setVisibility(View.VISIBLE);
            radioPublic.setChecked(true);
        }
    }

    private void submitActivity(SocialActivityDTO socialActivityDTO) {
        Boolean isValidActivity = validateSubmit(socialActivityDTO);
        if(!isValidActivity) {
            return;
        }
        final ProgressDialog loading = ProgressDialog.show(SongShareActivity.this, "Please wait", "Sharing your activity");
        Call<SocialActivityDTO> activitySaveCall = userApi.linkSongToActivity(socialActivityDTO);
        activitySaveCall.enqueue(new Callback<SocialActivityDTO>() {
            @Override
            public void onResponse(Call<SocialActivityDTO> call, Response<SocialActivityDTO> response) {
                loading.dismiss();
                if(response.isSuccessful()) {
                    Log.i("SONG_SHARE_ACTIVITY", "Successfully shared: " + response.body().getId());
                    Toast.makeText(SongShareActivity.this, "The song was successfully shared.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SongShareActivity.this, "An Error occurred. Please try again.", Toast.LENGTH_LONG).show();
                }
                finish();
            }

            @Override
            public void onFailure(Call<SocialActivityDTO> call, Throwable t) {
                Toast.makeText(SongShareActivity.this, "An Error occurred. Please try again.", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private Boolean validateSubmit(SocialActivityDTO socialActivityDTO) {
        return true;
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

    public void setSocialActivityType(View v) {
        boolean isTypeChecked = ((RadioButton) v).isChecked();
        if(isTypeChecked) {
            if(v.getId() == R.id.dedicate) {
                socialActivityType = SocialActivityType.DEDICATE;
            } else if(v.getId() == R.id.share) {
                socialActivityType = SocialActivityType.SHARE;
            } if(v.getId() == R.id.recommend) {
                socialActivityType = SocialActivityType.RECOMMEND;
            }
            prepareLayout();
        }
    }
}
