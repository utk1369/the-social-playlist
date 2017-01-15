package com.thesocialplaylist.user.music.dependencyinjection.components;

import com.thesocialplaylist.user.music.activity.musicplayer.AlbumDetailsActivity;
import com.thesocialplaylist.user.music.activity.musicplayer.MediaPlayerActivity;
import com.thesocialplaylist.user.music.activity.musicplayer.MusicLibraryActivity;
import com.thesocialplaylist.user.music.dependencyinjection.modules.AppModule;
import com.thesocialplaylist.user.music.dependencyinjection.modules.MusicLibraryManagerModule;
import com.thesocialplaylist.user.music.dependencyinjection.modules.SQLiteCacheManagerModule;
import com.thesocialplaylist.user.music.fragment.musicplayer.mediacontroller.MediaControllerFragment;
import com.thesocialplaylist.user.music.fragment.musicplayer.musiclibrary.AlbumsGridFragment;
import com.thesocialplaylist.user.music.fragment.musicplayer.youtube.YoutubeSearchFragment;
import com.thesocialplaylist.user.music.manager.MusicLibraryManager;
import com.thesocialplaylist.user.music.service.MusicService;

import javax.inject.Singleton;

import dagger.Component;
import dagger.Subcomponent;

/**
 * Created by user on 17-10-2016.
 */

@Singleton
@Component(modules = {MusicLibraryManagerModule.class, SQLiteCacheManagerModule.class, AppModule.class})
public interface MusicLibraryManagerComponent {
    void inject(AlbumDetailsActivity albumDetailsActivity);
    void inject(MediaControllerFragment mediaControllerFragment);
    void inject(MusicLibraryActivity musicLibraryActivity);
    void inject(AlbumsGridFragment albumsGridFragment);
    void inject(MediaPlayerActivity mediaPlayerActivity);
    void inject(MusicService musicService);
    void inject(YoutubeSearchFragment youtubeSearchFragment);
}
