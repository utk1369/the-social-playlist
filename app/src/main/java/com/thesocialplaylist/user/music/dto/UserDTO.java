package com.thesocialplaylist.user.music.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by user on 26-07-2016.
 */
public class UserDTO implements Serializable {

    @SerializedName("_id")
    private String id;

    private String name;

    private String fbId;

    private String status;

    private String imageUrl;

    private List<FriendDTO> friends; //should be list of UserDTOs

    private List<SongDTO> songs;

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

    public List<FriendDTO> getFriends() {
        return friends;
    }

    public void setFriends(List<FriendDTO> friends) {
        this.friends = friends;
    }

    public List<SongDTO> getSongs() {
        return songs;
    }

    public void setSongs(List<SongDTO> songs) {
        this.songs = songs;
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
                ", songs=" + songs +
                ", socialActivities=" + socialActivities +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
