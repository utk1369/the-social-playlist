package com.thesocialplaylist.user.music.utils;

import android.util.Base64;

/**
 * Created by user on 12-03-2017.
 */

public class TextUtil {

    public static String convertTextToTitleCase(String text) {
        text = text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
        return text;
    }

    public static String encodeText(String text) {
        return Base64.encodeToString(text.getBytes(), Base64.DEFAULT);
    }
}
