package com.example.todolist.Tasks;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.todolist.Adapter.ToDoAdapter;
import com.example.todolist.Interface.ToDoAdapterListener;
import com.example.todolist.MainActivity;
import com.example.todolist.Model.ToDoModel;
import com.example.todolist.R;
import com.example.todolist.Utils.Database;

import java.util.ArrayList;

public class CancleTasks extends AppCompatActivity implements ToDoAdapterListener {

    private Button btn_back, btn_Restore, btn_RestoreAll;
    private ListView CancelTaskList;
    private Database database;
    private ArrayList<ToDoModel> arrayModel;
    private ToDoAdapter adapter;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancle_tasks);

        btn_back = findViewById(R.id.btn_back);
        btn_Restore = findViewById(R.id.btn_restore);
        btn_RestoreAll = findViewById(R.id.btn_restoreAll);
        CancelTaskList = findViewById(R.id.lv_impTask);

        arrayModel = new ArrayList<>();
        adapter = new ToDoAdapter(CancleTasks.this, R.layout.t_layout, arrayModel, CancleTasks.this);
        adapter.setCancelTasksScreen(true);
        CancelTaskList.setAdapter(adapter);
        getDataCanceled();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_Restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < arrayModel.size(); i++) {
                    ToDoModel model = arrayModel.get(i);
                    if (model.isChecked()) {
                        // Xóa khỏi table deleted_Task
                        database.QueryData("DELETE FROM deleted_Task WHERE Id = '"+model.getId()+"'");
                        database.QueryData("INSERT INTO Task(Id, Status, Date, Time) VALUES (null, '" + model.getStatus() + "','"+model.getDate()+"','"+model.getDate()+"');");
                    }else{
                        Toast.makeText(CancleTasks.this, "Vui lòng check vào các Task cần được khôi phục",Toast.LENGTH_SHORT).show();
                    }
                }
                // Thực hiện cập nhật lại danh sách hiển thị
                getDataCanceled();
            }
        });


        btn_RestoreAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị hộp thoại thông báo trước khi khôi phục tất cả
                AlertDialog.Builder builder = new AlertDialog.Builder(CancleTasks.this);
                builder.setTitle("Khôi phục tất cả")
                        .setMessage("Bạn có chắc chắn muốn khôi phục tất cả các Task đã bị xóa?")
                        .setPositiveButton("Khôi phục", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                String selectQuery = "SELECT * FROM deleted_Task";
                                Cursor cursor = database.GetData(selectQuery);
                                // Chèn lại dữ liệu vào table Task
                                if (cursor.moveToNext()) {
                                    do {
                                        //int id = cursor.getInt(0);
                                        String status = cursor.getString(1);
                                        String date = cursor.getString(2);
                                        String time = cursor.getString(3);
                                        // Thực hiện câu truy vấn INSERT để chèn dữ liệu vào table Task
                                        String insertQuery = "INSERT INTO Task(Status, Date, Time) VALUES ('" + status + "', '" + date + "', '" + time + "')";
                                        database.QueryData(insertQuery);
                                    } while (cursor.moveToNext());
                                }
                                cursor.close();
                                database.QueryData("DELETE FROM deleted_Task");
                                getDataCanceled();
                            }
                        })
                        .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
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


    @Override
    public void deleteTask(String status, final int id, String date, String time){
        AlertDialog.Builder dialogXoa = new AlertDialog.Builder(this);
        dialogXoa.setMessage("Bạn có muốn xóa không?");

        dialogXoa.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                database.QueryData("DELETE FROM deleted_Task WHERE Id = '"+id+"'");
                Toast.makeText(CancleTasks.this, "Đã xóa",Toast.LENGTH_SHORT).show();
                getDataCanceled();
            }
        });

        dialogXoa.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogXoa.show();

    }
    @Override
    public void editTask(String status, String date, String time, final int id){}

}