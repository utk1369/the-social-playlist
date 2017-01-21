package com.thesocialplaylist.user.music;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.thesocialplaylist.user.music.dependencyinjection.components.DaggerMusicLibraryManagerComponent;
import com.thesocialplaylist.user.music.dependencyinjection.components.DaggerRetrofitComponent;
import com.thesocialplaylist.user.music.dependencyinjection.components.MusicLibraryManagerComponent;
import com.thesocialplaylist.user.music.dependencyinjection.components.RetrofitComponent;
import com.thesocialplaylist.user.music.dependencyinjection.modules.AppModule;
import com.thesocialplaylist.user.music.dependencyinjection.modules.RetrofitModule;
import com.thesocialplaylist.user.music.utils.AppUtil;

import java.io.IOException;

/**
 * Created by user on 02-10-2016.
 */

public class TheSocialPlaylistApplication extends Application {

    private String BASE_URL;

    private MusicLibraryManagerComponent musicLibraryManagerComponent;

    private RetrofitComponent retrofitComponent;

    public MusicLibraryManagerComponent getMusicLibraryManagerComponent() {
        return musicLibraryManagerComponent;
    }

    public RetrofitComponent getRetrofitComponent() {
        return retrofitComponent;
    }

    @Override
    public void onCreate() {
        try {
            BASE_URL = AppUtil.getProperty("api.base.url", getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
        musicLibraryManagerComponent = DaggerMusicLibraryManagerComponent
                .builder()
                .appModule(new AppModule(this))
                .build();

        retrofitComponent = DaggerRetrofitComponent
                .builder()
                .retrofitModule(new RetrofitModule(BASE_URL))
                .build();

        ActiveAndroid.initialize(this);
    }
}
