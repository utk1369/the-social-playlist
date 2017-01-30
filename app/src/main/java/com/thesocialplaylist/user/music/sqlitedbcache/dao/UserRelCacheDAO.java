package com.thesocialplaylist.user.music.sqlitedbcache.dao;

import com.activeandroid.Cache;
import com.activeandroid.query.Select;
import com.activeandroid.util.SQLiteUtils;
import com.thesocialplaylist.user.music.sqlitedbcache.model.UserRelCache;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 27-01-2017.
 */

public class UserRelCacheDAO {

    public List<UserRelCache> getAllUserInfoWithRelTp(UserRelCache.RELATIONSHIP_TYPES relTp) {
        return new Select().from(UserRelCache.class).where(" rel_tp = ?", relTp).execute();
    }

    public List<UserRelCache> getAllUserInfoWithUserId(String userId) {
        return new Select().from(UserRelCache.class).where(" user_id = ?", userId).execute();
    }

    public List<UserRelCache> getAllUserInfoWithFbId(String fbId) {
        return new Select().from(UserRelCache.class).where(" fb_id = ?", fbId).execute();
    }

    public Long save(UserRelCache userRelCache) {
        return userRelCache.save();
    }

    public List<Long> save(List<UserRelCache> userRelCaches) {
        List<Long> mIds = new ArrayList<>();
        for(UserRelCache userRelCache: userRelCaches) {
            mIds.add(save(userRelCache));
        }
        return mIds;
    }

    //to be removed from production
    public void recreateTable() {
        SQLiteUtils.execSql("DROP TABLE user_rel");
        SQLiteUtils.execSql(SQLiteUtils.createTableDefinition(Cache.getTableInfo(UserRelCache.class)));
    }
}
