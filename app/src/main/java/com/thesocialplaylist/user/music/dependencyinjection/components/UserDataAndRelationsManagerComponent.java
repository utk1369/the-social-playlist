package com.thesocialplaylist.user.music.dependencyinjection.components;

import com.thesocialplaylist.user.music.activity.MainActivity;
import com.thesocialplaylist.user.music.activity.UserListActivity;
import com.thesocialplaylist.user.music.activity.UserProfileActivity;
import com.thesocialplaylist.user.music.activity.musicplayer.SongShareActivity;
import com.thesocialplaylist.user.music.dependencyinjection.modules.AppModule;
import com.thesocialplaylist.user.music.dependencyinjection.modules.MusicLibraryManagerModule;
import com.thesocialplaylist.user.music.dependencyinjection.modules.RetrofitModule;
import com.thesocialplaylist.user.music.dependencyinjection.modules.SQLiteCacheDAOManagerModule;
import com.thesocialplaylist.user.music.dependencyinjection.modules.UserApiModule;
import com.thesocialplaylist.user.music.dependencyinjection.modules.UserDataAndRelationsManagerModule;
import com.thesocialplaylist.user.music.fragment.ActivitiesFragment;
import com.thesocialplaylist.user.music.fragment.FriendsListFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by user on 27-01-2017.
 */


@Component(modules = {
        UserDataAndRelationsManagerModule.class,
        UserApiModule.class,
        SQLiteCacheDAOManagerModule.class,
        RetrofitModule.class,
        MusicLibraryManagerModule.class,
        SQLiteCacheDAOManagerModule.class,
        AppModule.class
    })
@Singleton
public interface UserDataAndRelationsManagerComponent {
    void inject(MainActivity mainActivity);
    void inject(UserProfileActivity userProfileActivity);
    void inject(SongShareActivity songShareActivity);
    void inject(UserListActivity userListActivity);
    //avoid injecting this into fragment
    void inject(ActivitiesFragment activitiesFragment);
}
