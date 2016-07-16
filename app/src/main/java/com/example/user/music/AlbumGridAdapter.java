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

import com.example.user.music.models.Album;
import com.example.user.music.utils.AlbumArtAsyncLoader;
import com.example.user.music.utils.AsyncTaskResponseHandler;

import java.util.List;

/**
 * Created by utk on 16-05-2016.
 */
public class AlbumGridAdapter extends RecyclerView.Adapter<AlbumGridAdapter.AlbumGridElementHolder>{
    private List<Album> albumsList;
    private Context appContext;
    private GridClickListener mListener;


    public AlbumGridAdapter(List<Album> albumsList, Context mContext) {
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
        Album album = albumsList.get(position);
        int imageViewHt = holder.albumArt.getHeight(); int imageViewWd = holder.albumArt.getWidth();

        AlbumArtAsyncLoader albumArtAsyncLoader = new AlbumArtAsyncLoader();
        albumArtAsyncLoader.setAsyncTaskResponseHandler(new AsyncTaskResponseHandler() {
            @Override
            public void onResponseFromAsyncTask(Object[] params) {
                Log.i("Bitmap", params[0].toString());
                holder.albumArt.setImageBitmap((Bitmap) params[0]);
            }
        });
        albumArtAsyncLoader.execute(new Object[]{appContext, album.getAlbumId(), imageViewHt, imageViewWd});
        holder.albumTitle.setText(album.getAlbumName());
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
