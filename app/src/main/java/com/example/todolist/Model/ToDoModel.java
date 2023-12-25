package com.example.todolist.Model;

public class ToDoModel {
    private int id;
    private String status, date, time;

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
}
