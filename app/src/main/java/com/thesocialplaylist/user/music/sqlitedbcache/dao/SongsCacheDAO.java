package com.thesocialplaylist.user.music.sqlitedbcache.dao;

import android.util.Log;

import com.activeandroid.Cache;
import com.activeandroid.Model;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.activeandroid.util.SQLiteUtils;
import com.thesocialplaylist.user.music.dto.SongDTO;
import com.thesocialplaylist.user.music.sqlitedbcache.model.SongsCache;

import org.apache.commons.lang3.SerializationUtils;

import java.util.List;

/**
 * Created by user on 18-09-2016.
 */
public class SongsCacheDAO {

    private final Class MODEL_CLASS = SongsCache.class;
    private final String TABLE_NAME = "songs_cache";

    public Long save(SongsCache songsCache) {
        return songsCache.save();
    }

    public List<SongsCache> getAllSongsFromCache() {
        return new Select().from(SongsCache.class).execute();
    }

    public SongsCache getOneSongForGivenClause(String clause, Object... args) {
        //recreateTable();
        return new Select().from(SongsCache.class).where(clause, args).executeSingle();
    }

    public List<SongsCache> getAllSongsForGivenClause(String clause, Object... args) {
        return new Select().from(SongsCache.class).where(clause, args).execute();
    }

    public SongsCache getSongCacheByDefaultId(Long defaultSongsId) {
        return SongsCache.load(SongsCache.class, defaultSongsId);
    }

    public void updateSongsCache(String fieldToSet, Object updateVal, String clause, Object... args) {
        new Update(SongsCache.class).set(fieldToSet, updateVal).where(clause, args).execute();
    }

    //to be removed from production
    public void recreateTable() {
        SQLiteUtils.execSql("DROP TABLE " + TABLE_NAME);
        SQLiteUtils.execSql(SQLiteUtils.createTableDefinition(Cache.getTableInfo(MODEL_CLASS)));
    }

    //to be called only in case of updates
    public void merge(SongsCache existingSongsCache, SongsCache newSongsCache) {
        if(existingSongsCache == null)
            return;
        if(existingSongsCache.getSongId() == null || newSongsCache.getSongId() == null ||
                !existingSongsCache.getSongId().equals(newSongsCache.getSongId()))
            throw new IllegalArgumentException("Song Ids do not match for " + existingSongsCache.getSongId());
        if((existingSongsCache.getHits() == null && newSongsCache.getHits() != null) ||
                (existingSongsCache.getHits() != null && !existingSongsCache.getHits().equals(newSongsCache.getHits())))
            existingSongsCache.setHits(newSongsCache.getHits());
        if((existingSongsCache.getRating() == null && newSongsCache.getRating() != null) ||
                (existingSongsCache.getRating() != null && !existingSongsCache.getRating().equals(newSongsCache.getRating())))
            existingSongsCache.setRating(newSongsCache.getRating());
        if((existingSongsCache.getLastListenedAt() == null && newSongsCache.getLastListenedAt() != null) ||
                (existingSongsCache.getLastListenedAt() != null && !existingSongsCache.getLastListenedAt().equals(newSongsCache.getLastListenedAt())))
            existingSongsCache.setLastListenedAt(newSongsCache.getLastListenedAt());
        if((existingSongsCache.getLikes() == null && newSongsCache.getLikes() != null) ||
                (existingSongsCache.getLikes() != null && !existingSongsCache.getLikes().equals(newSongsCache.getLikes())))
            existingSongsCache.setLikes(newSongsCache.getLikes());
    }
}
