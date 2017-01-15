package com.thesocialplaylist.user.music.events.models;

import com.thesocialplaylist.user.music.dto.SongDTO;
import com.thesocialplaylist.user.music.enums.PlaybackEvent;

/**
 * Created by user on 24-09-2016.
 */

public class TrackPlaybackEvent {

    private PlaybackEvent playbackEvent;

    private SongDTO songDTO;

    public TrackPlaybackEvent(PlaybackEvent playbackEvent, SongDTO songDTO) {
        this.playbackEvent = playbackEvent;
        this.songDTO = songDTO;
    }

    public PlaybackEvent getPlaybackEvent() {
        return playbackEvent;
    }

    public void setPlaybackEvent(PlaybackEvent playbackEvent) {
        this.playbackEvent = playbackEvent;
    }

    public SongDTO getSongDTO() {
        return songDTO;
    }

    public void setSongDTO(SongDTO songDTO) {
        this.songDTO = songDTO;
    }
}
