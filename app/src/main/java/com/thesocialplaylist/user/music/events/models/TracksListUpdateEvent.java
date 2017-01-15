package com.thesocialplaylist.user.music.events.models;

import com.thesocialplaylist.user.music.dto.SongDTO;

import java.util.List;

/**
 * Created by user on 24-09-2016.
 */

public class TracksListUpdateEvent {

    private List<SongDTO> songDTOs;

    public List<SongDTO> getSongDTOs() {
        return songDTOs;
    }

    public TracksListUpdateEvent(List<SongDTO> songDTOs) {
        this.songDTOs = songDTOs;
    }

    public void setSongDTOs(List<SongDTO> songDTOs) {
        this.songDTOs = songDTOs;
    }
}
