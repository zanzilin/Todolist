package com.example.todolist.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class ToDoModel implements Parcelable {
    private int id;
    private String status, date, time;
    private boolean isChecked, isFlagged;

    public boolean isFlagged() {
        return isFlagged;
    }

    public void setFlagged(boolean flagged) {
        isFlagged = flagged;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isChecked(){
        return isChecked;
    }


    public ToDoModel(int id, String status, String date, String time){
        this.id = id;
        this.status= status;
        this.date=date;
        this.time = time;

    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }



    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // Ghi dữ liệu từ các biến thành viên vào Parcel
        dest.writeString(status);
        dest.writeString(date);
        dest.writeString(time);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ToDoModel> CREATOR = new Creator<ToDoModel>() {
        @Override
        public ToDoModel createFromParcel(Parcel in) {
            return new ToDoModel(in);
        }

        @Override
        public ToDoModel[] newArray(int size) {
            return new ToDoModel[size];
        }
    };

    protected ToDoModel(Parcel in) {
        // Đọc dữ liệu từ Parcel và gán vào các biến thành viên
        status = in.readString();
        date = in.readString();
        time = in.readString();

    }
}
