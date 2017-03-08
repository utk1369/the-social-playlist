package com.thesocialplaylist.user.music.dto;

import java.io.Serializable;

/**
 * Created by user on 15-02-2017.
 */

public class FriendDTO implements Serializable{

    private UserDTO friend;

    private String fbId;

    public UserDTO getFriend() {
        return friend;
    }

    public void setFriend(UserDTO friend) {
        this.friend = friend;
    }

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }
}
