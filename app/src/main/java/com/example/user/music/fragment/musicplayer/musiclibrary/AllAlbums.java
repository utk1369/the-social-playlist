package com.example.user.music.fragment.musicplayer.musiclibrary;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.music.AlbumGridAdapter;
import com.example.user.music.R;
import com.example.user.music.activity.musicplayer.AlbumDetailsActivity;
import com.example.user.music.models.Album;
import com.example.user.music.utils.AppUtil;

import java.util.List;

/**
 * Created by utk on 11-05-2016.
 */
public class AllAlbums extends Fragment {
    private View fragmentView;
    private RecyclerView albumGrid;
    private List<Album> albums;
    private Context appContext;
    private AlbumGridAdapter albumsAdapter;

    private GridLayoutManager albumGridLayoutManager;

    public AllAlbums() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_album_grid, container, false);
        initialize();
        return fragmentView;
    }

    private void initialize() {
        albumGrid = (RecyclerView) fragmentView.findViewById(R.id.album_grid);
        appContext = getActivity().getApplicationContext();
        albums = AppUtil.getAllAlbumsAsList(appContext);
        Log.i("No of Albums = ", albums.size() + "");
        albumsAdapter = new AlbumGridAdapter(albums, appContext);
        albumsAdapter.setGridClickListener(new AlbumGridAdapter.GridClickListener() {
            @Override
            public void onGridItemClick(View view, int position) {
                Log.i("Item clicked at ", position + "");
                Album clickedAlbum = albums.get(position);
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
