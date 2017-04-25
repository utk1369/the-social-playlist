package com.thesocialplaylist.user.music.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.thesocialplaylist.user.music.R;
import com.thesocialplaylist.user.music.TheSocialPlaylistApplication;
import com.thesocialplaylist.user.music.activity.musicplayer.MediaPlayerActivity;
import com.thesocialplaylist.user.music.dto.SongDTO;
import com.thesocialplaylist.user.music.enums.PlaybackEvent;
import com.thesocialplaylist.user.music.events.models.TrackPlaybackEvent;
import com.thesocialplaylist.user.music.events.models.TracksListUpdateEvent;
import com.thesocialplaylist.user.music.manager.MusicLibraryManager;
import com.thesocialplaylist.user.music.utils.ImageUtil;

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

    private static final int NOTIFICATION_ID = 1369;
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
        buildNotification(songDTO, PlaybackEvent.PLAY);
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
        return mediaPlayerBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null && intent.getAction() != null) {
            try {
                handleIncomingIntent(intent);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("MUSIC SERVICE", "Incoming intent failed to handle");
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void handleIncomingIntent(Intent intent) throws IOException {
        if(intent.getAction().equals(PlaybackEvent.NEXT.name())) {
            playNextSong();
            buildNotification(nowPlayingList.get(currentPlayingPosition), PlaybackEvent.NEXT);
        } else if(intent.getAction().equals(PlaybackEvent.PREV.name())) {
            playPrevSong();
            buildNotification(nowPlayingList.get(currentPlayingPosition),PlaybackEvent.PREV);
        } else if(intent.getAction().equals(PlaybackEvent.PLAY.name())) {
            play();
            buildNotification(nowPlayingList.get(currentPlayingPosition), PlaybackEvent.PLAY);
        } else if(intent.getAction().equals(PlaybackEvent.PAUSE.name())) {
            pause();
            buildNotification(nowPlayingList.get(currentPlayingPosition), PlaybackEvent.PAUSE);
        }
    }


    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("Music Service", "on unbind called");
        return false;
    }

    @Override
    public void onDestroy() {
        Log.i("Music Service", "on destroy called");
        /*mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();*/
        stopForeground(true);
        //make sure to unbind all the clients bound to this service before calling stop self.
        stopSelf();
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

    public void buildNotification(SongDTO nowPlaying, PlaybackEvent playbackEvent) {
        int playPauseIcon = R.drawable.ic_play_arrow_white_24dp;
        PendingIntent playPausePendingIntent =  getPlaybackAction(PlaybackEvent.PLAY);
        if(playbackEvent.equals(PlaybackEvent.PAUSE)) {
            playPauseIcon = R.drawable.ic_play_arrow_white_24dp;
            playPausePendingIntent = getPlaybackAction(PlaybackEvent.PLAY);
        } else if(playbackEvent.equals(PlaybackEvent.PLAY)) {
            playPauseIcon = R.drawable.ic_pause_white_24dp;
            playPausePendingIntent = getPlaybackAction(PlaybackEvent.PAUSE);
        }

        Notification notification = new NotificationCompat.Builder(getApplicationContext())
                // Show controls on lock screen even when user hides sensitive content.
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_audiotrack_white_24dp)
                // Add media control buttons that invoke intents in your media service
                .addAction(R.drawable.ic_skip_previous_white_24dp, "Previous", getPlaybackAction(PlaybackEvent.PREV)) // #0
                .addAction(playPauseIcon, "Play/Pause", playPausePendingIntent)  // #1
                .addAction(R.drawable.ic_skip_next_white_24dp, "Next", getPlaybackAction(PlaybackEvent.NEXT))     // #2
                // Apply the media style template
                .setStyle(new NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2))
                .setContentTitle(nowPlaying.getMetadata().getTitle())
                .setContentText(nowPlaying.getMetadata().getArtist())
                .setContentIntent(PendingIntent.getActivity(this, 123, new Intent(this, MediaPlayerActivity.class), PendingIntent.FLAG_CANCEL_CURRENT))
                .setLargeIcon(ImageUtil.getAlbumArtAsBitmap(this,
                        nowPlaying.getMetadata().getAlbumId(), BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_audiotrack_white_24dp)))
                .setShowWhen(false)
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }

    private PendingIntent getPlaybackAction(PlaybackEvent playbackEvent) {
        Intent playbackIntent = new Intent(this, MusicService.class);
        playbackIntent.setAction(playbackEvent.name());
        return PendingIntent.getService(this, 1, playbackIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
