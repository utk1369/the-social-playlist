package com.thesocialplaylist.user.music.dependencyinjection.modules;

import com.thesocialplaylist.user.music.sqlitedbcache.dao.ExternalLinksCacheDAO;
import com.thesocialplaylist.user.music.sqlitedbcache.dao.SongsCacheDAO;
import com.thesocialplaylist.user.music.sqlitedbcache.dao.UserRelCacheDAO;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by user on 26-09-2016.
 */

@Module
public class SQLiteCacheDAOManagerModule {

    @Provides
    @Singleton
    ExternalLinksCacheDAO provideExternalLinksCacheDAO() {
        return new ExternalLinksCacheDAO();
    }

    @Provides
    @Singleton
    SongsCacheDAO provideSongsCacheDAO() {
        return new SongsCacheDAO();
    }

    @Provides
    @Singleton
    UserRelCacheDAO provideUserRelCacheDAO() {
        return new UserRelCacheDAO();
    }
}
