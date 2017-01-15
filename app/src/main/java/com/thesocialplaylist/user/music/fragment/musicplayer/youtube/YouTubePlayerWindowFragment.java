package com.thesocialplaylist.user.music.fragment.musicplayer.youtube;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.thesocialplaylist.user.music.R;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link YouTubePlayerWindowFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class YouTubePlayerWindowFragment extends Fragment implements YouTubePlayer.OnInitializedListener {

    private static final String URI = "uri";
    private static final String API_KEY = "apiKey";

    private String videoURI = null;
    private String apiKey = null;

    public YouTubePlayerWindowFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param uri Uri for the video.
     * @return A new instance of fragment YouTubePlayerWindowFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static YouTubePlayerWindowFragment newInstance(String uri, String apiKey) {
        YouTubePlayerWindowFragment fragment = new YouTubePlayerWindowFragment();
        Bundle args = new Bundle();
        args.putString(URI, uri);
        args.putString(API_KEY, apiKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            videoURI = getArguments().getString(URI);
            apiKey = getArguments().getString(API_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View playerWindow = inflater.inflate(R.layout.fragment_you_tube_player_window, container, false);
        YouTubePlayerSupportFragment youTubePlayerSupportFragment
                = YouTubePlayerSupportFragment.newInstance();
        youTubePlayerSupportFragment.initialize(apiKey, this);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.player, youTubePlayerSupportFragment);
        transaction.commit();

        return playerWindow;
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        if(!wasRestored) {
            youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
            youTubePlayer.loadVideo(videoURI);
            //youTubePlayer.play();
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        String errorMsg = youTubeInitializationResult.toString();
        Log.e("YOUTUBE_PLAYER_FRAGMENT", errorMsg);
        Toast.makeText(getActivity().getApplicationContext(), "Could not play Video. Please retry!", Toast.LENGTH_LONG).show();
    }
}
