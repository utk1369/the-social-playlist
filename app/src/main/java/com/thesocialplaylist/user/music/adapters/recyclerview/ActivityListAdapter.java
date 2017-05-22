package com.thesocialplaylist.user.music.adapters.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thesocialplaylist.user.music.R;
import com.thesocialplaylist.user.music.dto.SocialActivityDTO;
import com.thesocialplaylist.user.music.dto.SongMetadataDTO;
import com.thesocialplaylist.user.music.dto.UserDTO;
import com.thesocialplaylist.user.music.enums.SocialActivityType;
import com.thesocialplaylist.user.music.utils.MessageFormatter;

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
    public void onBindViewHolder(ActivityRowHolder holder, int position) {
        holder.timestamp.setText("a minute ago");
        holder.headline.setText(MessageFormatter.generateHeadlineForActivity(activityList.get(position), idToUserDetailsMap));
        holder.songInfo.setText(MessageFormatter.getSongInfo(activityList.get(position).getSongMetadata()));
        holder.caption.setText(activityList.get(position).getCaption());
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

        public ActivityRowHolder(View itemView) {
            super(itemView);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            headline = (TextView) itemView.findViewById(R.id.headline);
            songInfo = (TextView) itemView.findViewById(R.id.song_info);
            caption = (TextView) itemView.findViewById(R.id.caption);
        }
    }

    public void updateDataSet(List<SocialActivityDTO> updatedDataSet) {
        this.activityList = updatedDataSet;
        notifyDataSetChanged();
    }
}
