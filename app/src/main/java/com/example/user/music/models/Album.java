package com.example.user.music.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by user on 21-05-2016.
 */
public class Album implements Serializable {

    private long albumId;
    private String albumName;
    private List<Song> albumSongs;

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

    public List<Song> getAlbumSongs() {
        return albumSongs;
    }

    public void setAlbumSongs(List<Song> albumSongs) {
        this.albumSongs = albumSongs;
    }
}
