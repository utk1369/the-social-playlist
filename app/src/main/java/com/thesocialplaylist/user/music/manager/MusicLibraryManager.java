package com.thesocialplaylist.user.music.manager;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.StringUtils;
import com.thesocialplaylist.user.music.dto.AlbumDTO;
import com.thesocialplaylist.user.music.dto.ExternalLinksDTO;
import com.thesocialplaylist.user.music.dto.SongDTO;
import com.thesocialplaylist.user.music.dto.SongMetadataDTO;
import com.thesocialplaylist.user.music.dto.builders.SongDTOBuilder;
import com.thesocialplaylist.user.music.dto.extractors.ExternalLinksDTOExtractor;
import com.thesocialplaylist.user.music.dto.extractors.SongDTOExtractor;
import com.thesocialplaylist.user.music.events.models.SyncTracksEvent;
import com.thesocialplaylist.user.music.sqlitedbcache.dao.ExternalLinksCacheDAO;
import com.thesocialplaylist.user.music.sqlitedbcache.dao.SongsCacheDAO;
import com.thesocialplaylist.user.music.sqlitedbcache.model.ExternalLinksCache;
import com.thesocialplaylist.user.music.sqlitedbcache.model.SongsCache;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 01-09-2016.
 */
public class MusicLibraryManager {

    SongsCacheDAO songsCacheDAO;

    ExternalLinksCacheDAO externalLinksCacheDAO;

    Application app;

    public MusicLibraryManager(SongsCacheDAO songsCacheDAO, ExternalLinksCacheDAO externalLinksCacheDAO, Application app) {
        this.songsCacheDAO = songsCacheDAO;
        this.externalLinksCacheDAO = externalLinksCacheDAO;
        this.app = app;
    }

    private List<SongMetadataDTO> cursorToListOfSongs(Cursor cursor) {
        List<SongMetadataDTO> songMetadataDTOs = new ArrayList<>();
        while(cursor.moveToNext()) {
            SongMetadataDTO metadata = new SongMetadataDTO();
            metadata.setId(cursor.getString(0));
            metadata.setTitle(cursor.getString(1));
            metadata.setArtist(cursor.getString(3));
            metadata.setDuration(cursor.getString(4));
            metadata.setData(cursor.getString(5));
            metadata.setAlbumId(Long.parseLong(cursor.getString(6)));
            metadata.setAlbum(cursor.getString(7));

            songMetadataDTOs.add(metadata);
        }
        return songMetadataDTOs;
    }

