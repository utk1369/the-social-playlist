package com.example.user.music.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.user.music.R;
import com.example.user.music.activity.musicplayer.MediaPlayerActivity;
import com.example.user.music.models.Song;

import java.io.IOException;
import java.util.List;

/**
 * Created by user on 08-04-2016.
 */
public class MusicService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    private MediaPlayer mediaPlayer;
    private List<Song> nowPlayingList;
    private int currentPlayingPosition = 0;
    private final IBinder mediaPlayerBinder = new MediaPlayerServiceBinder();

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
        mp.start();
        Intent notificationIntent = new Intent(this, MediaPlayerActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_headset_white_24dp)
                .setTicker(nowPlayingList.get(getCurrentPlayingPosition()).getTitle())
                .setContentTitle("Now Playing")
                .setContentText(nowPlayingList.get(getCurrentPlayingPosition()).getTitle() + ":)");
        Notification notification = builder.build();
        startForeground(1, notification);
    }

    public void setNowPlayingList(List<Song> listOfSongs) {
        this.nowPlayingList = listOfSongs;
        Log.i("Size of list", "" + listOfSongs.size());
    }

    public List<Song> getNowPlayingList() {
        return this.nowPlayingList;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mediaPlayerBinder;
    }

    @Override
    public boolean onUnbind(Intent intent){
        //mediaPlayer.stop();
        //mediaPlayer.release();
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
    }

    public void playSong(List<Song> nowPlayingList, int position) throws IOException {
        setNowPlayingList(nowPlayingList); setCurrentPlayingPosition(position);
        Song selection = this.nowPlayingList.get(currentPlayingPosition);
        Log.i("Song from service: ", selection.getTitle());
        mediaPlayer.reset();
        mediaPlayer.setDataSource(selection.getData());
        mediaPlayer.prepareAsync();
        //mediaPlayer.start();
    }

    public int playOrPauseAction() {
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            stopForeground(true);
            return 0;
        }
        else {
            mediaPlayer.start();
            Intent notificationIntent = new Intent(this, MediaPlayerActivity.class);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification.Builder builder = new Notification.Builder(this);
            builder.setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_headset_white_24dp)
                    .setTicker(nowPlayingList.get(getCurrentPlayingPosition()).getTitle())
                    .setContentTitle("Now Playing")
                    .setContentText(nowPlayingList.get(getCurrentPlayingPosition()).getTitle() + ":)");
            Notification notification = builder.build();
            startForeground(1, notification);
            return 1;
        }
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
}
