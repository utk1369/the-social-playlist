package com.thesocialplaylist.user.music.fragment.musicplayer.musiclibrary;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.thesocialplaylist.user.music.recyclerview.adapters.ArtistsListAdapter;
import com.thesocialplaylist.user.music.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by user on 15-06-2016.
 */
public class ArtistsListFragment extends Fragment {

    private View fragmentView;
    private RecyclerView artistsList;
    private Context appContext;
    private List<String> artists;
    private ArtistsListAdapter artistsAdapter;
    private LinearLayoutManager artistsListLayoutManager;

    public ArtistsListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_artists_list, container, false);
        initialize();
        return fragmentView;
    }

    private void initialize() {
        artistsList = (RecyclerView) fragmentView.findViewById(R.id.artists_list);
        appContext = getActivity().getApplicationContext();
        artists = Arrays.asList(new String[] {"Pink Floyd", "Coldplay", "Mohit Chauhan"});
        Log.i("No of Artists = ", artists.size() + "");
        artistsAdapter = new ArtistsListAdapter(artists, appContext);

        artistsList.setAdapter(artistsAdapter);
        artistsListLayoutManager = new LinearLayoutManager(appContext);
        artistsList.setLayoutManager(artistsListLayoutManager);
    }
}
