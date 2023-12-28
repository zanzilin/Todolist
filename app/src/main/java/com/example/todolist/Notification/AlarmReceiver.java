package com.example.todolist.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.todolist.Notification.Music;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent myIntent = new Intent(context, Music.class);
        context.startService(myIntent);
    }
}
