package com.thesocialplaylist.user.music.dto.extractors;

import com.thesocialplaylist.user.music.dto.UserDTO;
import com.thesocialplaylist.user.music.sqlitedbcache.model.UserRelCache;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 28-01-2017.
 */

public class UserDTOExtractor {

    private static UserRelCache userRelCacheObjectCreator(String userId, String fbId,
                                                          UserRelCache.TYPE_CODES typeCd,
                                                          String value, UserRelCache.RELATIONSHIP_TYPES relTp) {
        UserRelCache userRelCache = new UserRelCache();
        userRelCache.setUserId(userId);
        userRelCache.setFbId(fbId);
        userRelCache.setTypeCd(typeCd);
        userRelCache.setValue(value);
        userRelCache.setRelTp(relTp);

        return userRelCache;
    }

    public static List<UserRelCache> extractListOfUserRelCachesFromUserDTO(UserDTO userDTO, UserRelCache.RELATIONSHIP_TYPES relTp) {
        List<UserRelCache> userRelCaches = new ArrayList<>();

        if(userDTO.getName() != null) {
            userRelCaches.add(
                    userRelCacheObjectCreator(userDTO.getId(), userDTO.getFbId(),
                            UserRelCache.TYPE_CODES.NAME, userDTO.getName(), relTp));
        }

        if(userDTO.getStatus() != null) {
            userRelCaches.add(
                    userRelCacheObjectCreator(userDTO.getId(), userDTO.getFbId(),
                            UserRelCache.TYPE_CODES.STATUS, userDTO.getStatus(), relTp));
        }

        if(userDTO.getImageUrl() != null) {
            userRelCaches.add(
                    userRelCacheObjectCreator(userDTO.getId(), userDTO.getFbId(),
                            UserRelCache.TYPE_CODES.IMAGE_URL, userDTO.getImageUrl(), relTp));
        }

        return userRelCaches;
    }
}
