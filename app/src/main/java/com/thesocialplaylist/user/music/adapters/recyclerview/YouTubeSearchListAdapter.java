package com.thesocialplaylist.user.music.adapters.recyclerview;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thesocialplaylist.user.music.R;
import com.thesocialplaylist.user.music.dto.ExternalLinksDTO;

import java.util.List;

/**
 * Created by user on 24-08-2016.
 */
public class YouTubeSearchListAdapter extends RecyclerView.Adapter<YouTubeSearchListAdapter.YouTubeSearchRowHolder> {

    private List<ExternalLinksDTO> youtubeVideoDTOs;
    private Context appContext;
    private OnItemClickListener mClickListener;
    private OnItemSelectListener mSelectListener;

    private Integer selectedVideoPos = null;
    private Integer playingVideoPos = null;

    public void setSelectedVideoPos(Integer selectedVideoPos) {
        this.selectedVideoPos = selectedVideoPos;
    }

    public void setPlayingVideoPos(Integer playingVideoPos) {
        this.playingVideoPos = playingVideoPos;
    }

    public void setOnItemClickListener(OnItemClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    public void setOnItemSelectListener(OnItemSelectListener mSelectListener) {
        this.mSelectListener = mSelectListener;
    }

    public interface OnItemClickListener {
        public void onItemClick(int pos);
    }

    public interface OnItemSelectListener {
        public void onItemSelect(int pos);
    }



    public YouTubeSearchListAdapter(List<ExternalLinksDTO> youtubeVideoDTOs, Context context) {
        this.youtubeVideoDTOs = youtubeVideoDTOs;
        this.appContext = context;
    }

    @Override
    public YouTubeSearchRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.youtube_search_result_row, parent, false);
        return new YouTubeSearchRowHolder(itemView);
    }

    @Override
    public void onBindViewHolder(YouTubeSearchRowHolder holder, int position) {
        ExternalLinksDTO video = youtubeVideoDTOs.get(position);
        holder.videoTitle.setText(video.getTitle());
        Picasso.with(appContext)
                .load(video.getThumbnailUrl())
                .fit()
                .into(holder.videoThumbnail);

        highlightRow(holder.itemView, (playingVideoPos == null) ? false : (position == playingVideoPos));
        holder.isSelected.setChecked((selectedVideoPos == null) ? false : (position == selectedVideoPos));
    }

    @Override
    public int getItemCount() {
        return youtubeVideoDTOs.size();
    }

    private void highlightRow(View v, boolean isPlaying) {
        if(isPlaying) {
            v.setBackgroundColor(Color.LTGRAY);
        } else {
            v.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    public class YouTubeSearchRowHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView videoThumbnail;
        private TextView videoTitle;
        private RadioButton isSelected;

        public YouTubeSearchRowHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            videoThumbnail = (ImageView) itemView.findViewById(R.id.video_thumbnail);
            videoTitle = (TextView) itemView.findViewById(R.id.video_title);
            isSelected = (RadioButton) itemView.findViewById(R.id.is_selected);
            isSelected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(selectedVideoPos != null)
                        notifyItemChanged(selectedVideoPos);
                    mSelectListener.onItemSelect(getAdapterPosition());
                }
            });
        }

        @Override
        public void onClick(View v) {
            Integer prevPlayingVideo = playingVideoPos;
            playingVideoPos = getAdapterPosition();
            if(prevPlayingVideo != null)
                notifyItemChanged(prevPlayingVideo);
            highlightRow(v, true);
            mClickListener.onItemClick(playingVideoPos);
        }
    }
}
