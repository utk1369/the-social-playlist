package com.example.user.music;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.music.enums.TracksListMode;
import com.example.user.music.models.Song;
import com.example.user.music.utils.AlbumArtAsyncLoader;
import com.example.user.music.utils.AppUtil;
import com.example.user.music.utils.AsyncTaskResponseHandler;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by user on 13-04-2016.
 */
public class TracksListAdapter extends RecyclerView.Adapter<TracksListAdapter.TrackRowHolder> {
    private List<Song> tracksList;
    private Context appContext;
    private List<Integer> selectedRows; // for playlist mode
    private int selectedRow; // for music player mode
    private TracksListMode mode;

    public int getSelectedRow() {
        return selectedRow;
    }

    public void setSelectedRow(int selectedRow) {
        this.selectedRow = selectedRow;
    }

    public void toggleSelectedRow(int selectedRow) {
        if(selectedRows.contains(selectedRow))
            selectedRows.remove(selectedRows.indexOf(selectedRow));
        else
            selectedRows.add(selectedRow);
    }

    public void highlightRow(View view, boolean isSelected) {
        TextView selectedTitle = (TextView) view.findViewById(R.id.track_title);
        TextView selectedArtist = (TextView) view.findViewById(R.id.track_artist);
        if(isSelected) {
            view.setBackgroundColor(android.graphics.Color.BLACK);
            selectedTitle.setTextColor(android.graphics.Color.WHITE);
            selectedArtist.setTextColor(android.graphics.Color.WHITE);
            view.setSelected(true);
        } else {
            selectedTitle.setTextColor(android.graphics.Color.BLACK);
            selectedArtist.setTextColor(android.graphics.Color.BLACK);
            view.setBackgroundColor(android.graphics.Color.WHITE);
            view.setSelected(false);
        }
    }

    public interface OnRecyclerItemClickListener {
        void onItemClick(View view, int position);
    }
    private OnRecyclerItemClickListener mListener;

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener listener) {
        this.mListener = listener;
    }

    public TracksListAdapter(List<Song> tracksList, TracksListMode mode, Context context) {
        this.tracksList = tracksList;
        this.appContext = context;
        this.mode = mode;
    }
    @Override
    public TrackRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_row, parent, false);
        return new TrackRowHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TrackRowHolder holder, int position) {
        Song track = tracksList.get(position);
        int imageViewHt = holder.thumbnail.getHeight(); int imageViewWd = holder.thumbnail.getWidth();

        AlbumArtAsyncLoader albumArtAsyncLoader = new AlbumArtAsyncLoader();
        albumArtAsyncLoader.setAsyncTaskResponseHandler(new AsyncTaskResponseHandler() {
            @Override
            public void onResponseFromAsyncTask(Object[] params) {
                Log.i("Bitmap", params[0].toString());
                holder.thumbnail.setImageBitmap((Bitmap) params[0]);
            }
        });
        albumArtAsyncLoader.execute(new Object[]{appContext, track.getAlbumId(), imageViewHt, imageViewWd});
        holder.trackTitle.setText(track.getTitle());
        holder.trackArtist.setText(track.getArtist());
        Boolean selectionCriteria = false;
        if(mode == TracksListMode.MUSIC_PLAYER_MODE)
            selectionCriteria = (position == selectedRow);
        else if(mode == TracksListMode.PLAYLIST_MODE)
            selectionCriteria = (selectedRows != null && selectedRows.contains(position));
        highlightRow(holder.itemView, selectionCriteria);
    }

    @Override
    public int getItemCount() {
        return tracksList.size();
    }

    public class TrackRowHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView thumbnail;
        private TextView trackTitle;
        private TextView trackArtist;

        public TrackRowHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            trackTitle = (TextView) itemView.findViewById(R.id.track_title);
            trackArtist = (TextView) itemView.findViewById(R.id.track_artist);
        }

        @Override
        public void onClick(View v) {
            if(mListener != null) {
                //for music player mode, unselect the previous selection. Runs the bind view holder for this item again
                if(mode == TracksListMode.MUSIC_PLAYER_MODE)
                    notifyItemChanged(getSelectedRow());
                mListener.onItemClick(v, getAdapterPosition());
            }
        }
    }
}
