package com.example.user.music.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import java.io.FileNotFoundException;

/**
 * Created by user on 25-04-2016.
 */
public class AlbumArtAsyncLoader extends AsyncTask<Object, Object, Bitmap> {

    public AsyncTaskResponseHandler asyncTaskResponseHandler = null;

    public void setAsyncTaskResponseHandler(AsyncTaskResponseHandler asyncTaskResponseHandler) {
        this.asyncTaskResponseHandler = asyncTaskResponseHandler;
    }

    @Override
    protected Bitmap doInBackground(Object[] params) {
        Context appContext = (Context) params[0];
        long albumId = (long) params[1];
        int targetHt = (int) params[2];
        int targetWd = (int) params[3];
        Bitmap albumImg = null;
        try {
            albumImg = AppUtil.getAlbumArt(appContext, albumId, targetHt, targetWd);
        } catch (FileNotFoundException e) {
            albumImg = AppUtil.getDefaultAlbumArt(appContext);
        }
        return albumImg;
    }

    @Override
    protected void onPostExecute(Bitmap albumArt) {
        Object[] result = new Object[] {albumArt};
        asyncTaskResponseHandler.onResponseFromAsyncTask(result);
    }
}
