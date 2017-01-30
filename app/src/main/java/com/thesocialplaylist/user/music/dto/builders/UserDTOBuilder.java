package com.thesocialplaylist.user.music.dto.builders;

import com.thesocialplaylist.user.music.dto.UserDTO;
import com.thesocialplaylist.user.music.sqlitedbcache.model.UserRelCache;

import java.util.List;

/**
 * Created by user on 28-01-2017.
 */

public class UserDTOBuilder {

    public static UserDTO populate(List<UserRelCache> userRelCaches) {
        UserDTO userDTO = new UserDTO();
        for(UserRelCache userRelCache: userRelCaches) {
            if (userRelCache.getTypeCd().equals(UserRelCache.TYPE_CODES.NAME))
                userDTO.setName(userRelCache.getValue());
            if (userRelCache.getTypeCd().equals(UserRelCache.TYPE_CODES.STATUS))
                userDTO.setStatus(userRelCache.getValue());
            if (userRelCache.getTypeCd().equals(UserRelCache.TYPE_CODES.IMAGE_URL))
                userDTO.setImageUrl(userRelCache.getValue());
        }
        return userDTO;
    }
}
