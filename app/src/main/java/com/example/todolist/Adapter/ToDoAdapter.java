package com.example.todolist.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.todolist.MainActivity;
import com.example.todolist.Model.ToDoModel;
import com.example.todolist.R;
import com.example.todolist.Interface.ToDoAdapterListener;

import java.util.ArrayList;
import java.util.List;

public class ToDoAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<ToDoModel> taskList;
    private ToDoAdapterListener listener;
    private boolean isCancelTasksScreen;



    public  ToDoAdapter(MainActivity context, int layout, List<ToDoModel> taskList, ToDoAdapterListener listener){
        this.context = context;
        this.layout = layout;
        this.taskList = taskList;
        this.listener = listener;
    }
    public ToDoAdapter(Context context, int layout, List<ToDoModel> taskList) {
        this.context = context;
        this.layout = layout;
        this.taskList = taskList;
    }
    private boolean isChecked,isFlag, isItemViewClickable = true;
    private List<ToDoModel> checkedTasks = new ArrayList<>();
    public void setCancelTasksScreen(boolean isCancelTasksScreen) {
        this.isCancelTasksScreen = isCancelTasksScreen;
    }
    public void setItemViewClickable(boolean isClickable) {
        isItemViewClickable = isClickable;
    }
    public void setFlag(boolean isFlag){
        this.isFlag = isFlag;
    }
    public void setChecked(boolean isChecked){
        this.isChecked = isChecked;
    }

    private List<ToDoModel> importantTasks = new ArrayList<>();


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
        ImageView imgFlag;
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

            holder.imgDelete = (ImageView) convertView.findViewById(R.id.imageviewDelete);
            holder.imgFlag = (ImageView) convertView.findViewById(R.id.iv_flag);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox_id);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }




        final ToDoModel model = taskList.get(position);
        holder.txtStatus.setText(model.getStatus());
        holder.txtDate.setText(model.getDate());
        holder.txtTime.setText(model.getTime());



        if (isFlag){
            holder.imgFlag.setColorFilter(Color.RED);
        } else {
            holder.imgFlag.setColorFilter(Color.BLACK);
        }

        if (isChecked){
            holder.checkBox.setChecked(true);
            holder.txtStatus.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.txtDate.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.txtTime.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.checkBox.setChecked(false);
            holder.txtStatus.setPaintFlags(holder.txtStatus.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.txtDate.setPaintFlags(holder.txtDate.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.txtTime.setPaintFlags(holder.txtTime.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        if (!isItemViewClickable) {
            convertView.setEnabled(false);
            convertView.setClickable(false);
            holder.checkBox.setEnabled(false);
            holder.imgDelete.setEnabled(false);
            holder.imgFlag.setEnabled(false);
        } else {
            convertView.setEnabled(true);
            convertView.setClickable(true);
            holder.checkBox.setEnabled(true);
            holder.imgDelete.setEnabled(true);
            holder.imgFlag.setEnabled(true);
        }

        if (isCancelTasksScreen) {
            holder.checkBox.setEnabled(false);
            holder.imgDelete.setEnabled(false);
            holder.imgFlag.setEnabled(false);
            convertView.setEnabled(false);
            convertView.setClickable(false);
        } else {
            holder.checkBox.setEnabled(true);
            holder.imgDelete.setEnabled(true);
            holder.imgFlag.setEnabled(true);
            convertView.setEnabled(true);
            convertView.setClickable(true);
        }



        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                listener.editTask(model.getStatus(), model.getDate(), model.getTime() ,model.getId());

            }
        });

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.deleteTask(model.getStatus(), model.getId(),model.getDate(), model.getTime());
            }
        });

        holder.imgFlag.setOnClickListener(new View.OnClickListener() {
            private boolean isRed = false;
            @Override
            public void onClick(View v) {
                if(isRed){
                    holder.imgFlag.setColorFilter(Color.BLACK);
                    isRed = false;
                    model.setFlagged(false);
                }else {
                    holder.imgFlag.setColorFilter(Color.RED);
                    isRed = true;
                    model.setFlagged(true);
                }
                holder.imgFlag.invalidate();
            }
        });
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {

                    importantTasks.add(model);

                    holder.checkBox.setChecked(true);
                    holder.txtStatus.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.txtDate.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.txtTime.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                }
                else
                {
                    importantTasks.remove(model);

                    holder.checkBox.setChecked(false);
                    holder.txtStatus.setPaintFlags(holder.txtStatus.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    holder.txtDate.setPaintFlags(holder.txtDate.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    holder.txtTime.setPaintFlags(holder.txtTime.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
                boolean isChecked = ((CheckBox) v).isChecked();
                model.setChecked(isChecked);
            }
        });

        return convertView;
    }




}
