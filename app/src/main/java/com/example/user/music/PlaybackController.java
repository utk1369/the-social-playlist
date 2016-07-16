package com.example.user.music;

import android.content.Context;
import android.widget.MediaController;

/**
 * Created by user on 27-03-2016.
 */
public class PlaybackController extends MediaController {

    public PlaybackController(Context context) {
        super(context);
    }

    @Override
    public void hide() {
        //do noithing
    }
}
