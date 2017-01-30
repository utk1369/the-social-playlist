package com.thesocialplaylist.user.music.dependencyinjection.modules;

import android.app.Application;

import com.thesocialplaylist.user.music.manager.MusicLibraryManager;
import com.thesocialplaylist.user.music.sqlitedbcache.dao.ExternalLinksCacheDAO;
import com.thesocialplaylist.user.music.sqlitedbcache.dao.SongsCacheDAO;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by user on 17-10-2016.
 */

@Module
public class MusicLibraryManagerModule {
    @Provides
    @Singleton
    MusicLibraryManager provideMusicLibraryManager(SongsCacheDAO songsCacheDAO, ExternalLinksCacheDAO externalLinksCacheDAO, Application app) {
        return new MusicLibraryManager(songsCacheDAO, externalLinksCacheDAO, app);
    }
}
