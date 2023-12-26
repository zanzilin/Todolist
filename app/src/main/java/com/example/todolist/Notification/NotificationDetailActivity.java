package com.example.todolist.Notification;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todolist.R;

public class NotificationDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_notification_detail);
        Button btn_stop = (Button) findViewById(R.id.btn_stop);


    }
}
