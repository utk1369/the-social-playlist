package com.thesocialplaylist.user.music.dto.builders;

import com.thesocialplaylist.user.music.dto.ExternalLinksDTO;
import com.thesocialplaylist.user.music.dto.SongDTO;
import com.thesocialplaylist.user.music.dto.SongMetadataDTO;
import com.thesocialplaylist.user.music.sqlitedbcache.model.ExternalLinksCache;
import com.thesocialplaylist.user.music.sqlitedbcache.model.SongsCache;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 11-10-2016.
 */

public class SongDTOBuilder {
    public static SongDTO populate(SongsCache songsCache, SongMetadataDTO songMetadataDTO) {

        SongDTO songDTO = new SongDTO();
        if(songsCache != null) {
            songDTO.setId(songsCache.getSongId());
            songDTO.setHits(songsCache.getHits());
            songDTO.setRating(songsCache.getRating());
            List<ExternalLinksCache> externalLinksCacheList = songsCache.externalLinksCache();

            List<ExternalLinksDTO> externalLinksDTOs = new ArrayList<>();
            for(ExternalLinksCache externalLinksCache: externalLinksCacheList) {
                externalLinksDTOs.add(ExternalLinksDTOBuilder.populate(externalLinksCache));
            }
            songDTO.setExternalLinks(externalLinksDTOs);
            //convert songsCache Timestamp to store unix timestamps in Long format
            //songDTO.setLastListenedAt(new Timestamp(songsCache.getLastListenedAt()));
        }
        songDTO.setMetadata(songMetadataDTO);
        songDTO.setId(songMetadataDTO.getId());
        return songDTO;
    }
}
