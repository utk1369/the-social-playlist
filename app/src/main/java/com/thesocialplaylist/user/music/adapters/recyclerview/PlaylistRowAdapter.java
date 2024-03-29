package com.thesocialplaylist.user.music.adapters.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thesocialplaylist.user.music.R;
import com.thesocialplaylist.user.music.dto.PlaylistDTO;

import java.util.List;

/**
 * Created by user on 27-06-2016.
 */
public class PlaylistRowAdapter extends RecyclerView.Adapter<PlaylistRowAdapter.PlaylistRowHolder> {

    private List<PlaylistDTO> playlistDTOs;
    private Context appContext;

    public PlaylistRowAdapter(List<PlaylistDTO> playlistDTOs, Context context) {
        this.playlistDTOs = playlistDTOs;
        this.appContext = context;
    }

    @Override
    public PlaylistRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_playlists, parent, false);
        return new PlaylistRowHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PlaylistRowHolder holder, int position) {
        PlaylistDTO playlistDTO = playlistDTOs.get(position);
        holder.playlistTitle.setText(playlistDTO.getName());
        //holder.noOfSongs.setText(playlistDTO.get);
    }

    @Override
    public int getItemCount() {
        return playlistDTOs.size();
    }

    public class PlaylistRowHolder extends RecyclerView.ViewHolder {
        private TextView playlistTitle;
        private TextView noOfSongs;

        public PlaylistRowHolder(View itemView) {
            super(itemView);
            playlistTitle = (TextView) itemView.findViewById(R.id.playlist_title);
            noOfSongs = (TextView) itemView.findViewById(R.id.no_of_songs);
        }
    }
}
