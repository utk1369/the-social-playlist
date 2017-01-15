package com.thesocialplaylist.user.music.sqlitedbcache.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by user on 18-09-2016.
 */

@Table(name="external_links_cache")
public class ExternalLinksCache extends Model{

    public ExternalLinksCache() {
        super();
    }

    @Column(name="songs_cache", onDelete = Column.ForeignKeyAction.CASCADE)
    private SongsCache associatedSong;

    @Column(name="link_item_tp")
    private String linkItemTp;

    @Column(name="link_item_id")
    private String linkItemId;

    @Column(name="link_item_title")
    private String linkItemTitle;

    @Column(name="link_item_thumbnail_url")
    private String linkItemThumbnailUrl;

    public SongsCache getSongsCache() {
        return associatedSong;
    }

    public void setSongsCache(SongsCache associatedSong) {
        this.associatedSong = associatedSong;
    }

    public String getLinkItemTp() {
        return linkItemTp;
    }

    public void setLinkItemTp(String linkItemTp) {
        this.linkItemTp = linkItemTp;
    }

    public String getLinkItemId() {
        return linkItemId;
    }

    public void setLinkItemId(String linkItemId) {
        this.linkItemId = linkItemId;
    }

    public String getLinkItemTitle() {
        return linkItemTitle;
    }

    public void setLinkItemTitle(String linkItemTitle) {
        this.linkItemTitle = linkItemTitle;
    }

    public String getLinkItemThumbnailUrl() {
        return linkItemThumbnailUrl;
    }

    public void setLinkItemThumbnailUrl(String linkItemThumbnailUrl) {
        this.linkItemThumbnailUrl = linkItemThumbnailUrl;
    }
}
