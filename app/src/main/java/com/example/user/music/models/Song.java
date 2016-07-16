package com.example.user.music.models;

import java.io.Serializable;

/**
 * Created by utk on 19-01-2016.
 */
public class Song implements Serializable{
    private static final long serialVersionUID = 0L;


    private String id;
    private String title;
    private String name;
    private String artist;
    private String duration;
    private String data;

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    private long albumId;

    @Override
    public String toString() {
        return this.getName();
    }

    public Song(String id, String title, String name, String artist, String duration, String data, long albumId) {
        this.setId(id);
        this.setTitle(title);
        this.setName(name);
        this.setArtist(artist);
        this.setDuration(duration);
        this.setData(data);
        this.setAlbumId(albumId);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
