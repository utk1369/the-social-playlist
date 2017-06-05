package com.thesocialplaylist.user.music.sqlitedbcache.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.fasterxml.jackson.core.SerializableString;

import java.io.Serializable;
import java.util.List;

/**
 * Created by user on 18-09-2016.
 */

@Table(name="songs_cache")
public class SongsCache extends Model implements Serializable {

    @Column(name="metadata_hash", unique = true)
    private String metadataHash;

    @Column(name="song_id", unique = true)
    private String songId;

    @Column(name="hits")
    private Integer hits;

    @Column(name="last_listened_at")
    private String lastListenedAt;

    @Column(name="rating")
    private Double rating;

    @Column(name="likes")
    private Integer likes;

    @Column(name="is_synced")
    private Boolean isSynced;

    public List<ExternalLinksCache> externalLinksCache() {
        return getMany(ExternalLinksCache.class, "songs_cache");
    }

    public String getMetadataHash() {
        return metadataHash;
    }

    public void setMetadataHash(String metadataHash) {
        this.metadataHash = metadataHash;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public Integer getHits() {
        return hits;
    }

    public void setHits(Integer hits) {
        this.hits = hits;
    }

    public String getLastListenedAt() {
        return lastListenedAt;
    }

    public void setLastListenedAt(String lastListenedAt) {
        this.lastListenedAt = lastListenedAt;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Boolean getSynced() {
        return isSynced;
    }

    public void setSynced(Boolean synced) {
        isSynced = synced;
    }
}
