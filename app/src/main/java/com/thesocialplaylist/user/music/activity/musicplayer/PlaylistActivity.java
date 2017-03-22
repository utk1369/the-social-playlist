package com.thesocialplaylist.user.music.activity.musicplayer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.thesocialplaylist.user.music.dto.PlaylistDTO;
import com.thesocialplaylist.user.music.adapters.recyclerview.PlaylistRowAdapter;
import com.thesocialplaylist.user.music.R;
import com.thesocialplaylist.user.music.manager.PlaylistManager;

import java.util.List;

public class PlaylistActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton fab;
    private ActionBar actionBar;
    private RecyclerView playlistsView;
    private PlaylistRowAdapter adapter;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlists);
        initialize();
        assignActionsToButtons();
    }

    private void assignActionsToButtons() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(PlaylistActivity.this);
                View playlistCreatePrompt = inflater.inflate(R.layout.playlist_create_dialog, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PlaylistActivity.this);
                alertDialogBuilder.setView(playlistCreatePrompt);
                alertDialogBuilder.setTitle("New PlaylistDTO");
                final EditText newPlaylistName = (EditText) playlistCreatePrompt.findViewById(R.id.playlist_name);
                alertDialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String id = PlaylistManager.getPlaylistId(PlaylistActivity.this.getContentResolver(), newPlaylistName.getText().toString());
                        if (id == null) {
                            id = PlaylistManager.createNewPlaylist(PlaylistActivity.this.getContentResolver(), newPlaylistName.getText().toString());
                            initialize();
                            Toast.makeText(PlaylistActivity.this, "New PlaylistDTO Created!", Toast.LENGTH_LONG).show();
                        } else {
                            dialog.cancel();
                            Toast.makeText(PlaylistActivity.this, "A PlaylistDTO with the same name already exists!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = alertDialogBuilder.create();
                dialog.show();
            }
        });
    }

    private void initialize() {
        List<PlaylistDTO> allPlaylistDTOs = PlaylistManager.getAllPlaylists(getContentResolver());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Playlists");

        playlistsView = (RecyclerView) findViewById(R.id.playlists);
        adapter = new PlaylistRowAdapter(allPlaylistDTOs, getApplicationContext());
        layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        playlistsView.setLayoutManager(layoutManager);
        playlistsView.setAdapter(adapter);

        fab = (FloatingActionButton) findViewById(R.id.fab);
    }

}
