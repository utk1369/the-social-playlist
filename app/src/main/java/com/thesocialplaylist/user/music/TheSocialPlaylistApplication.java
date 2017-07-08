package com.thesocialplaylist.user.music;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

import com.crashlytics.android.Crashlytics;
import com.thesocialplaylist.user.music.dependencyinjection.components.DaggerMusicLibraryManagerComponent;
import com.thesocialplaylist.user.music.dependencyinjection.components.DaggerUserDataAndRelationsManagerComponent;
import com.thesocialplaylist.user.music.dependencyinjection.components.MusicLibraryManagerComponent;
import com.thesocialplaylist.user.music.dependencyinjection.components.UserDataAndRelationsManagerComponent;
import com.thesocialplaylist.user.music.dependencyinjection.modules.AppModule;
import com.thesocialplaylist.user.music.dependencyinjection.modules.RetrofitModule;
import com.thesocialplaylist.user.music.utils.AppUtil;

import io.fabric.sdk.android.Fabric;
import java.io.IOException;

/**
 * Created by user on 02-10-2016.
 */

public class TheSocialPlaylistApplication extends Application {

    private String BASE_URL;

    private MusicLibraryManagerComponent musicLibraryManagerComponent;

    private UserDataAndRelationsManagerComponent userDataAndRelationsManagerComponent;

    public MusicLibraryManagerComponent getMusicLibraryManagerComponent() {
        return musicLibraryManagerComponent;
    }

    public UserDataAndRelationsManagerComponent getUserDataAndRelationsManagerComponent() {
        return userDataAndRelationsManagerComponent;
    }

    @Override
    public void onCreate() {
        Fabric.with(this, new Crashlytics());
        try {
            BASE_URL = AppUtil.getProperty("api.base.url", getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
        musicLibraryManagerComponent = DaggerMusicLibraryManagerComponent
                .builder()
                .appModule(new AppModule(this))
                .build();

        userDataAndRelationsManagerComponent = DaggerUserDataAndRelationsManagerComponent
                .builder()
                .retrofitModule(new RetrofitModule(BASE_URL))
                .appModule(new AppModule(this))
                .build();

        ActiveAndroid.initialize(this);
    }
}
