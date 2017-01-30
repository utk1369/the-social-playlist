package com.thesocialplaylist.user.music.dependencyinjection.components;

import com.thesocialplaylist.user.music.activity.MainActivity;
import com.thesocialplaylist.user.music.dependencyinjection.modules.RetrofitModule;
import com.thesocialplaylist.user.music.dependencyinjection.modules.SQLiteCacheDAOManagerModule;
import com.thesocialplaylist.user.music.dependencyinjection.modules.UserApiModule;
import com.thesocialplaylist.user.music.dependencyinjection.modules.UserDataAndRelationsManagerModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by user on 27-01-2017.
 */


@Component(modules = {UserDataAndRelationsManagerModule.class, UserApiModule.class, SQLiteCacheDAOManagerModule.class, RetrofitModule.class})
@Singleton
public interface UserDataAndRelationsManagerComponent {
    void inject(MainActivity mainActivity);
}
