package com.thesocialplaylist.user.music.dependencyinjection.modules;

import com.thesocialplaylist.user.music.sqlitedbcache.SQLiteCacheManager;
import com.thesocialplaylist.user.music.sqlitedbcache.dao.ExternalLinksCacheDAO;
import com.thesocialplaylist.user.music.sqlitedbcache.dao.SongsCacheDAO;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by user on 26-09-2016.
 */

@Module
public class SQLiteCacheManagerModule {

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
    SQLiteCacheManager provideSQLiteCacheManager(SongsCacheDAO songsCacheDAO,
                                                 ExternalLinksCacheDAO externalLinksCacheDAO) {
        return new SQLiteCacheManager(songsCacheDAO, externalLinksCacheDAO);
    }
}
