package com.thesocialplaylist.user.music.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Created by utk on 19-01-2016.
 */
public class SongDTO implements Serializable{
    private static final long serialVersionUID = 0L;

    private String id;

    private SongMetadataDTO metadata;

    private List<ExternalLinksDTO> externalLinksDTOs;

    private Integer hits;

    private Timestamp lastListenedAt;

    private List<String> likes;

    private Double rating;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SongMetadataDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(SongMetadataDTO metadata) {
        this.metadata = metadata;
    }

    public List<ExternalLinksDTO> getExternalLinksDTOs() {
        return externalLinksDTOs;
    }

    public void setExternalLinksDTOs(List<ExternalLinksDTO> externalLinksDTOs) {
        this.externalLinksDTOs = externalLinksDTOs;
    }

    public Timestamp getLastListenedAt() {
        return lastListenedAt;
    }

    public void setLastListenedAt(Timestamp lastListenedAt) {
        this.lastListenedAt = lastListenedAt;
    }

    public Integer getHits() {
        return hits;
    }

    public void setHits(Integer hits) {
        this.hits = hits;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}
