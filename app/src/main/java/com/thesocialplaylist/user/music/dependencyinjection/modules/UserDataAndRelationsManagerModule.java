package com.thesocialplaylist.user.music.dependencyinjection.modules;

import com.thesocialplaylist.user.music.api.declaration.UserApi;
import com.thesocialplaylist.user.music.manager.UserDataAndRelationsManager;
import com.thesocialplaylist.user.music.sqlitedbcache.dao.UserRelCacheDAO;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by user on 27-01-2017.
 */

@Module
public class UserDataAndRelationsManagerModule {

    @Provides
    @Singleton
    public UserDataAndRelationsManager provideUserDataAndRelationsManager(UserApi userApi, UserRelCacheDAO userRelCacheDAO) {
        return new UserDataAndRelationsManager(userApi, userRelCacheDAO);
    }
}
