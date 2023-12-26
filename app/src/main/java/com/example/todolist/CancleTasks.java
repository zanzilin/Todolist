package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.todolist.Adapter.ToDoAdapter;
import com.example.todolist.Interface.ToDoAdapterListener;
import com.example.todolist.Model.ToDoModel;
import com.example.todolist.Utils.Database;

import java.util.ArrayList;

public class CancleTasks extends AppCompatActivity  {

    private Button btn_back;
    private ListView CancelTaskList;
    private Database database;
    private ArrayList<ToDoModel> arrayModel;
    private ToDoAdapter adapter;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancle_tasks);

        btn_back = findViewById(R.id.btn_back);
        CancelTaskList = findViewById(R.id.lv_impTask);

        arrayModel = new ArrayList<>();
        adapter = new ToDoAdapter(CancleTasks.this, R.layout.t_layout, arrayModel);
        adapter.setCancelTasksScreen(true);
        CancelTaskList.setAdapter(adapter);

        getDataCanceled();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void getDataCanceled(){
        database = new Database(this, "note.sqlite", null, 2);
        Cursor dataTodo = database.GetData("SELECT * FROM deleted_Task");
        arrayModel.clear();
        while (dataTodo.moveToNext()){
            int id = dataTodo.getInt(0);
            String status = dataTodo.getString(1);
            String date = dataTodo.getString(2);
            String time = dataTodo.getString(3);
            arrayModel.add(new ToDoModel(id, status, date, time));
        }
        adapter.notifyDataSetChanged();
    }


}