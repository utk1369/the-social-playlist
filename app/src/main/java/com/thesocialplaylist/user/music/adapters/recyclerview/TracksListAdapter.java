package com.thesocialplaylist.user.music.adapters.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thesocialplaylist.user.music.R;
import com.thesocialplaylist.user.music.activity.musicplayer.youtube.YoutubeLinker;
import com.thesocialplaylist.user.music.dto.ExternalLinksDTO;
import com.thesocialplaylist.user.music.dto.SongDTO;
import com.thesocialplaylist.user.music.enums.ExternalLinkType;
import com.thesocialplaylist.user.music.enums.TracksListMode;
import com.thesocialplaylist.user.music.utils.AppUtil;

import java.util.List;

/**
 * Created by user on 13-04-2016.
 */
public class TracksListAdapter extends RecyclerView.Adapter<TracksListAdapter.TrackRowHolder> {
    private List<SongDTO> tracksList;
    private Context appContext;
    private List<Integer> selectedRows; // for playlist mode
    private int selectedRow; // for music player mode
    private TracksListMode mode;

    private OnRecyclerItemClickListener mListener;

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener listener) {
        this.mListener = listener;
    }

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

    public TracksListAdapter(List<SongDTO> tracksList, TracksListMode mode, Context context) {
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
        final SongDTO track = tracksList.get(position);
        holder.trackTitle.setText(track.getMetadata().getTitle());
        holder.trackArtist.setText(track.getMetadata().getArtist());
        if(mode.equals(TracksListMode.USER_PROFILE_MODE)) {
            AppUtil.loadImageUsingPicasso(appContext, null, holder.thumbnail,
                    AppUtil.getDrawableResource(appContext, R.drawable.ic_headset_black_36dp, appContext.getTheme()),
                    AppUtil.getDrawableResource(appContext, R.drawable.ic_headset_black_36dp, appContext.getTheme()));
        } else {
            AppUtil.loadAlbumArt(appContext, track.getMetadata().getAlbumId(), holder.thumbnail);
        }
        Boolean selectionCriteria = false;
        if(mode.equals(TracksListMode.MUSIC_PLAYER_MODE))
            selectionCriteria = (position == selectedRow);
        else if(mode.equals(TracksListMode.PLAYLIST_MODE))
            selectionCriteria = (selectedRows != null && selectedRows.contains(position));
        highlightRow(holder.itemView, selectionCriteria);
        if(track.getExternalLinks() != null && track.getExternalLinks().size() > 0) {
            Picasso.with(appContext)
                    .load(R.drawable.youtube_128)
                    .fit()
                    .into(holder.youtubeLinkFlag);
        } else {
            Picasso.with(appContext)
                    .load(R.drawable.ic_audiotrack_black_24dp)
                    .fit()
                    .into(holder.youtubeLinkFlag);
        }
    }

    @Override
    public int getItemCount() {
        return tracksList.size();
    }

    public class TrackRowHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView thumbnail;
        private TextView trackTitle;
        private TextView trackArtist;
        private ImageButton options;
        private ImageView youtubeLinkFlag;

        public TrackRowHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            trackTitle = (TextView) itemView.findViewById(R.id.track_title);
            trackArtist = (TextView) itemView.findViewById(R.id.track_artist);
            youtubeLinkFlag = (ImageView) itemView.findViewById(R.id.youtube_link_flag);
            options = (ImageButton) itemView.findViewById(R.id.options);
            options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                    popupMenu.inflate(R.menu.menu_track_row);
                    popupMenu.setOnMenuItemClickListener(new PopupMenuItemClickListener());
                    popupMenu.show();
                }
            });
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

        private class PopupMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.youtube_link) {
                    Intent youtubeLinkIntent = new Intent(appContext, YoutubeLinker.class);
                    youtubeLinkIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    youtubeLinkIntent.putExtra("KEYWORD",
                            tracksList.get(getAdapterPosition()).getMetadata().getTitle()
                                    + "+" + tracksList.get(getAdapterPosition()).getMetadata().getArtist());
                    youtubeLinkIntent.putExtra("SONG_ID", tracksList.get(getAdapterPosition()).getId());
                    List<ExternalLinksDTO> externalLinksDTOs = tracksList.get(getAdapterPosition()).getExternalLinks();
                    if(externalLinksDTOs != null) {
                        for(ExternalLinksDTO externalLinksDTO: externalLinksDTOs) {
                            if(externalLinksDTO.getLinkType().equals(ExternalLinkType.YOUTUBE)) {
                                youtubeLinkIntent.putExtra("LINKED_VIDEO_ID", externalLinksDTO.getId());
                                break;
                            }
                        }
                    }
                    appContext.startActivity(youtubeLinkIntent);
                    return true;
                }
                return false;
            }
        }


    }
}
