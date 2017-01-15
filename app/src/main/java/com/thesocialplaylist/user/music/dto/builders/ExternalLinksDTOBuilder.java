package com.thesocialplaylist.user.music.dto.builders;

import com.thesocialplaylist.user.music.dto.ExternalLinksDTO;
import com.thesocialplaylist.user.music.enums.ExternalLinkType;
import com.thesocialplaylist.user.music.sqlitedbcache.model.ExternalLinksCache;

/**
 * Created by user on 17-10-2016.
 */

public class ExternalLinksDTOBuilder {
    public static ExternalLinksDTO populate(ExternalLinksCache externalLinksCache) {
        ExternalLinksDTO externalLinksDTO = new ExternalLinksDTO();
        externalLinksDTO.setId(externalLinksCache.getLinkItemId());
        externalLinksDTO.setLinkType(ExternalLinkType.valueOf(externalLinksCache.getLinkItemTp()));
        externalLinksDTO.setThumbnailUrl(externalLinksCache.getLinkItemThumbnailUrl());
        externalLinksDTO.setTitle(externalLinksCache.getLinkItemTitle());

        return externalLinksDTO;
    }
}
