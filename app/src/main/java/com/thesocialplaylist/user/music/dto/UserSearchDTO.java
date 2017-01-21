package com.thesocialplaylist.user.music.dto;

import java.util.List;

/**
 * Created by user on 18-01-2017.
 */

public class UserSearchDTO {

    private UserDTO criteria;

    private List<String> projections;

    public UserDTO getCriteria() {
        return criteria;
    }

    public void setCriteria(UserDTO criteria) {
        this.criteria = criteria;
    }

    public List<String> getProjections() {
        return projections;
    }

    public void setProjections(List<String> projections) {
        this.projections = projections;
    }
}
