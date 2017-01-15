package com.thesocialplaylist.user.music.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by user on 21-05-2016.
 */
public class AlbumDTO implements Serializable {

    private long albumId;
    private String albumName;
    private List<SongDTO> albumSongs;

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public List<SongDTO> getAlbumSongs() {
        return albumSongs;
    }

    public void setAlbumSongs(List<SongDTO> albumSongs) {
        this.albumSongs = albumSongs;
    }
}
