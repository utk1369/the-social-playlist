package com.example.user.music.utils;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import com.example.user.music.R;
import com.example.user.music.models.Album;
import com.example.user.music.models.Song;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by utk on 19-01-2016.
 */
public class AppUtil {

    public static List<Song> cursorToListOfSongs(Cursor cursor) {
        List<Song> songs = new ArrayList<Song>();
        while(cursor.moveToNext()) {
            songs.add(new Song(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    Long.parseLong(cursor.getString(6))
            ));
        }
        return songs;
    }

    public static List<Song> getSongsAsList(Context context, String filterCriteria, String[] filterParams) {
        ContentResolver contentResolver = context.getContentResolver();
        String projections[] = new String[] {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ALBUM
        };
        String selections = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        if(filterCriteria != null) {
            selections += " AND " + filterCriteria;
        }

        Cursor cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projections,
                selections,
                filterParams,
                null
        );
        return cursorToListOfSongs(cursor);
    }

    public static List<Album> cursorToListOfAlbums(Cursor cursor) {
        List<Album> albums = new ArrayList<Album>();
        while(cursor.moveToNext()) {
            Album album = new Album();
            album.setAlbumId(Long.parseLong(cursor.getString(0)));
            album.setAlbumName(cursor.getString(1));
            albums.add(album);
        }
        return albums;
    }

    public static List<Album> getAllAlbumsAsList(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        String projections[] = new String[] {
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM
        };
        Cursor cursor = contentResolver.query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                projections,
                null,
                null,
                null
        );
        return cursorToListOfAlbums(cursor);
    }

    public static Bitmap getAlbumArt(Context context, long albumId, int targetHt, int targetWd) throws FileNotFoundException {
        Uri albumsUri = Uri.parse("content://media/external/audio/albumart");
        Uri albumArtUri = ContentUris.withAppendedId(albumsUri, albumId);
        return getImageInLowerResolution(context, albumArtUri, targetHt, targetWd);
    }

    public static Bitmap getImageInLowerResolution(Context context, Uri albumArtUri, int targetHt, int targetWd) throws FileNotFoundException {
        Bitmap albumArt = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(albumArtUri, "r");
        if(pfd != null) {
            FileDescriptor fd = pfd.getFileDescriptor();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(fd, null, options);
            options.inSampleSize = calculateInSampleSize(options, targetHt, targetWd);
            options.inJustDecodeBounds = false;
            albumArt = BitmapFactory.decodeFileDescriptor(fd, null, options);
            pfd = null;
            fd = null;
        }
        return albumArt;
    }

    public static Bitmap getDefaultAlbumArt(Context context) {
        Bitmap albumArt = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_audiotrack_black_48dp);
        return albumArt;
    }



    private static int calculateInSampleSize(BitmapFactory.Options options, int targetHt, int targetWd) {
        int inSampleSize = 1;
        int originalHt = options.outHeight; int originalWd = options.outWidth;
        if(originalHt < targetHt || originalWd < targetWd)
            return inSampleSize;

        while((originalHt / inSampleSize) > targetHt && (originalWd / inSampleSize) > targetWd) {
            inSampleSize *= 2;
        }
        return inSampleSize * 2;
    }
}
