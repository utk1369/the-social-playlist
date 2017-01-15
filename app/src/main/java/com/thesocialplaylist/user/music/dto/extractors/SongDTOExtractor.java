package com.thesocialplaylist.user.music.dto.extractors;

import com.thesocialplaylist.user.music.dto.SongDTO;
import com.thesocialplaylist.user.music.sqlitedbcache.model.SongsCache;

/**
 * Created by user on 03-10-2016.
 */

public class SongDTOExtractor {

    public static SongsCache extractSongsCacheFromSongDTO(SongDTO songDTO) {
        SongsCache songsCache = new SongsCache();
        songsCache.setSongId(songDTO.getId());
        songsCache.setHits(songDTO.getHits());
        songsCache.setLastListenedAt(songDTO.getLastListenedAt() == null ? null: songDTO.getLastListenedAt().toString());
        songsCache.setLikes(songDTO.getLikes() == null ? 0 : songDTO.getLikes().size());
        songsCache.setRating(songDTO.getRating());
        return songsCache;
    }

}
