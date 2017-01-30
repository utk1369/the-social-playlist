package com.thesocialplaylist.user.music.manager;

import android.util.Log;

import com.thesocialplaylist.user.music.api.declaration.UserApi;
import com.thesocialplaylist.user.music.dto.UserDTO;
import com.thesocialplaylist.user.music.dto.builders.UserDTOBuilder;
import com.thesocialplaylist.user.music.dto.extractors.UserDTOExtractor;
import com.thesocialplaylist.user.music.sqlitedbcache.dao.UserRelCacheDAO;
import com.thesocialplaylist.user.music.sqlitedbcache.model.UserRelCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 27-01-2017.
 */

public class UserDataAndRelationsManager {

    private UserApi userApi;

    private UserRelCacheDAO userRelCacheDAO;

    public UserApi getUserApi() {
        return userApi;
    }

    public UserDataAndRelationsManager(UserApi userApi, UserRelCacheDAO userRelCacheDAO) {
        this.userRelCacheDAO = userRelCacheDAO;
        this.userApi = userApi;
    }

    public UserDTO getAppUserData() {
        List<UserRelCache> userRelCaches = userRelCacheDAO.getAllUserInfoWithRelTp(UserRelCache.RELATIONSHIP_TYPES.APP_USER);
        return UserDTOBuilder.populate(userRelCaches);
    }

    public List<UserDTO> getAllFriendsData() {
        List<UserDTO> friendDTOs = new ArrayList<>();
        List<UserRelCache> userRelCaches = userRelCacheDAO.getAllUserInfoWithRelTp(UserRelCache.RELATIONSHIP_TYPES.FRIEND);
        if(userRelCaches == null)
            return friendDTOs;
        Map<String, List<UserRelCache>> friendsMap = new HashMap<>();
        for(UserRelCache userRelCache: userRelCaches) {
            if(!friendsMap.containsKey(userRelCache.getFbId()))
                friendsMap.put(userRelCache.getFbId(), new ArrayList<UserRelCache>());
            friendsMap.get(userRelCache.getFbId()).add(userRelCache);
        }

        for(Map.Entry<String, List<UserRelCache>> entry: friendsMap.entrySet()) {
            UserDTO friend = UserDTOBuilder.populate(entry.getValue());
            friendDTOs.add(friend);
        }
        return friendDTOs;
    }

    public void saveUserDetailsToCache(UserDTO userDTO, UserRelCache.RELATIONSHIP_TYPES relTp) {
        List<UserRelCache> existingUserRelCaches = userRelCacheDAO.getAllUserInfoWithUserId(userDTO.getId());
        List<UserRelCache> newUserRelCaches = UserDTOExtractor.extractListOfUserRelCachesFromUserDTO(userDTO, relTp);

        if(existingUserRelCaches == null || existingUserRelCaches.size() == 0) {
            Log.i("USER_DATA_AND_REL_MGR", "Inserting entries for user");
            userRelCacheDAO.save(newUserRelCaches);
        } else {
            //Might have to update or insert after comparing with existing entries
            for(UserRelCache newUserRelCache: newUserRelCaches) {
                Boolean isPresent = false;
                for(UserRelCache existingUserRelCache: existingUserRelCaches) {
                    if(existingUserRelCache.getTypeCd().equals(newUserRelCache.getTypeCd())) {
                        if(!existingUserRelCache.getValue().equals(newUserRelCache.getValue())) {
                            //to check if entries are getting updated or inserted.
                            Log.i("USER_DATA_AND_REL_MGR", "Updating " + newUserRelCache.getTypeCd());
                            userRelCacheDAO.save(newUserRelCache);
                        }
                        isPresent = true;
                    }
                }
                if(!isPresent) {
                    Log.i("USER_DATA_AND_REL_MGR", "Inserting " + newUserRelCache.getTypeCd());
                    userRelCacheDAO.save(newUserRelCache);
                }
            }
        }
    }
}
