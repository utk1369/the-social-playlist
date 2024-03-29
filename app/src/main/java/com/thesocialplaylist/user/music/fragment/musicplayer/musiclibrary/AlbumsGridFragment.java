package com.thesocialplaylist.user.music.fragment.musicplayer.musiclibrary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thesocialplaylist.user.music.TheSocialPlaylistApplication;
import com.thesocialplaylist.user.music.dto.AlbumDTO;
import com.thesocialplaylist.user.music.dto.SongDTO;
import com.thesocialplaylist.user.music.fragment.musicplayer.mediacontroller.MediaControllerFragment;
import com.thesocialplaylist.user.music.manager.MusicLibraryManager;
import com.thesocialplaylist.user.music.adapters.recyclerview.AlbumGridAdapter;
import com.thesocialplaylist.user.music.R;
import com.thesocialplaylist.user.music.activity.musicplayer.AlbumDetailsActivity;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by utk on 11-05-2016.
 */
public class AlbumsGridFragment extends Fragment {
    private View fragmentView;
    private RecyclerView albumGrid;
    private List<AlbumDTO> albumDTOs;
    private Context appContext;
    private AlbumGridAdapter albumsAdapter;

    private GridLayoutManager albumGridLayoutManager;

    private static final String ARG_ALBUMS_LIST = "AlbumsList";

    public static AlbumsGridFragment newInstance(List<AlbumDTO> albumDTOs) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_ALBUMS_LIST, (Serializable) albumDTOs);
        AlbumsGridFragment albumsGridFragment = new AlbumsGridFragment();
        albumsGridFragment.setArguments(args);
        return albumsGridFragment;
    }


    public AlbumsGridFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_albums_grid, container, false);
        initialize();
        return fragmentView;
    }

    private void initialize() {

        albumGrid = (RecyclerView) fragmentView.findViewById(R.id.album_grid);
        appContext = getActivity().getApplicationContext();

        albumDTOs = (List<AlbumDTO>) getArguments().getSerializable(ARG_ALBUMS_LIST);
        Log.i("No of Albums = ", albumDTOs.size() + "");
        albumsAdapter = new AlbumGridAdapter(albumDTOs, appContext);
        albumsAdapter.setGridClickListener(new AlbumGridAdapter.GridClickListener() {
            @Override
            public void onGridItemClick(View view, int position) {
                Log.i("Item clicked at ", position + "");
                AlbumDTO clickedAlbum = albumDTOs.get(position);
                Intent albumDetailsIntent = new Intent(getActivity().getApplicationContext(), AlbumDetailsActivity.class);
                albumDetailsIntent.putExtra("ALBUM_SELECTED", clickedAlbum);
                startActivity(albumDetailsIntent);
            }
        });
        albumGrid.setAdapter(albumsAdapter);
        albumGridLayoutManager = new GridLayoutManager(appContext, 2);
        albumGrid.setHasFixedSize(true);
        albumGrid.setLayoutManager(albumGridLayoutManager);
    }
}
