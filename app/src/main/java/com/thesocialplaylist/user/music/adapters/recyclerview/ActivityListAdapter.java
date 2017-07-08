package com.thesocialplaylist.user.music.adapters.recyclerview;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;
import com.thesocialplaylist.user.music.R;
import com.thesocialplaylist.user.music.dto.LinkDTO;
import com.thesocialplaylist.user.music.dto.SocialActivityDTO;
import com.thesocialplaylist.user.music.dto.UserDTO;
import com.thesocialplaylist.user.music.enums.ActivitySourceType;
import com.thesocialplaylist.user.music.utils.ImageUtil;
import com.thesocialplaylist.user.music.utils.MessageFormatter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 24-03-2017.
 */

public class ActivityListAdapter extends RecyclerView.Adapter<ActivityListAdapter.ActivityRowHolder> {

    private List<SocialActivityDTO> activityList;
    private Map<String, UserDTO> idToUserDetailsMap;
    private Context appContext;

    public ActivityListAdapter(List<SocialActivityDTO> activityList, Map<String, UserDTO> idToUserDetailsMap, Context mContext) {
        this.activityList = activityList;
        this.idToUserDetailsMap = idToUserDetailsMap;
        this.appContext = mContext;
    }

    @Override
    public ActivityRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_row_card, parent, false);
        return new ActivityRowHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ActivityRowHolder holder, int position) {
        holder.timestamp.setText("a minute ago");
        if(idToUserDetailsMap.containsKey(activityList.get(position).getPostedBy())) {
            ImageUtil.loadImageUsingPicasso(appContext,
                    Uri.parse(idToUserDetailsMap.get(activityList.get(position).getPostedBy()).getImageUrl()),
                    holder.dpOfPoster);
        }
        holder.headline.setText(MessageFormatter.generateHeadlineForActivity(activityList.get(position), idToUserDetailsMap));
        if(activityList.get(position).getSource() == ActivitySourceType.LOCAL) {
            holder.songInfo.setVisibility(View.VISIBLE);
            holder.previewLayout.setVisibility(View.GONE);
            holder.songInfo.setText(MessageFormatter.getSongInfo(activityList.get(position).getSongMetadata()));
        } else if(activityList.get(position).getSource() == ActivitySourceType.EXTERNAL) {
            holder.songInfo.setVisibility(View.GONE);
            holder.previewLayout.setVisibility(View.VISIBLE);
            LinkDTO linkDTO = activityList.get(position).getLink();
            holder.previewTitle.setText(linkDTO.getPreviewTitle());
            if(linkDTO.getPreviewImage() != null) {
                ImageUtil.loadImageUsingPicasso(appContext,
                        Uri.parse(linkDTO.getPreviewImage()), holder.previewImage);
            } else {
                ImageUtil.loadImageUsingPicasso(appContext,
                        R.drawable.ic_audiotrack_black_48dp, holder.previewImage);
            }
            holder.previewDesc.setText(linkDTO.getPreviewDesc());
        }
        holder.caption.setText(activityList.get(position).getCaption());
        holder.caption.setVisibility( activityList.get(position).getCaption() == null
            || activityList.get(position).getCaption().equals("") ? View.GONE: View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    public class ActivityRowHolder extends RecyclerView.ViewHolder {

        private TextView timestamp;
        private TextView headline;
        private TextView songInfo;
        private TextView caption;
        private ImageView dpOfPoster;
        private LinearLayout previewLayout;
        private ImageView previewImage;
        private TextView previewTitle;
        private TextView previewDesc;

        public ActivityRowHolder(View itemView) {
            super(itemView);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            headline = (TextView) itemView.findViewById(R.id.headline);
            songInfo = (TextView) itemView.findViewById(R.id.song_info);
            caption = (TextView) itemView.findViewById(R.id.caption);
            dpOfPoster = (ImageView) itemView.findViewById(R.id.dp_poster);
            previewLayout = (LinearLayout) itemView.findViewById(R.id.preview_layout);

            previewTitle = (TextView) previewLayout.findViewById(R.id.preview_title);
            previewImage = (ImageView) previewLayout.findViewById(R.id.preview_img);
            previewDesc = (TextView) previewLayout.findViewById(R.id.preview_desc);
        }
    }

    public void updateDataSet(List<SocialActivityDTO> updatedDataSet) {
        this.activityList = updatedDataSet;
        notifyDataSetChanged();
    }
}
