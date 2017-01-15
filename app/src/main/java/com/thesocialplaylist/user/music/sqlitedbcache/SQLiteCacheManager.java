package com.thesocialplaylist.user.music.sqlitedbcache;


import android.content.Context;

import com.thesocialplaylist.user.music.dto.ExternalLinksDTO;
import com.thesocialplaylist.user.music.dto.SongDTO;
import com.thesocialplaylist.user.music.dto.SongMetadataDTO;
import com.thesocialplaylist.user.music.dto.builders.SongDTOBuilder;
import com.thesocialplaylist.user.music.dto.extractors.ExternalLinksDTOExtractor;
import com.thesocialplaylist.user.music.dto.extractors.SongDTOExtractor;
import com.thesocialplaylist.user.music.manager.MusicLibraryManager;
import com.thesocialplaylist.user.music.sqlitedbcache.dao.ExternalLinksCacheDAO;
import com.thesocialplaylist.user.music.sqlitedbcache.dao.SongsCacheDAO;
import com.thesocialplaylist.user.music.sqlitedbcache.model.ExternalLinksCache;
import com.thesocialplaylist.user.music.sqlitedbcache.model.SongsCache;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by user on 20-09-2016.
 */
public class SQLiteCacheManager {

    SongsCacheDAO songsCacheDAO;

    ExternalLinksCacheDAO externalLinksCacheDAO;

    public SQLiteCacheManager(SongsCacheDAO songsCacheDAO, ExternalLinksCacheDAO externalLinksCacheDAO) {
        this.songsCacheDAO = songsCacheDAO;
        this.externalLinksCacheDAO = externalLinksCacheDAO;
    }

    public Long saveSongDetails(SongDTO songDTO) {
        SongsCache existingSongsCache = songsCacheDAO.getSongById(songDTO.getId());
        Long mId = null;
        if(existingSongsCache == null) {
            mId = songsCacheDAO.save(SongDTOExtractor.extractSongsCacheFromSongDTO(songDTO));
        } else {
            SongsCache existingRecord = songsCacheDAO.getSongCacheByDefaultId(existingSongsCache.getId());
            songsCacheDAO.merge(existingRecord,
                    SongDTOExtractor.extractSongsCacheFromSongDTO(songDTO));
            mId = songsCacheDAO.save(existingRecord);
        }
        return mId;
    }

    public ExternalLinksDTO associateSongToExternalLinkInCache(String songId, ExternalLinksDTO externalLinksDTO) {
        //do this in transaction
        SongsCache songsCache = songsCacheDAO.getSongById(songId);
        if(songsCache == null) {
            SongDTO newSongDTO = new SongDTO();
            newSongDTO.setId(songId);
            Long mId = songsCacheDAO.save(SongDTOExtractor.extractSongsCacheFromSongDTO(newSongDTO));
            songsCache = songsCacheDAO.getSongById(songId);
        }

        ExternalLinksCache newExternalLinksCache = ExternalLinksDTOExtractor.extractExternalLinksCache(songsCache, externalLinksDTO);

        //delete any other existing entries for the same type for the same song
        List<ExternalLinksCache> existingExternalLinks = songsCache.externalLinksCache();
        for(ExternalLinksCache externalLinksCache: existingExternalLinks) {
            if(externalLinksCache.getLinkItemTp().equals(newExternalLinksCache.getLinkItemTp()))
                externalLinksCacheDAO.delete(externalLinksCache);
        }

        externalLinksCacheDAO.insert(songsCache, newExternalLinksCache);
        return externalLinksDTO;
    }

    public SongsCache getSongsCache(String id) {
        return songsCacheDAO.getSongById(id);
    }

    public List<SongsCache> getAllSongsFromCache() {
        return songsCacheDAO.getAllSongsFromCache();
    }
}
