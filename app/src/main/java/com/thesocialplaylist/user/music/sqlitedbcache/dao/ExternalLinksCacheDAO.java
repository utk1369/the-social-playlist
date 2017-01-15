package com.thesocialplaylist.user.music.sqlitedbcache.dao;

import com.activeandroid.Cache;
import com.activeandroid.util.SQLiteUtils;
import com.thesocialplaylist.user.music.sqlitedbcache.model.ExternalLinksCache;
import com.thesocialplaylist.user.music.sqlitedbcache.model.SongsCache;

/**
 * Created by user on 18-09-2016.
 */
public class ExternalLinksCacheDAO {

    private final Class MODEL_CLASS = ExternalLinksCache.class;
    private final String TABLE_NAME = "external_links_cache";

    public void recreateTable() {
        SQLiteUtils.execSql("DROP TABLE " + TABLE_NAME);
        SQLiteUtils.execSql(SQLiteUtils.createTableDefinition(Cache.getTableInfo(MODEL_CLASS)));
    }

    public Long insert(SongsCache songsCache, ExternalLinksCache externalLinksCache) {
        return externalLinksCache.save();
    }

    public void delete(ExternalLinksCache externalLinksCache) {
        ExternalLinksCache.delete(ExternalLinksCache.class, externalLinksCache.getId());
    }
}
