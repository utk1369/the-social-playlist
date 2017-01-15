package com.thesocialplaylist.user.music.fragment.musicplayer.youtube;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.api.services.youtube.model.SearchResult;
import com.thesocialplaylist.user.music.R;
import com.thesocialplaylist.user.music.TheSocialPlaylistApplication;
import com.thesocialplaylist.user.music.dto.ExternalLinksDTO;
import com.thesocialplaylist.user.music.enums.ExternalLinkType;
import com.thesocialplaylist.user.music.manager.MusicLibraryManager;
import com.thesocialplaylist.user.music.recyclerview.adapters.YouTubeSearchListAdapter;
import com.thesocialplaylist.user.music.utils.YoutubeUtil;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Use the {@link YoutubeSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class YoutubeSearchFragment extends Fragment {

    private static final String SEARCH_STRING = "searchString";
    private static final String NO_OF_RESULTS = "nResults";
    private static final String API_KEY = "apiKey";

    private String searchString;
    private Long noOfResults;
    private String apiKey;

    private View searchView;

    private OnListItemClickListener onListItemClickListener;

    private RecyclerView youTubeSearchListView;

    @Inject
    MusicLibraryManager musicLibraryManager;

    public YoutubeSearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param searchString Parameter 1.
     * @param noOfResults Parameter 2.
     * @return A new instance of fragment YoutubeSearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static YoutubeSearchFragment newInstance(String searchString, Long noOfResults, String apiKey) {
        YoutubeSearchFragment fragment = new YoutubeSearchFragment();
        Bundle args = new Bundle();
        args.putString(SEARCH_STRING, searchString);
        args.putLong(NO_OF_RESULTS, noOfResults);
        args.putString(API_KEY, apiKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            searchString = getArguments().getString(SEARCH_STRING);
            noOfResults = getArguments().getLong(NO_OF_RESULTS);
            apiKey = getArguments().getString(API_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        searchView = inflater.inflate(R.layout.fragment_youtube_search, container, false);
        youTubeSearchListView = (RecyclerView) searchView.findViewById(R.id.youtube_search_list);
        ((TheSocialPlaylistApplication)getActivity().getApplication()).getMusicLibraryManagerComponent().inject(this);
        try {
            getSearchResults();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return searchView;
    }

    private void getSearchResults() throws IOException {
        Log.i("api key", apiKey);
        YouTube youTube = YoutubeUtil.getNewInstance(getActivity().getApplicationContext());
        final YouTube.Search.List query = YoutubeUtil.buildSearchQuery(
                youTube, apiKey, noOfResults, searchString);

        new SearchOnYoutube().execute(query);
    }

    private class SearchOnYoutube extends AsyncTask<YouTube.Search.List, Void, SearchListResponse> {

        @Override
        protected SearchListResponse doInBackground(YouTube.Search.List... params) {
            YouTube.Search.List query = params[0];
            try {
                return query.execute();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(SearchListResponse response) {
            displaySearchResults(response.getItems());
        }
    }

    private void displaySearchResults(List<SearchResult> results) {
        List<ExternalLinksDTO> youtubeVideoDTOs = new ArrayList<>();
        for(SearchResult result: results) {
            ExternalLinksDTO video = new ExternalLinksDTO();
            video.setId(result.getId().getVideoId());
            video.setTitle(result.getSnippet().getTitle());
            video.setThumbnailUrl(result.getSnippet().getThumbnails().getDefault().getUrl());
            video.setLinkType(ExternalLinkType.YOUTUBE);
            youtubeVideoDTOs.add(video);
        }
        publishResultsToRecyclerView(youtubeVideoDTOs);
    }

    private void publishResultsToRecyclerView(final List<ExternalLinksDTO> youtubeVideoDTOs) {
        final YouTubeSearchListAdapter adapter = new YouTubeSearchListAdapter(
                youtubeVideoDTOs, getActivity().getApplicationContext());

        String linkedVideoId = getActivity().getIntent().getStringExtra("LINKED_VIDEO_ID");
        for(int i=0; i<youtubeVideoDTOs.size() && linkedVideoId != null; i++) {
            if(youtubeVideoDTOs.get(i).getId().equals(linkedVideoId)) {
                adapter.setSelectedVideoPos(i);
                break;
            }
        }
        adapter.setOnItemClickListener(new YouTubeSearchListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                onListItemClickListener.onListItemSelected(youtubeVideoDTOs.get(position));
            }
        });
        adapter.setOnItemSelectListener(new YouTubeSearchListAdapter.OnItemSelectListener() {
            @Override
            public void onItemSelect(int pos) {
                adapter.setSelectedVideoPos(pos);
                Toast.makeText(getActivity().getApplicationContext(), "Linking the selected video...", Toast.LENGTH_SHORT).show();
                ExternalLinksDTO externalLinksDTO = musicLibraryManager
                        .associateSongToExternalLink(getActivity().getIntent().getStringExtra("SONG_ID"),
                                youtubeVideoDTOs.get(pos));
                Toast.makeText(getActivity().getApplicationContext(), "Done!", Toast.LENGTH_SHORT).show();
            }
        });
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        youTubeSearchListView.setHasFixedSize(true);
        youTubeSearchListView.setLayoutManager(linearLayoutManager);
        youTubeSearchListView.setAdapter(adapter);
    }

    public interface OnListItemClickListener {
        public void onListItemSelected(ExternalLinksDTO selectedVideo);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        onListItemClickListener = (OnListItemClickListener) activity;
    }
}
