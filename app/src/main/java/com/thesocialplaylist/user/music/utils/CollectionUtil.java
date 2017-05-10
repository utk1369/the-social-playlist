package com.thesocialplaylist.user.music.utils;

import java.util.List;

/**
 * Created by user on 01-05-2017.
 */

public class CollectionUtil {

    public static <T> List<T> findAndRemoveFromList(List<T> haystack, T needle) {
        for(T element: haystack) {
            if(element.equals(needle)) {
                haystack.remove(element);
                break;
            }
        }
        return haystack;
    }
}
