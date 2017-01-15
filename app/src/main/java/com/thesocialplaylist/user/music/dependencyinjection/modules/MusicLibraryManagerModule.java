package com.thesocialplaylist.user.music.dependencyinjection.modules;

import android.app.Application;

import com.thesocialplaylist.user.music.manager.MusicLibraryManager;
import com.thesocialplaylist.user.music.sqlitedbcache.SQLiteCacheManager;

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
    MusicLibraryManager provideMusicLibraryManager(SQLiteCacheManager sqLiteCacheManager, Application app) {
        return new MusicLibraryManager(sqLiteCacheManager, app);
    }
}
