package com.thesocialplaylist.user.music.adapters.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thesocialplaylist.user.music.R;

import java.util.List;

/**
 * Created by user on 16-06-2016.
 */
public class ArtistsListAdapter extends RecyclerView.Adapter<ArtistsListAdapter.ArtistRowHolder>{
    private List<String> artistsList;
    private Context appContext;

    public ArtistsListAdapter(List<String> artistsList, Context context) {
        this.artistsList = artistsList;
        this.appContext = context;
    }

    @Override
    public ArtistRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_row, parent, false);
        return new ArtistRowHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ArtistRowHolder holder, int position) {
        holder.artistTitle.setText(artistsList.get(position));
    }

    @Override
    public int getItemCount() {
        return artistsList.size();
    }

    public class ArtistRowHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView artistTitle;
        public ArtistRowHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            artistTitle = (TextView) itemView.findViewById(R.id.artist_title);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
