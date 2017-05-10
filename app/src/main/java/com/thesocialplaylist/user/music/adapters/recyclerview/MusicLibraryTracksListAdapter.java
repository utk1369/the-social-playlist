package com.thesocialplaylist.user.music.adapters.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.thesocialplaylist.user.music.R;
import com.thesocialplaylist.user.music.dto.SongDTO;
import com.thesocialplaylist.user.music.utils.ImageUtil;

import java.util.List;

/**
 * Created by user on 08-05-2017.
 */

public class MusicLibraryTracksListAdapter extends RecyclerView.Adapter<MusicLibraryTracksListAdapter.MusicLibraryTrackRowHolder> {

    private List<SongDTO> tracksList;
    private Context appContext;
    private int selectedRow;
    private OnTrackItemClickListener onTrackItemClickListener;
    private OnOptionsButtonClickListener onOptionsButtonClickListener;

    public interface OnTrackItemClickListener {
        public void onTrackItemClick(View v, int position, List<SongDTO> tracksList);
    }

    public interface OnOptionsButtonClickListener {
        public void onOptionsButtonClick(View v, int position, List<SongDTO> tracksList);
    }

    public MusicLibraryTracksListAdapter(List<SongDTO> tracksList, Context appContext,
                                         OnTrackItemClickListener onTrackItemClickListener, OnOptionsButtonClickListener onOptionsButtonClickListener) {
        this.tracksList = tracksList;
        this.appContext = appContext;
        this.onTrackItemClickListener = onTrackItemClickListener;
        this.onOptionsButtonClickListener = onOptionsButtonClickListener;
    }

    @Override
    public MusicLibraryTrackRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_row, parent, false);
        return new MusicLibraryTrackRowHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MusicLibraryTrackRowHolder holder, int position) {
        final SongDTO track = tracksList.get(position);
        holder.trackTitle.setText(track.getMetadata().getTitle());
        holder.trackArtist.setText(track.getMetadata().getArtist());
        ImageUtil.loadAlbumArt(appContext, track.getMetadata().getAlbumId(), holder.thumbnail);

        Boolean selectionCriteria = (position == selectedRow);
        highlightRow(holder.itemView, selectionCriteria);
    }

    public void highlightRow(View view, boolean isSelected) {
        TextView selectedTitle = (TextView) view.findViewById(R.id.track_title);
        TextView selectedArtist = (TextView) view.findViewById(R.id.track_artist);
        ImageButton options = (ImageButton) view.findViewById(R.id.options);

        if(isSelected) {
            view.setBackgroundColor(android.graphics.Color.BLACK);
            selectedTitle.setTextColor(android.graphics.Color.WHITE);
            selectedArtist.setTextColor(android.graphics.Color.WHITE);
            options.setImageResource(R.drawable.ic_more_vert_white_24dp);
            view.setSelected(true);
        } else {
            selectedTitle.setTextColor(android.graphics.Color.BLACK);
            selectedArtist.setTextColor(android.graphics.Color.BLACK);
            view.setBackgroundColor(android.graphics.Color.WHITE);
            options.setImageResource(R.drawable.ic_more_vert_black_24dp);
            view.setSelected(false);
        }
    }

    @Override
    public int getItemCount() {
        return tracksList.size();
    }

    public class MusicLibraryTrackRowHolder extends RecyclerView.ViewHolder {
        private static final int RANGE_THRESHOLD = 10;
        private ImageView thumbnail;
        private TextView trackTitle;
        private TextView trackArtist;
        private ImageButton options;

        public MusicLibraryTrackRowHolder(final View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            trackTitle = (TextView) itemView.findViewById(R.id.track_title);
            trackArtist = (TextView) itemView.findViewById(R.id.track_artist);
            options = (ImageButton) itemView.findViewById(R.id.options);
            bindListeners(itemView);
        }

        private void bindListeners(final View itemView) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedRow = getAdapterPosition();
                    if(onTrackItemClickListener != null) {
                        notifyItemRangeChanged(Math.max(0, getAdapterPosition() - RANGE_THRESHOLD),
                                Math.min(tracksList.size() - (getAdapterPosition() - RANGE_THRESHOLD), 2 * RANGE_THRESHOLD));
                        onTrackItemClickListener.onTrackItemClick(itemView, getAdapterPosition(), tracksList);
                    }
                }
            });

            options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onOptionsButtonClickListener != null) {
                        onOptionsButtonClickListener.onOptionsButtonClick(itemView, getAdapterPosition(), tracksList);
                    }
                }
            });
        }
    }

    public void refreshTracksList(List<SongDTO> updatedSongsList) {
        this.tracksList = updatedSongsList;
        notifyDataSetChanged();
    }

    public void refreshTracksListRange(List<SongDTO> updatedSongsList, int start, int end) {
        for(int i = start; i <= end; i++) {
            this.tracksList.set(i, updatedSongsList.get(i));
        }
        notifyItemRangeChanged(start, (end - start + 1));
    }
}
