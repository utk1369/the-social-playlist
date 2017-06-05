package com.thesocialplaylist.user.music.dto;

import com.google.gson.annotations.SerializedName;
import com.thesocialplaylist.user.music.enums.SocialActivityDomain;
import com.thesocialplaylist.user.music.enums.SocialActivityType;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by user on 06-09-2016.
 */
public class SocialActivityDTO implements Serializable {

    @SerializedName("_id")
    private String id;

    private String postedBy;

    private SocialActivityType activityType;

    private SocialActivityDomain domain;

    private List<String> recipientUserIds;

    private SongMetadataDTO songMetadata;

    private String link;

    private List<String> likes;

    private String caption;

    private Date createdAt;

    private Date updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public SocialActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(SocialActivityType activityType) {
        this.activityType = activityType;
    }

    public SocialActivityDomain getDomain() {
        return domain;
    }

    public void setDomain(SocialActivityDomain domain) {
        this.domain = domain;
    }

    public List<String> getRecipientUserIds() {
        return recipientUserIds;
    }

    public void setRecipientUserIds(List<String> recipientUserIds) {
        this.recipientUserIds = recipientUserIds;
    }

    public SongMetadataDTO getSongMetadata() {
        return songMetadata;
    }

    public void setSongMetadata(SongMetadataDTO songMetadata) {
        this.songMetadata = songMetadata;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
