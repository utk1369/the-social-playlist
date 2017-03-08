package com.thesocialplaylist.user.music.dto;

/**
 * Created by user on 19-02-2017.
 */

public class PopulateDTO {

    private String path;

    private String select;

    public PopulateDTO(String path, String select) {
        this.path = path;
        this.select = select;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSelect() {
        return select;
    }

    public void setSelect(String select) {
        this.select = select;
    }
}
