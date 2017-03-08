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

    public UserDataAndRelationsManager(UserApi userApi, UserRelCacheDAO userRelCacheDAO, MusicLibraryManager musicLibraryManager) {
        this.userRelCacheDAO = userRelCacheDAO;
        this.userApi = userApi;
        this.musicLibraryManager = musicLibraryManager;
        EventBus.getDefault().register(this);
    }

    public UserDTO getAppUserDataFromCache() {
        List<UserRelCache> userRelCaches = userRelCacheDAO.getAllUserInfoWithRelTp(UserRelCache.RELATIONSHIP_TYPES.APP_USER);
        return UserDTOBuilder.populate(userRelCaches);
    }

    public List<FriendDTO> getAllFriendsDataFromCache() {
        List<FriendDTO> friendDTOs = new ArrayList<>();
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
            FriendDTO friend = new FriendDTO();
            UserDTO userDTO = UserDTOBuilder.populate(entry.getValue());
            friend.setFbId(userDTO.getFbId());
            friend.setFriend(userDTO);
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

    public void syncSongsForUser() {
        final UserDTO userDTO = getAppUserDataFromCache();
        List<SongDTO> songsToBeSynced = musicLibraryManager.getAllSongsToBeSynced();
        Call<List<SongDTO>> syncSongs = userApi.saveSongs(userDTO.getId(), songsToBeSynced);
        syncSongs.enqueue(new Callback<List<SongDTO>>() {
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
        syncSongsForUser();
    }
}
