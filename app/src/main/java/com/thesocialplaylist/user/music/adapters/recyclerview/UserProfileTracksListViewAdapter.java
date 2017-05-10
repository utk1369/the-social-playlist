package com.thesocialplaylist.user.music.adapters.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.thesocialplaylist.user.music.R;
import com.thesocialplaylist.user.music.dto.SongDTO;
import com.thesocialplaylist.user.music.dto.UserDTO;
import com.thesocialplaylist.user.music.utils.ImageUtil;

import java.util.List;

/**
 * Created by user on 08-05-2017.
 */

public class UserProfileTracksListViewAdapter extends RecyclerView.Adapter<UserProfileTracksListViewAdapter.UserProfileTrackRowHolder> {

    private List<SongDTO> tracksList;
    private Context appContext;
    private OnTrackInfoClickListener onTrackInfoClickListener;
    private OnLikeButtonClickListener onLikeButtonClickListener;
    private UserDTO appUserDetails;


    public interface OnTrackInfoClickListener {
        public void onTrackInfoClick(int position, List<SongDTO> tracksList);
    }

    public interface OnLikeButtonClickListener {
        public void onLikeButtonClick(int position, List<SongDTO> tracksList);
    }

    public UserProfileTracksListViewAdapter(List<SongDTO> tracksList, Context appContext,
                                            OnTrackInfoClickListener onTrackInfoClickListener, OnLikeButtonClickListener onLikeButtonClickListener,
                                            UserDTO appUserDetails) {
        this.tracksList = tracksList;
        this.appContext = appContext;
        this.onTrackInfoClickListener = onTrackInfoClickListener;
        this.onLikeButtonClickListener = onLikeButtonClickListener;
        this.appUserDetails = appUserDetails;
    }

    @Override
    public UserProfileTrackRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_row, parent, false);
        return new UserProfileTrackRowHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UserProfileTrackRowHolder holder, int position) {
        final SongDTO track = tracksList.get(position);
        holder.trackTitle.setText(track.getMetadata().getTitle());
        holder.trackArtist.setText(track.getMetadata().getArtist());
        ImageUtil.loadAlbumArt(appContext, track.getMetadata().getAlbumId(), holder.thumbnail);
        holder.actionButtonsLayout.setVisibility(View.VISIBLE);
        holder.noOfLikes.setText(track.getLikes().size() + "");
        if(track.getLikes().contains(appUserDetails.getId())) {
            ImageUtil.colorImageViewDrawable(appContext, holder.likeButton, R.color.colorPrimary);
        } else {
            ImageUtil.colorImageViewDrawable(appContext, holder.likeButton, R.color.secondaryTextColor);
        }
        holder.noOfShares.setText(track.getSocialActivities().size() + "");
        holder.userRating.setRating(track.getRating() == null ? (float) 0.0 : track.getRating().floatValue());
        holder.itemView.setBackgroundColor(android.graphics.Color.WHITE);
    }

    @Override
    public int getItemCount() {
        return tracksList.size();
    }

    public class UserProfileTrackRowHolder extends RecyclerView.ViewHolder {
        private ImageView thumbnail;
        private TextView trackTitle;
        private TextView trackArtist;
        private ImageButton options;

        //Action Buttons
        private LinearLayout actionButtonsLayout;
        private TextView noOfLikes;
        private ImageView likeButton;
        private TextView noOfShares;
        private ImageView shareButton;
        private RatingBar userRating;

        public UserProfileTrackRowHolder(final View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            trackTitle = (TextView) itemView.findViewById(R.id.track_title);
            trackArtist = (TextView) itemView.findViewById(R.id.track_artist);
            options = (ImageButton) itemView.findViewById(R.id.options);

            actionButtonsLayout = (LinearLayout) itemView.findViewById(R.id.action_btns);
            noOfLikes = (TextView) itemView.findViewById(R.id.no_of_likes);
            likeButton = (ImageView)  itemView.findViewById(R.id.like_btn);
            noOfShares = (TextView) itemView.findViewById(R.id.no_of_shares);
            shareButton = (ImageView) itemView.findViewById(R.id.share_btn);
            userRating = (RatingBar) itemView.findViewById(R.id.user_rating);

            bindListeners(itemView);
        }

        private void bindListeners(final View itemView) {
            trackTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onTrackInfoClickListener.onTrackInfoClick(getAdapterPosition(), tracksList);
                }
            });

            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onLikeButtonClickListener.onLikeButtonClick(getAdapterPosition(), tracksList);
                }
            });

            //options.setOnClickListener();
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
