package com.thesocialplaylist.user.music.utils;

/**
 * Created by user on 12-03-2017.
 */

public class TextUtil {

    public static String convertTextToTitleCase(String text) {
        text = text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
        return text;
    }
}
