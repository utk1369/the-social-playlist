package com.thesocialplaylist.user.music.dto;

import java.io.Serializable;

/**
 * Created by user on 09-07-2017.
 */

public class LinkDTO implements Serializable {

    private String previewTitle;

    private String previewDesc;

    private String previewImage;

    private String url;

    public String getPreviewTitle() {
        return previewTitle;
    }

    public void setPreviewTitle(String previewTitle) {
        this.previewTitle = previewTitle;
    }

    public String getPreviewDesc() {
        return previewDesc;
    }

    public void setPreviewDesc(String previewDesc) {
        this.previewDesc = previewDesc;
    }

    public String getPreviewImage() {
        return previewImage;
    }

    public void setPreviewImage(String previewImage) {
        this.previewImage = previewImage;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
