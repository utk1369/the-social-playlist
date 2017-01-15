package com.thesocialplaylist.user.music.utils;

import android.content.Context;

import com.thesocialplaylist.user.music.R;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;

import java.io.IOException;

/**
 * Created by user on 11-08-2016.
 */
public class YoutubeUtil {

    public static YouTube getNewInstance(Context appContext) {
        return new YouTube.Builder(
                    new NetHttpTransport(),
                    new GsonFactory(),
                    new HttpRequestInitializer() {
                        @Override
                        public void initialize(HttpRequest request) throws IOException {

                        }
                    }
                ).setApplicationName(appContext.getString(R.string.app_name)).build();
    }

    public static YouTube.Search.List buildSearchQuery(YouTube youtube,
                                                       String apiKey, Long maxResults,
                                                       String keywords) throws IOException {
        YouTube.Search.List query = youtube.search().list("id, snippet");
        query.setKey(apiKey);
        query.setType("video");
        query.setFields("items(id/videoId,snippet(thumbnails/default/url,title))");
        query.setOrder("viewCount");
        query.setMaxResults(maxResults);
        query.setQ(keywords);
        query.getVideoSyndicated();
        return query;
    }
}
