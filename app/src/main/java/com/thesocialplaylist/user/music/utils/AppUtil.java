package com.thesocialplaylist.user.music.utils;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.thesocialplaylist.user.music.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by utk on 19-01-2016.
 */
public class AppUtil {

    public static Drawable getDrawableResource(Context context, int drawableId, Resources.Theme theme)  {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return context.getResources().getDrawable(drawableId, theme);
        return context.getResources().getDrawable(drawableId);
    }

    public static String getProperty(String key, Context context) throws IOException {
        Properties properties = new Properties();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("config.properties");
        properties.load(inputStream);
        return properties.getProperty(key);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int targetHt, int targetWd) {
        int inSampleSize = 1;
        int originalHt = options.outHeight; int originalWd = options.outWidth;
        if(originalHt < targetHt || originalWd < targetWd)
            return inSampleSize;

        while((originalHt / inSampleSize) > targetHt && (originalWd / inSampleSize) > targetWd) {
            inSampleSize *= 2;
        }
        return inSampleSize * 2;
    }

    private static Uri getAlbumArtUri(long albumId) {
        Uri albumsUri = Uri.parse("content://media/external/audio/albumart");
        Uri albumArtUri = ContentUris.withAppendedId(albumsUri, albumId);
        return albumArtUri;
    }

    public static void loadImageUsingPicasso(Context context, Uri uri, ImageView imageView, Drawable placeholder, Drawable errorPlaceholder) {
        Picasso.with(context)
                .load(uri)
                .fit()
                .placeholder(placeholder)
                .error(errorPlaceholder)
                .into(imageView);
    }

    public static void loadAlbumArt(Context context, long albumId, ImageView albumArtImageView) {
        loadImageUsingPicasso(context, getAlbumArtUri(albumId),
                albumArtImageView,
                AppUtil.getDrawableResource(context, R.drawable.ic_audiotrack_black_48dp, context.getTheme()),
                AppUtil.getDrawableResource(context, R.drawable.ic_audiotrack_black_48dp, context.getTheme())
        );
    }
}
