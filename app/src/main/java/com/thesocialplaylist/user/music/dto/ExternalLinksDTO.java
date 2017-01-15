package com.thesocialplaylist.user.music.dto;

import com.thesocialplaylist.user.music.enums.ExternalLinkType;

import java.io.Serializable;

/**
 * Created by user on 06-09-2016.
 */
public class ExternalLinksDTO implements Serializable {

    private ExternalLinkType linkType;

    private String id;

    private String title;

    private String thumbnailUrl;

    public ExternalLinkType getLinkType() {
        return linkType;
    }

    public void setLinkType(ExternalLinkType linkType) {
        this.linkType = linkType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
}
