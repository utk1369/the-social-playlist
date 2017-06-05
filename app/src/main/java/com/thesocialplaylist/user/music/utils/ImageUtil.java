package com.thesocialplaylist.user.music.utils;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.thesocialplaylist.user.music.R;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;

/**
 * Created by user on 12-03-2017.
 */

public class ImageUtil {

    public static void loadAlbumArt(Context context, Long albumId, ImageView albumArtImageView) {
        loadImageUsingPicasso(context, (albumId == null ? null : getAlbumArtUri(albumId)),
                albumArtImageView,
                getDrawableResource(context, R.drawable.ic_audiotrack_black_48dp, context.getTheme()),
                getDrawableResource(context, R.drawable.ic_audiotrack_black_48dp, context.getTheme())
        );
    }

    public static void loadImageUsingPicasso(Context context, Uri uri, ImageView imageView, Drawable placeholder, Drawable errorPlaceholder) {
        Picasso.with(context)
                .load(uri)
                .fit()
                .placeholder(placeholder)
                .error(errorPlaceholder)
                .into(imageView);
    }

    public static void loadImageUsingPicasso(Context context, Uri uri, ImageView imageView) {
        Picasso.with(context)
                .load(uri)
                .fit()
                .into(imageView);
    }

    public static void loadImageUsingPicasso(Context context, int resourceId, ImageView imageView) {
        Picasso.with(context)
                .load(resourceId)
                .fit()
                .into(imageView);
    }

    private static Uri getAlbumArtUri(Long albumId) {
        Uri albumsUri = Uri.parse("content://media/external/audio/albumart");
        Uri albumArtUri = ContentUris.withAppendedId(albumsUri, albumId);
        return albumArtUri;
    }

    public static Drawable getDrawableResource(Context context, int drawableId, Resources.Theme theme)  {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return context.getResources().getDrawable(drawableId, theme);
        return context.getResources().getDrawable(drawableId);
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

    public static Bitmap getAlbumArtAsBitmap(Context appContext, long albumId, Bitmap defaultImage) {
        Bitmap albumArt = null;

        try {
            ParcelFileDescriptor pfd = appContext.getContentResolver()
                    .openFileDescriptor(getAlbumArtUri(albumId), "r");
            if(pfd != null) {
                FileDescriptor fd = pfd.getFileDescriptor();
                albumArt = BitmapFactory.decodeFileDescriptor(fd);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return defaultImage;
        }
        return albumArt;
    }

    public static Bitmap blurImage(Context appContext, Bitmap inputImage, float blurRadius) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            RenderScript rScript = RenderScript.create(appContext);
            Allocation inputImageAlloc = Allocation.createFromBitmap(rScript, inputImage);
            ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(rScript, inputImageAlloc.getElement());
            scriptIntrinsicBlur.setRadius(blurRadius);
            scriptIntrinsicBlur.setInput(inputImageAlloc);

            Bitmap resultImage = Bitmap.createBitmap(inputImage.getWidth(), inputImage.getHeight(), inputImage.getConfig());
            Allocation resultImageAlloc = Allocation.createFromBitmap(rScript, resultImage);
            scriptIntrinsicBlur.forEach(resultImageAlloc);
            resultImageAlloc.copyTo(resultImage);

            return resultImage;
        }
        return null;
    }

    public static void colorImageViewDrawable(Context appContext, ImageView imageView,
                                              int color) {
        imageView.setColorFilter(ContextCompat.getColor(appContext, color));
    }
}
