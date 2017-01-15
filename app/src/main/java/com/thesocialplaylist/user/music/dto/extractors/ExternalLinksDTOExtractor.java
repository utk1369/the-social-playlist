package com.thesocialplaylist.user.music.dto.extractors;

import com.thesocialplaylist.user.music.dto.ExternalLinksDTO;
import com.thesocialplaylist.user.music.sqlitedbcache.model.ExternalLinksCache;
import com.thesocialplaylist.user.music.sqlitedbcache.model.SongsCache;

/**
 * Created by user on 03-10-2016.
 */

public class ExternalLinksDTOExtractor {

    public static ExternalLinksCache extractExternalLinksCache(SongsCache songsCache, ExternalLinksDTO externalLinksDTO) {
        ExternalLinksCache externalLinksCache = new ExternalLinksCache();
        externalLinksCache.setLinkItemId(externalLinksDTO.getId());
        externalLinksCache.setLinkItemTitle(externalLinksDTO.getTitle());
        externalLinksCache.setLinkItemTp(externalLinksDTO.getLinkType().toString());
        externalLinksCache.setLinkItemThumbnailUrl(externalLinksDTO.getThumbnailUrl());
        externalLinksCache.setSongsCache(songsCache);

        return externalLinksCache;
    }
}
