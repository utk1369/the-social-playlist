package com.thesocialplaylist.user.music.dto;

import java.util.List;

/**
 * Created by user on 22-01-2017.
 */

public class UserRegistrationDTO {

    private UserDTO userDetails;

    private List<String> fbFriends;

    public List<String> getFbFriends() {
        return fbFriends;
    }

    public void setFbFriends(List<String> fbFriends) {
        this.fbFriends = fbFriends;
    }

    public UserDTO getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDTO userDetails) {
        this.userDetails = userDetails;
    }
}
