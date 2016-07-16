package com.example.user.music;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MediaPlayerBroadcastReceiver extends BroadcastReceiver {
    public MediaPlayerBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i("Action Received", action);
    }
}
