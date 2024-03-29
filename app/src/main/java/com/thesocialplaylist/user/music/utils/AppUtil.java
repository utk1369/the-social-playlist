package com.thesocialplaylist.user.music.utils;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.thesocialplaylist.user.music.dto.SongDTO;
import com.thesocialplaylist.user.music.fragment.ErrorTemplateFragment;
import com.thesocialplaylist.user.music.dto.SongMetadataDTO;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by utk on 19-01-2016.
 */
public class AppUtil {

    public static String getProperty(String key, Context context) throws IOException {
        Properties properties = new Properties();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("config.properties");
        properties.load(inputStream);
        return properties.getProperty(key);
    }

    public static void replaceFragments(FragmentManager fragmentManager, int containerViewId, Fragment fragmentToReplace) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerViewId, fragmentToReplace);
        fragmentTransaction.commit();
    }

    public static void searchSongOnYoutube(SongMetadataDTO metadata, Context appContext) {
        Intent intent = new Intent(Intent.ACTION_SEARCH);
        intent.setPackage("com.google.android.youtube");
        intent.putExtra("query", metadata.getTitle() + " " + metadata.getArtist());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        appContext.startActivity(intent);
    }

    public static Fragment getErrorFragment(String message, int icon) {
        return ErrorTemplateFragment.newInstance(message, icon);
    }

    public static String getMetadataHash(SongMetadataDTO metadata) {
        String separator = "+";
        String metadataString = metadata.getTitle() + separator + metadata.getAlbum() + separator + metadata.getArtist();
        return TextUtil.encodeText(metadataString);
    }
}
