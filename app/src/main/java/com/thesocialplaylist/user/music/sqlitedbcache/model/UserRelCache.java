package com.thesocialplaylist.user.music.sqlitedbcache.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

/**
 * Created by user on 27-01-2017.
 */

@Table(name = "user_rel")
public class UserRelCache extends Model implements Serializable {

    public enum TYPE_CODES {
        NAME,
        STATUS,
        IMAGE_URL;
    }

    public enum RELATIONSHIP_TYPES {
        APP_USER,
        FRIEND;
    }

    @Column(name = "user_id")
    private String userId;

    @Column(name = "fb_id")
    private String fbId;

    @Column(name = "type_cd")
    private TYPE_CODES typeCd;

    @Column(name = "value")
    private String value;

    @Column(name = "rel_tp")
    private RELATIONSHIP_TYPES relTp;

    @Column(name = "last_upd_at")
    private String lastUpdatedAt;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    public TYPE_CODES getTypeCd() {
        return typeCd;
    }

    public void setTypeCd(TYPE_CODES typeCd) {
        this.typeCd = typeCd;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public RELATIONSHIP_TYPES getRelTp() {
        return relTp;
    }

    public void setRelTp(RELATIONSHIP_TYPES relTp) {
        this.relTp = relTp;
    }

    public String getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(String lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }
}
