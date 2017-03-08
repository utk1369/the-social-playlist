package com.thesocialplaylist.user.music.dto;

import java.util.List;

/**
 * Created by user on 22-02-2017.
 */

public class UserProfileRequestDTO {

    private List<String> projections;

    private List<PopulateDTO> populate;

    public List<String> getProjections() {
        return projections;
    }

    public void setProjections(List<String> projections) {
        this.projections = projections;
    }

    public List<PopulateDTO> getPopulate() {
        return populate;
    }

    public void setPopulate(List<PopulateDTO> populate) {
        this.populate = populate;
    }
}
