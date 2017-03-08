package com.thesocialplaylist.user.music.dto;

import java.util.List;

/**
 * Created by user on 19-02-2017.
 */

public class UserLoginRequestDTO {

    private UserDTO user;

    private List<PopulateDTO> populate;

    public UserLoginRequestDTO(UserDTO user, List<PopulateDTO> populate) {
        this.user = user;
        this.populate = populate;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public List<PopulateDTO> getPopulate() {
        return populate;
    }

    public void setPopulate(List<PopulateDTO> populate) {
        this.populate = populate;
    }
}
