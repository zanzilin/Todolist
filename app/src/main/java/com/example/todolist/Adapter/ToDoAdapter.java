package com.example.todolist.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.MainActivity;
import com.example.todolist.Model.ToDoModel;
import com.example.todolist.R;

import java.util.ArrayList;
import java.util.List;

public class ToDoAdapter extends BaseAdapter {

    private MainActivity context;
    private int layout;
    private List<ToDoModel> taskList;

    public  ToDoAdapter(MainActivity context, int layout, List<ToDoModel> taskList){
        this.context = context;
        this.layout = layout;
        this.taskList = taskList;
    }

    @Override
    public int getCount() {
        return taskList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder{
        CheckBox checkBox;
        TextView txtStatus;
        TextView txtDate;
        TextView txtTime;
        ImageView imgDelete;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if(convertView == null){
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);

            holder.txtStatus = (TextView) convertView.findViewById(R.id.textviewTen);
            holder.txtDate = (TextView) convertView.findViewById(R.id.textviewDate);
            holder.txtTime = (TextView) convertView.findViewById(R.id.textviewTime);
//
            holder.imgDelete = (ImageView) convertView.findViewById(R.id.imageviewDelete);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox_id);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }



        final ToDoModel model = taskList.get(position);
        holder.txtStatus.setText(model.getStatus());
        holder.txtDate.setText(model.getDate());
        holder.txtTime.setText(model.getTime());



        //bắt sự kiện xóa và sửa
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.editTask(model.getStatus(), model.getDate(), model.getTime() ,model.getId());
            }
        });

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.deleteTask(model.getStatus(), model.getId());
            }
        });

        //bắt sự kiện checkbox
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    holder.checkBox.setChecked(true);
                    holder.txtStatus.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.txtDate.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.txtTime.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                }
                else
                {
                    holder.checkBox.setChecked(false);
                    holder.txtStatus.setPaintFlags(holder.txtStatus.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    holder.txtDate.setPaintFlags(holder.txtDate.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    holder.txtTime.setPaintFlags(holder.txtTime.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
            }
        });

        return convertView;
    }


}
