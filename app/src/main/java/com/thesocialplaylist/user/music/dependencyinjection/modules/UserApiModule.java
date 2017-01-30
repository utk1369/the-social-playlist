package com.thesocialplaylist.user.music.dependencyinjection.modules;

import com.thesocialplaylist.user.music.api.declaration.UserApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by user on 22-01-2017.
 */

@Module
public class UserApiModule {

    @Singleton
    @Provides
    public UserApi providesUserApi(Retrofit retrofit) {
        return retrofit.create(UserApi.class);
    }
}
