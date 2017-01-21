package com.thesocialplaylist.user.music.dependencyinjection.components;

import com.thesocialplaylist.user.music.activity.MainActivity;
import com.thesocialplaylist.user.music.dependencyinjection.modules.RetrofitModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by user on 18-01-2017.
 */

@Singleton
@Component(modules = {RetrofitModule.class})
public interface RetrofitComponent {
    void inject(MainActivity mainActivity);
}
