package com.example.todolist.Tasks;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.todolist.Adapter.ToDoAdapter;
import com.example.todolist.Interface.ToDoAdapterListener;
import com.example.todolist.Model.ToDoModel;
import com.example.todolist.R;

import java.util.ArrayList;

public class ImportantTasks extends AppCompatActivity {

    private Button btn_back;
    private ListView importantTaskList;
    private ArrayList<ToDoModel> importantTasks;
    private ToDoAdapter importantAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_important_tasks);

        btn_back = findViewById(R.id.btn_back);
        importantTaskList = findViewById(R.id.lv_impTask);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
       importantTasks = intent.getParcelableArrayListExtra("importantTasks");

        // Thiết lập Adapter cho ListView

        importantAdapter = new ToDoAdapter(this, R.layout.t_layout, importantTasks);
        importantAdapter.setFlag(true);
        importantAdapter.setChecked(true);
        importantAdapter.setItemViewClickable(false);
        importantTaskList.setAdapter(importantAdapter);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}