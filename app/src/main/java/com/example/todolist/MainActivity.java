package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.todolist.Adapter.ToDoAdapter;
import com.example.todolist.Interface.ToDoAdapterListener;
import com.example.todolist.Model.ToDoModel;

import com.example.todolist.Notification.AlarmReceiver;
import com.example.todolist.Tasks.CancleTasks;
import com.example.todolist.Tasks.DoneTasks;
import com.example.todolist.Tasks.ImportantTasks;
import com.example.todolist.Utils.Database;
import com.example.todolist.Notification.NotificationDetailActivity;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity implements ToDoAdapterListener {


    private Database database;
    private ListView taskList;
    private ImageView imgAdd, imgImportant, imgDone,imgCancle, imgExport;
    private TextView tvSortName, tvSortDate,tvReload;
    private EditText edtFindName;
    private Button btnFindName;

    private ArrayList<ToDoModel> arrayModel, searchResults, originalList;
    private ToDoAdapter adapter;
    private boolean isAscending, isAscending_date = true;


    @Override
    protected void onResume() {
        super.onResume();
        GetDataToDo();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       imgAdd =  findViewById(R.id.img_add);
       imgImportant = findViewById(R.id.img_imp);
       imgDone = findViewById(R.id.img_done);
       imgCancle = findViewById(R.id.img_cancle);
       taskList =  findViewById(R.id.tasksRecyclerView);
       tvSortName = findViewById(R.id.tv_sortName);
       tvSortDate = findViewById(R.id.tv_sortDate);
       tvReload = findViewById(R.id.tv_reload);
       edtFindName = findViewById(R.id.edt_findName);
       btnFindName = findViewById(R.id.btn_findName);
       imgExport = findViewById(R.id.img_export);



       searchResults = new ArrayList<>();
       arrayModel = new ArrayList<>();
       adapter = new ToDoAdapter(this, R.layout.t_layout, arrayModel, this);
       taskList.setAdapter(adapter);
       originalList = new ArrayList<>(arrayModel);


        // Khởi tạo đối tượng Database
        database = new Database(this, "note.sqlite", null, 2);
        database.QueryData("CREATE TABLE IF NOT EXISTS Task(Id INTEGER PRIMARY KEY AUTOINCREMENT, Status VARCHAR(200), Date VARCHAR(200), Time VARCHAR(200));");
        database.QueryData("CREATE TABLE IF NOT EXISTS deleted_Task(Id INTEGER PRIMARY KEY AUTOINCREMENT, Status VARCHAR(200), Date VARCHAR(200), Time VARCHAR(200));");

        GetDataToDo();





        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewTask();
            }
        });

        imgImportant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               openImportantTasks();
            }
        });
        imgDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDoneTasks();
            }
        });
        imgCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CancleTasks.class);
                startActivity(intent);
            }
        });
        tvReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetDataToDo();
            }
        });
        tvSortName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isAscending = !isAscending;
                Collections.sort(arrayModel, new Comparator<ToDoModel>() {
                    @Override
                    public int compare(ToDoModel task1, ToDoModel task2) {
                        if (isAscending) {
                            // Sắp xếp từ A-Z
                            return task1.getStatus().compareToIgnoreCase(task2.getStatus());
                        } else {
                            // Sắp xếp từ Z-A
                            return task2.getStatus().compareToIgnoreCase(task1.getStatus());
                        }
                    }
                });

                // Cập nhật lại adapter sau khi sắp xếp
                adapter.notifyDataSetChanged();
            }
        });
        tvSortDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isAscending_date = !isAscending_date;
                Collections.sort(arrayModel, new Comparator<ToDoModel>() {
                    @Override
                    public int compare(ToDoModel task1, ToDoModel task2) {
                        if (isAscending_date) {
                            // Sắp xếp từ cũ đến mới
                            return task1.getDate().compareTo(task2.getDate());
                        } else {
                            // Sắp xếp từ mới đến cũ
                            return task2.getDate().compareTo(task1.getDate());
                        }
                    }
                });

                // Cập nhật lại adapter sau khi sắp xếp
                adapter.notifyDataSetChanged();

            }
        });

        btnFindName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = edtFindName.getText().toString().toLowerCase();
                searchResults.clear();

                // Thực hiện tìm kiếm
                for (ToDoModel item : arrayModel) {
                    if (item.getStatus().toLowerCase().contains(searchText)) {
                        searchResults.add(item);
                    }
                }

                // Cập nhật danh sách arrayModel thành searchResults
                originalList = new ArrayList<>(searchResults);
                // Cập nhật adapter với danh sách mới

            }
        });
        imgExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Export Confirmation")
                        .setMessage("Bạn có muốn xuất file Excel?")
                        .setPositiveButton("Export", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                exportToExcel();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            }
        });


    }


    private void addNewTask(){

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.new_task);

        final EditText edtTen = (EditText) dialog.findViewById(R.id.editTextTenCV);
        final TextView txtDate = (TextView) dialog.findViewById(R.id.textDateCV);
        final TextView txtTime = (TextView) dialog.findViewById(R.id.textTimeCV);

        Button btnThem = (Button) dialog.findViewById(R.id.buttonThem);
        Button btnHuy =  (Button) dialog.findViewById(R.id.buttonHuy);

        txtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickTime(txtTime, edtTen);
            }
        });
        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate(txtDate);
            }
        });

        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = edtTen.getText().toString();
                String date = txtDate.getText().toString();
                String time = txtTime.getText().toString();

                //kiểm tra không nhập gì vào ô editText
                if(status.equals("")){
                    Toast.makeText(MainActivity.this, "Vui lòng nhập tiêu đề công việc.",Toast.LENGTH_SHORT).show();
                }else {
                    //insert database
                    database.QueryData("INSERT INTO Task (Id, Status, Date, Time) VALUES (null, '" + status + "','"+date+"','"+time+"');");
                    Toast.makeText(MainActivity.this, "Đã thêm.",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    GetDataToDo();
                }
            }
        });

        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();


    }

    @Override
    public void editTask(String status, String date, String time, final int id){

        final  Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.edit_task);

        final EditText edtStatus = (EditText) dialog.findViewById(R.id.editTextTenCV);
        final TextView tvDate = (TextView) dialog.findViewById(R.id.textDateCV_edt);
        final TextView tvTime = (TextView) dialog.findViewById(R.id.textTimeCV_edt);

        Button btnXacNhan = (Button) dialog.findViewById(R.id.buttonupdate);
        Button btnHuy = (Button) dialog.findViewById(R.id.buttonCancle);

        edtStatus.setText(status);
        tvDate.setText(date);
        tvTime.setText(time);


        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickTime(tvTime, edtStatus);
            }
        });
        //bắt sự kiện chọn ngày trong dialog
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate(tvDate);
            }
        });

        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String statusMoi = edtStatus.getText().toString().trim();
                String dateMoi = tvDate.getText().toString().trim();
                String timeMoi = tvTime.getText().toString().trim();

                database.QueryData("UPDATE Task SET Status ='"+statusMoi+"', Date = '"+dateMoi+"', Time = '"+timeMoi+"' WHERE Id = '"+ id +"'");
                Toast.makeText(MainActivity.this, "Đã cập nhập",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                GetDataToDo();
            }
        });
        dialog.show();

    }
    @Override
    public void deleteTask(String status, final int id, String date, String time){
        AlertDialog.Builder dialogXoa = new AlertDialog.Builder(this);
        dialogXoa.setMessage("Bạn có muốn xóa không?");

        dialogXoa.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                database.QueryData("DELETE FROM Task WHERE Id = '"+id+"'");
                database.QueryData("INSERT INTO deleted_Task(Id, Status, Date, Time) VALUES (null, '" + status + "','"+date+"','"+time+"');");
                Toast.makeText(MainActivity.this, "Đã xóa",Toast.LENGTH_SHORT).show();
                GetDataToDo();
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


    private void openImportantTasks() {
        Intent intent = new Intent(MainActivity.this, ImportantTasks.class);
        ArrayList<ToDoModel> importantTasks = new ArrayList<>();

        for (ToDoModel task : arrayModel) {
            if (task.isFlagged()) {
                importantTasks.add(task);

            }
        }
        intent.putParcelableArrayListExtra("importantTasks", (ArrayList<? extends Parcelable>) importantTasks);
        startActivity(intent);
    }
    private void openDoneTasks(){
        ArrayList<ToDoModel> selectedTasks = new ArrayList<>();
        for (ToDoModel task : arrayModel) {
            if (task.isChecked()) {
                selectedTasks.add(task);
            }
        }
        Intent intent = new Intent(MainActivity.this, DoneTasks.class);
        intent.putParcelableArrayListExtra("selectedTasks", (ArrayList<? extends Parcelable>) selectedTasks);
        startActivity(intent);

    }

    private void GetDataToDo(){

        Cursor dataTodo = database.GetData("SELECT * FROM Task");
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
    private void exportToExcel() {
        // Tạo một workbook mới
        Workbook workbook = new XSSFWorkbook();

        // Tạo một trang tính mới
        Sheet sheet = workbook.createSheet("Data");

        // Ghi dữ liệu từ arrayModel vào tệp Excel
        for (int i = 0; i < arrayModel.size(); i++) {
            ToDoModel task = arrayModel.get(i);

            Row row = sheet.createRow(i);
            Cell statusCell = row.createCell(0);
            statusCell.setCellValue(task.getStatus());

            Cell dateCell = row.createCell(1);
            dateCell.setCellValue(task.getDate());

            Cell timeCell = row.createCell(2);
            timeCell.setCellValue(task.getTime());
        }

        // Ghi workbook vào tệp Excel
        try {
            String filePath = getExternalFilesDir(null) + File.separator + "data.xlsx";
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            workbook.write(fileOutputStream);
            fileOutputStream.close();
            Toast.makeText(MainActivity.this, "Exported to Excel", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Export Failed", Toast.LENGTH_SHORT).show();
        }
    }





    private void pickTime(final TextView txtTime, final EditText edtTen) {
        final Calendar calendar = Calendar.getInstance();

        int gio = calendar.get(Calendar.HOUR_OF_DAY);
        int phut = calendar.get(Calendar.MINUTE);
        final TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                calendar.set(0,0,0,hourOfDay, minute);
                txtTime.setText(simpleDateFormat.format(calendar.getTime()));
                setMultipleAlarms(calendar, edtTen);
            }
        }, gio, phut, true);
        timePickerDialog.show();

    }
    private void pickDate(final TextView txtDate) {
        final Calendar calendar = Calendar.getInstance();
        int ngay = calendar.get(Calendar.DATE);
        int thang = calendar.get(Calendar.MONTH);
        int nam = calendar.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                calendar.set(year,month,dayOfMonth);
                txtDate.setText(simpleDateFormat.format(calendar.getTime()));
            }
        }, nam, thang, ngay);
        datePickerDialog.show();
    }

    public void setMultipleAlarms(Calendar calendar, EditText edtTen){

        //đặt chuông báo
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        // Tạo thông báo hiển thị trên màn hình
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int notification = NotificationManager.IMPORTANCE_DEFAULT;
            CharSequence name = "To Do List";
            NotificationChannel channel = new NotificationChannel("TDL_1", name, notification);
            channel.setDescription(edtTen.getText().toString());
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        addNotification(edtTen.getText().toString());

    }
    private void addNotification(String des) {
        String strTitle = "To Do List";
        String strMsg = des;
        Intent notificationIntent = new Intent(this, NotificationDetailActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.putExtra("message", strMsg);
        notificationIntent.putExtra("title", strTitle);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Hello")
                .setSmallIcon(R.drawable.baseline_timer_24)
                .setContentTitle(strTitle)
                .setContentText(strMsg)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setColor(Color.BLUE)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .addAction(R.mipmap.ic_launcher, "DỪNG", pendingIntent);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.notify(0, builder.build());
    }






}