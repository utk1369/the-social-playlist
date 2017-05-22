package com.thesocialplaylist.user.music.manager;

import android.util.Log;

import com.thesocialplaylist.user.music.api.declaration.UserApi;
import com.thesocialplaylist.user.music.dto.FriendDTO;
import com.thesocialplaylist.user.music.dto.SongDTO;
import com.thesocialplaylist.user.music.dto.UserDTO;
import com.thesocialplaylist.user.music.dto.builders.UserDTOBuilder;
import com.thesocialplaylist.user.music.dto.extractors.UserDTOExtractor;
import com.thesocialplaylist.user.music.enums.PlaybackEvent;
import com.thesocialplaylist.user.music.events.models.SyncTracksEvent;
import com.thesocialplaylist.user.music.events.models.TrackPlaybackEvent;
import com.thesocialplaylist.user.music.sqlitedbcache.dao.UserRelCacheDAO;
import com.thesocialplaylist.user.music.sqlitedbcache.model.UserRelCache;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 27-01-2017.
 */

public class UserDataAndRelationsManager {

    private UserApi userApi;

    private UserRelCacheDAO userRelCacheDAO;

    private MusicLibraryManager musicLibraryManager;

    public UserApi getUserApi() {
        return userApi;
    }

    public MusicLibraryManager getMusicLibraryManager() {
        return musicLibraryManager;
    }

    public UserDataAndRelationsManager(UserApi userApi, UserRelCacheDAO userRelCacheDAO, MusicLibraryManager musicLibraryManager) {
        this.userRelCacheDAO = userRelCacheDAO;
        this.userApi = userApi;
        this.musicLibraryManager = musicLibraryManager;
        EventBus.getDefault().register(this);
    }

    public void recreateTable() {
        userRelCacheDAO.recreateTable();
    }

    public UserDTO getAppUserDataFromCache() {
        List<UserRelCache> userRelCaches = userRelCacheDAO.getAllUserInfoWithRelTp(UserRelCache.RELATIONSHIP_TYPES.APP_USER);
        if(userRelCaches == null || userRelCaches.size() == 0)
            return null;
        return UserDTOBuilder.populate(userRelCaches);
    }

    public Map<String, UserDTO> getAllUserDTOsFromCacheAsMap(List<UserRelCache> userRelCaches) {
        Map<String, UserDTO> usersMap= new HashMap<>();
        if(userRelCaches == null)
            return usersMap;
        Map<String, List<UserRelCache>> usersCacheMap = new HashMap<>();
        for(UserRelCache userRelCache: userRelCaches) {
            if(!usersCacheMap.containsKey(userRelCache.getFbId()))
                usersCacheMap.put(userRelCache.getFbId(), new ArrayList<UserRelCache>());
            usersCacheMap.get(userRelCache.getFbId()).add(userRelCache);
        }

        for(Map.Entry<String, List<UserRelCache>> entry: usersCacheMap.entrySet()) {
            UserDTO userDTO = UserDTOBuilder.populate(entry.getValue());
            usersMap.put(userDTO.getId(), userDTO);
        }
        return usersMap;
    }

    public Map<String, UserDTO> getAllUsersFromCacheAsMap() {
        List<UserRelCache> userRelCaches = userRelCacheDAO.getAllUserInfoWithRelTp(UserRelCache.RELATIONSHIP_TYPES.APP_USER);
        userRelCaches.addAll(userRelCacheDAO.getAllUserInfoWithRelTp(UserRelCache.RELATIONSHIP_TYPES.FRIEND));

        return getAllUserDTOsFromCacheAsMap(userRelCaches);
    }

    public List<FriendDTO> getAllFriendsDataFromCacheAsList() {
        List<FriendDTO> friendDTOs = new ArrayList<>();
        List<UserRelCache> userRelCaches = userRelCacheDAO.getAllUserInfoWithRelTp(UserRelCache.RELATIONSHIP_TYPES.FRIEND);
        Map<String, UserDTO> friendsMap = getAllUserDTOsFromCacheAsMap(userRelCaches);

        for(Map.Entry<String, UserDTO> entry: friendsMap.entrySet()) {
            FriendDTO friend = new FriendDTO();
            friend.setFbId(entry.getValue().getFbId());
            friend.setFriend(entry.getValue());
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
                            UserRelCache userRelCacheToBeUpdated = userRelCacheDAO.getUserRelCacheByDefaultId(existingUserRelCache.getId());
                            userRelCacheToBeUpdated.setValue(newUserRelCache.getValue());
                            userRelCacheDAO.save(userRelCacheToBeUpdated);
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

    public void saveSongsToServer(String userId, List<SongDTO> songsToBeSyncedToServer, Callback<List<SongDTO>> callback) {
        Call<List<SongDTO>> syncSongsCall = userApi.saveSongs(userId, songsToBeSyncedToServer);
        syncSongsCall.enqueue(callback);
    }

    public void syncDbSongsToServer() {
        final UserDTO userDTO = getAppUserDataFromCache();
        List<SongDTO> songsToBeSynced = musicLibraryManager.getAllSongsToBeSynced();
        Log.i("USER_DATA_AND_REL_MGR", "No of songs to be synced: " + songsToBeSynced.size());
        saveSongsToServer(userDTO.getId(), songsToBeSynced, new Callback<List<SongDTO>>() {
            @Override
            public void onResponse(Call<List<SongDTO>> call, Response<List<SongDTO>> response) {
                Log.i("USER_DATA_AND_REL_MGR", "Songs Sync success.");
                List<SongDTO> updatedListOfSongs = response.body();
                if(updatedListOfSongs == null) {
                    Log.e("USER_DATA_AND_REL_MGR", "Songs Sync failed.");
                } else {
                    musicLibraryManager.updateSyncStatus(updatedListOfSongs, true);
                }
            }

            @Override
            public void onFailure(Call<List<SongDTO>> call, Throwable t) {
                Log.i("USER_DATA_AND_REL_MGR", "Songs Sync failed.");
            }
        });
    }

    //Event Subscribers
    @Subscribe(threadMode =  ThreadMode.BACKGROUND)
    public void onTrackChange(TrackPlaybackEvent trackPlaybackEvent) {
        if(trackPlaybackEvent.getPlaybackEvent().equals(PlaybackEvent.NEW_TRACK)) {
            SongDTO songDTO = trackPlaybackEvent.getSongDTO();
            songDTO.setLastListenedAt(new Timestamp(System.currentTimeMillis()));
            songDTO.setHits(songDTO.getHits() == null ? 1 : (songDTO.getHits() + 1));
            musicLibraryManager.saveSongDetailsToCache(songDTO);
            Log.i("Saved", songDTO.getMetadata().getTitle());
            EventBus.getDefault().post(new SyncTracksEvent());
        }
    }

    @Subscribe(threadMode =  ThreadMode.BACKGROUND)
    public void onTracksSyncRequest(SyncTracksEvent syncTracksEvent) {
        syncDbSongsToServer();
    }
}
