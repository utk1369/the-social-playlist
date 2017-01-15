package com.thesocialplaylist.user.music.manager;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.thesocialplaylist.user.music.dto.PlaylistDTO;
import com.thesocialplaylist.user.music.dto.SongDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by utk on 10-04-2016.
 */
public class PlaylistManager {

    private static final Uri playlistUri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;

    public static String getPlaylistId(ContentResolver resolver, String playlistName) {
        String id = null;
        Cursor cursor = resolver.query(playlistUri,
                new String[] {MediaStore.Audio.Playlists._ID},
                MediaStore.Audio.Playlists.NAME + "=?",
                new String[] {playlistName}, null);

        if(cursor != null) {
            if(cursor.moveToFirst()) {
                // if there is one result
                id = cursor.getString(0);
            }
            cursor.close();
        }
        return id;
    }

    public static String createNewPlaylist(ContentResolver resolver, String playlistName) {
        ContentValues insertValues = new ContentValues();
        insertValues.put(MediaStore.Audio.Playlists.NAME, playlistName);
        insertValues.put(MediaStore.Audio.Playlists.DATE_ADDED, System.currentTimeMillis());
        insertValues.put(MediaStore.Audio.Playlists.DATE_MODIFIED, System.currentTimeMillis());

        Uri insertUri = resolver.insert(playlistUri, insertValues);
        String id = insertUri.getLastPathSegment();
        return id;
    }

    public static List<PlaylistDTO> getAllPlaylists(ContentResolver resolver) {
        List<PlaylistDTO> allPlaylistDTOs = new ArrayList<>();
        Cursor playlistCursor = resolver.query(playlistUri,
                new String[]{MediaStore.Audio.Playlists._ID, MediaStore.Audio.Playlists.NAME, MediaStore.Audio.Playlists.DATE_ADDED, MediaStore.Audio.Playlists.DATE_MODIFIED},
                null, null, null);

        while(playlistCursor.moveToNext()) {
            PlaylistDTO playlistDTO = new PlaylistDTO();
            playlistDTO.setId(Integer.parseInt(playlistCursor.getString(0)));
            playlistDTO.setName(playlistCursor.getString(1));
            playlistDTO.setDateAdded(Long.parseLong(playlistCursor.getString(2)));
            playlistDTO.setDateModified(Long.parseLong(playlistCursor.getString(3)));
            allPlaylistDTOs.add(playlistDTO);
        }
        return allPlaylistDTOs;
    }

    public static boolean addTracksToPlaylist(ContentResolver resolver, List<SongDTO> tracks, String playlistId) {
        int n = tracks.size();
        ContentValues[] insertValues = new ContentValues[n];
        for(int i=0; i<n; i++) {
            insertValues[i] = new ContentValues();
            insertValues[i].put(MediaStore.Audio.Playlists.Members.AUDIO_ID, tracks.get(i).getId());
        }
        Uri memberUri = MediaStore.Audio.Playlists.Members.getContentUri("external", Long.parseLong(playlistId));
        int nTracksAdded = resolver.bulkInsert(memberUri, insertValues);
        return nTracksAdded == n;
    }

    public static Cursor getTracks(ContentResolver resolver, String playlistId) {
        Uri playlistMemberUri = MediaStore.Audio.Playlists.Members.getContentUri("external", Long.parseLong(playlistId));
        String[] projections = new String[] {
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ARTIST_ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Playlists.Members.AUDIO_ID,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media._ID
        };
        Cursor playlistTracks = resolver.query(playlistMemberUri, projections, null, null,  MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER);
        return playlistTracks;
    }
}
