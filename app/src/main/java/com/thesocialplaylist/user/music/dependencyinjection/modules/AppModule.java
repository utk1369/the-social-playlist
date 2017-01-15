package com.thesocialplaylist.user.music.dependencyinjection.modules;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by user on 31-10-2016.
 */

@Module
public class AppModule {

    Application myApp;

    public AppModule(Application myApp) {
        this.myApp = myApp;
    }
    @Singleton
    @Provides
    public Application provideApplication() {
        return myApp;
    }
}
