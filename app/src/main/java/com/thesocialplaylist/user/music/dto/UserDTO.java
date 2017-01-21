package com.thesocialplaylist.user.music.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by user on 26-07-2016.
 */
public class UserDTO implements Serializable {

    private String id;

    private String name;

    private String fbId;

    private String status;

    private String imageUrl;

    private List<String> friends;

    private List<SongDTO> songDTOs;

    private List<SocialActivityDTO> socialActivities;

    private Date createdAt;

    private Date updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public List<SongDTO> getSongDTOs() {
        return songDTOs;
    }

    public void setSongDTOs(List<SongDTO> songDTOs) {
        this.songDTOs = songDTOs;
    }

    public List<SocialActivityDTO> getSocialActivities() {
        return socialActivities;
    }

    public void setSocialActivities(List<SocialActivityDTO> socialActivities) {
        this.socialActivities = socialActivities;
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

    @Override
    public String toString() {
        return "UserDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", fbId='" + fbId + '\'' +
                ", status='" + status + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", friends=" + friends +
                ", songDTOs=" + songDTOs +
                ", socialActivities=" + socialActivities +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
