package com.thesocialplaylist.user.music.adapters.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thesocialplaylist.user.music.R;
import com.thesocialplaylist.user.music.dto.AlbumDTO;
import com.thesocialplaylist.user.music.utils.AppUtil;

import java.util.List;

/**
 * Created by utk on 16-05-2016.
 */
public class AlbumGridAdapter extends RecyclerView.Adapter<AlbumGridAdapter.AlbumGridElementHolder>{
    private List<AlbumDTO> albumsList;
    private Context appContext;
    private GridClickListener mListener;


    public AlbumGridAdapter(List<AlbumDTO> albumsList, Context mContext) {
        this.albumsList = albumsList;
        this.appContext = mContext;
    }

    public interface GridClickListener {
        void onGridItemClick(View view, int position);
    }

    public void setGridClickListener(GridClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public AlbumGridElementHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_grid, parent, false);
        return new AlbumGridElementHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AlbumGridElementHolder holder, int position) {
        AlbumDTO albumDTO = albumsList.get(position);
        AppUtil.loadAlbumArt(appContext, albumDTO.getAlbumId(), holder.albumArt);
        holder.albumTitle.setText(albumDTO.getAlbumName());
    }

    @Override
    public int getItemCount() {
        return albumsList.size();
    }

    public class AlbumGridElementHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView albumArt;
        private TextView albumTitle;

        public AlbumGridElementHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            albumArt = (ImageView) itemView.findViewById(R.id.album_art);
            albumTitle = (TextView) itemView.findViewById(R.id.album_title);
        }

        @Override
        public void onClick(View v) {
            if(mListener != null) {
                mListener.onGridItemClick(v, getAdapterPosition());
            }
        }
    }
}