    public List<SongMetadataDTO> getSongsAsList(String filterCriteria, String[] filterParams) {
        ContentResolver contentResolver = app.getApplicationContext().getContentResolver();
        String projections[] = new String[] {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ALBUM
        };
        String selections = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        if(filterCriteria != null) {
            selections += " AND " + filterCriteria;
        }

        Cursor cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projections,
                selections,
                filterParams,
                null
        );
        return cursorToListOfSongs(cursor);
    }

    public List<AlbumDTO> cursorToListOfAlbums(Cursor cursor) {
        List<AlbumDTO> albumDTOs = new ArrayList<AlbumDTO>();
        while(cursor.moveToNext()) {
            AlbumDTO albumDTO = new AlbumDTO();
            albumDTO.setAlbumId(Long.parseLong(cursor.getString(0)));
            albumDTO.setAlbumName(cursor.getString(1));
            albumDTOs.add(albumDTO);
        }
        return albumDTOs;
    }

    public List<AlbumDTO> getAllAlbumsAsList(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        String projections[] = new String[] {
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM
        };
        Cursor cursor = contentResolver.query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                projections,
                null,
                null,
                null
        );
        return cursorToListOfAlbums(cursor);
    }

    public SongMetadataDTO getSongMetadataById(String id) {
        String filterCriteria = MediaStore.Audio.Media._ID + " = ?";
        String[] filterParams = new String[]{id};
        List<SongMetadataDTO> results = getSongsAsList(filterCriteria, filterParams);
        if(results == null || results.size() == 0) {
            Log.d("Music Library manager", "Song Id Invalid: " + id);
            throw new IllegalArgumentException("Invalid Song Id: " + id);
        }
        return results.get(0);
    }

    public List<SongDTO> getAllSongs(String filterCriteria, String[] filterParameters) {
        //songsCacheDAO.recreateTable();
        List<SongDTO> songDTOs = new ArrayList<>();
        List<SongMetadataDTO> songMetadataDTOs = getSongsAsList(filterCriteria, filterParameters);
        List<SongsCache> songsCaches = songsCacheDAO.getAllSongsFromCache();

        for(SongMetadataDTO metadataDTO: songMetadataDTOs) {
            SongDTO songDTO = null;
            for (SongsCache songsCache : songsCaches) {
                if (songsCache.getSongId().equals(metadataDTO.getId())) {
                    songDTO = SongDTOBuilder.populate(songsCache, metadataDTO);
                    break;
                }
            }
            if (songDTO == null) {
                songDTO = SongDTOBuilder.populate(null, metadataDTO);
            }
            songDTOs.add(songDTO);
        }
        return songDTOs;
    }

    public List<SongDTO> getAllSongs() {
        return getAllSongs(null, null);
    }

    public List<SongDTO> getAllSongsToBeSynced() {
        List<SongDTO> songDTOs = new ArrayList<>();
        List<SongsCache> songsCaches = songsCacheDAO.getAllSongsForGivenClause("is_synced = ?", false);

        for (SongsCache songsCache : songsCaches) {
            SongMetadataDTO songMetadataDTO = getSongMetadataById(songsCache.getSongId());
            SongDTO songDTO = SongDTOBuilder.populate(songsCache, songMetadataDTO);
            songDTOs.add(songDTO);
        }
        return songDTOs;
    }

    public SongDTO getSongDetails(String id){
        SongsCache songsCache = songsCacheDAO.getOneSongForGivenClause("song_id = ?", id);
        SongMetadataDTO metadataDTO = getSongMetadataById(id);
        return SongDTOBuilder.populate(songsCache, metadataDTO);
    }

    public SongDTO saveSongDetailsToCache(SongDTO songDTO) {
        SongsCache existingSongsCache = songsCacheDAO.getOneSongForGivenClause("song_id = ?", songDTO.getId());
        Long mId = null;

        //Insert
        if(existingSongsCache == null) {
            songsCacheDAO.save(SongDTOExtractor.extractSongsCacheFromSongDTO(songDTO));
        } else { //Update
            SongsCache existingRecord = songsCacheDAO.getSongCacheByDefaultId(existingSongsCache.getId());
            songsCacheDAO.merge(existingRecord,
                    SongDTOExtractor.extractSongsCacheFromSongDTO(songDTO));
            songsCacheDAO.save(existingRecord);
        }
        return getSongDetails(songDTO.getMetadata().getId());
    }

    public ExternalLinksDTO associateSongToExternalLink(String songId, ExternalLinksDTO externalLinksDTO) {

        SongsCache songsCache = songsCacheDAO.getOneSongForGivenClause("song_id = ?", songId);
        if(songsCache == null) {
            SongDTO newSongDTO = new SongDTO();
            newSongDTO.setId(songId);
            Long mId = songsCacheDAO.save(SongDTOExtractor.extractSongsCacheFromSongDTO(newSongDTO));
            songsCache = songsCacheDAO.getOneSongForGivenClause("song_id = ?", songId);
        }

        ExternalLinksCache newExternalLinksCache = ExternalLinksDTOExtractor.extractExternalLinksCache(songsCache, externalLinksDTO);

        //delete any other existing entries for the same type for the same song
        List<ExternalLinksCache> existingExternalLinks = songsCache.externalLinksCache();
        for(ExternalLinksCache externalLinksCache: existingExternalLinks) {
            if(externalLinksCache.getLinkItemTp().equals(newExternalLinksCache.getLinkItemTp()))
                externalLinksCacheDAO.delete(externalLinksCache);
        }

        externalLinksCacheDAO.insert(songsCache, newExternalLinksCache);
        songsCacheDAO.updateSongsCache("is_synced = ?", false, "song_id IN (" + songId + ")");
        EventBus.getDefault().post(new SyncTracksEvent());

        return externalLinksDTO;
    }

    public void updateSyncStatus(List<SongDTO> songs, Boolean syncStatus) {
        if(songs == null || songs.size() == 0)
            return;
        String songIdsToUpdate[] = new String[songs.size()];
        for(int i=0; i < songs.size(); i++) {
            songIdsToUpdate[i] = (songs.get(i).getId());
        }

        songsCacheDAO.updateSongsCache("is_synced = ?", syncStatus, "song_id IN (" + TextUtils.join(",", songIdsToUpdate) + ")");
    }
}
