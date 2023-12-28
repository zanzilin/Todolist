package com.example.todolist.Tasks;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.todolist.Adapter.ToDoAdapter;
import com.example.todolist.Model.ToDoModel;
import com.example.todolist.R;

import java.util.ArrayList;

public class DoneTasks extends AppCompatActivity {
    private Button btn_back;
    private ListView doneTaskList;
    private ArrayList<ToDoModel> doneTasks;
    private ToDoAdapter doneAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done_tasks);

        btn_back = findViewById(R.id.btn_back);
        doneTaskList = findViewById(R.id.lv_impTask);

        Intent intent = getIntent();
        doneTasks = intent.getParcelableArrayListExtra("selectedTasks");
        doneAdapter = new ToDoAdapter(this, R.layout.t_layout, doneTasks);
        //doneAdapter.setFlag(true);
       doneAdapter.setChecked(true);
        doneAdapter.setItemViewClickable(false);
        doneTaskList.setAdapter(doneAdapter);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}