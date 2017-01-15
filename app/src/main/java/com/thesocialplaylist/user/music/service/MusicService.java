package com.thesocialplaylist.user.music.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.thesocialplaylist.user.music.TheSocialPlaylistApplication;
import com.thesocialplaylist.user.music.dto.SongDTO;
import com.thesocialplaylist.user.music.enums.PlaybackEvent;
import com.thesocialplaylist.user.music.events.models.TrackPlaybackEvent;
import com.thesocialplaylist.user.music.events.models.TracksListUpdateEvent;
import com.thesocialplaylist.user.music.manager.MusicLibraryManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by user on 08-04-2016.
 */
public class MusicService extends Service
        implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    private MediaPlayer mediaPlayer;
    private List<SongDTO> nowPlayingList;
    private int currentPlayingPosition;
    private final IBinder mediaPlayerBinder = new MediaPlayerServiceBinder();

    private AudioManager audioManager;
    private AudioFocusChangeListener audioFocusChangeListener = new AudioFocusChangeListener();

    private EventBus mEventBus = EventBus.getDefault();

    @Inject
    MusicLibraryManager musicLibraryManager;

    public int getCurrentPlayingPosition() {
        return currentPlayingPosition;
    }

    public void setCurrentPlayingPosition(int currentPlayingPosition) {
        this.currentPlayingPosition = currentPlayingPosition;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        try {
            playNextSong();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        play();
        SongDTO songDTO = getNowPlayingList().get(getCurrentPlayingPosition());
        mEventBus.post(new TrackPlaybackEvent(
                PlaybackEvent.NEW_TRACK,
                songDTO));
    }

    public void setNowPlayingList(List<SongDTO> listOfSongDTOs) {
        this.nowPlayingList = listOfSongDTOs;
        mEventBus.post(new TracksListUpdateEvent(listOfSongDTOs));
        Log.i("Size of list", "" + listOfSongDTOs.size());
    }

    public List<SongDTO> getNowPlayingList() {
        return this.nowPlayingList;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //register subscribers
        EventBus.getDefault().register(this);
        return mediaPlayerBinder;
    }

    @Override
    public boolean onUnbind(Intent intent){
        //mediaPlayer.stop();
        //mediaPlayer.release();
        //unregister subscribers
        EventBus.getDefault().unregister(this);
        Log.i("Unbinding Service", "");
        return false;
    }

    public class MediaPlayerServiceBinder extends Binder {
        public MusicService getMusicService() {
            return MusicService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initialize();
    }

    private void initialize() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);

        nowPlayingList = null;
        currentPlayingPosition = 0;

        audioManager = (AudioManager) getSystemService(this.AUDIO_SERVICE);

        ((TheSocialPlaylistApplication)getApplication()).getMusicLibraryManagerComponent().inject(this);
    }

    public void playSong(List<SongDTO> nowPlayingList, int position) throws IOException {
        setNowPlayingList(nowPlayingList);
        setCurrentPlayingPosition(position);
        SongDTO selection = this.nowPlayingList.get(currentPlayingPosition);
        Log.i("Song from service: ", selection.getMetadata().getTitle());
        mediaPlayer.reset();
        mediaPlayer.setDataSource(selection.getMetadata().getData());
        mediaPlayer.prepareAsync(); //listens to onprepared listener
    }

    private class AudioFocusChangeListener implements AudioManager.OnAudioFocusChangeListener {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if(focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                pause();
                audioManager.abandonAudioFocus(audioFocusChangeListener);
                Log.i("Audio Focus", "lost");
            } else if(focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                play();
                Log.i("Audio Focus", "gained");
            } else if(focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                pause();
                Log.i("Audio Focus", "transient lost");
            }
        }
    }

    public void play() {
        int result = audioManager.requestAudioFocus(audioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
        );
        if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mediaPlayer.start();
            mEventBus.post(new TrackPlaybackEvent(
                    PlaybackEvent.PLAY,
                    getNowPlayingList().get(getCurrentPlayingPosition())));
        } else {
            Log.e("Audio focus", "not granted");
        }
    }

    public void pause() {
        pausePlayer();
        mEventBus.post(new TrackPlaybackEvent(
                PlaybackEvent.PAUSE,
                getNowPlayingList().get(getCurrentPlayingPosition())));
    }

    //Implement methods required by MediaController to control playback
    public int getPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }

    public void pausePlayer(){
        mediaPlayer.pause();
    }

    public void seek(int posn){
        mediaPlayer.seekTo(posn);
    }

    public void go(){
        mediaPlayer.start();
    }

    public void playNextSong() throws IOException {
        int n = nowPlayingList.size();
        currentPlayingPosition = (currentPlayingPosition + 1) % n;
        playSong(getNowPlayingList(), currentPlayingPosition);
    }

    public void playPrevSong() throws IOException {
        int n = nowPlayingList.size();
        currentPlayingPosition = ((currentPlayingPosition - 1) + n) % n;
        playSong(getNowPlayingList(), currentPlayingPosition);
    }

    //Event Subscribers
    @Subscribe(threadMode =  ThreadMode.BACKGROUND)
    public void onTrackChange(TrackPlaybackEvent trackPlaybackEvent) {
        if(trackPlaybackEvent.getPlaybackEvent().equals(PlaybackEvent.NEW_TRACK)) {
            SongDTO songDTO = trackPlaybackEvent.getSongDTO();
            songDTO.setLastListenedAt(new Timestamp(System.currentTimeMillis()));
            songDTO.setHits(songDTO.getHits() == null ? 1 : (songDTO.getHits() + 1));
            musicLibraryManager.saveSongDetailsToCache(songDTO);
            Log.i("Saved", songDTO.getMetadata().getTitle());
        }
    }
}
