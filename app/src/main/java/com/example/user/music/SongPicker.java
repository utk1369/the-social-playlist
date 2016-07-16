package com.example.user.music;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.user.music.activity.musicplayer.MediaPlayerActivity;
import com.example.user.music.models.Song;
import com.example.user.music.utils.AppUtil;

import java.util.List;

public class SongPicker extends Activity {
    List<Song> availableSongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_picker);
        displayListOfSongs();
    }

    private void displayListOfSongs() {
        availableSongs = AppUtil.getSongsAsList(this, null, null);
        ArrayAdapter adapter = new ArrayAdapter<Song>(this, R.layout.activity_songs_list_view, availableSongs);
        ListView songsListView = (ListView) findViewById(R.id.songsList);
        songsListView.setAdapter(adapter);
        songsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Item selected at index", position+"");
                Intent selectedSongIntent = new Intent(getApplicationContext(), MediaPlayerActivity.class);
                selectedSongIntent.putExtra("selectedSongIdx", position);
                setResult(RESULT_OK, selectedSongIntent);
                finish();
            }
        });
    }
}
