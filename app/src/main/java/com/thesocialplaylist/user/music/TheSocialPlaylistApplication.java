package com.thesocialplaylist.user.music;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.thesocialplaylist.user.music.dependencyinjection.components.DaggerMusicLibraryManagerComponent;
import com.thesocialplaylist.user.music.dependencyinjection.components.MusicLibraryManagerComponent;
import com.thesocialplaylist.user.music.dependencyinjection.modules.AppModule;

/**
 * Created by user on 02-10-2016.
 */

public class TheSocialPlaylistApplication extends Application {

    private MusicLibraryManagerComponent musicLibraryManagerComponent;

    public MusicLibraryManagerComponent getMusicLibraryManagerComponent() {
        return musicLibraryManagerComponent;
    }

    @Override
    public void onCreate() {
        musicLibraryManagerComponent = DaggerMusicLibraryManagerComponent
                .builder()
                .appModule(new AppModule(this))
                .build();
        ActiveAndroid.initialize(this);
    }
}
