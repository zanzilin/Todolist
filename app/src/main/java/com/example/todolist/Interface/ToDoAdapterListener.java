package com.example.todolist.Interface;

public interface ToDoAdapterListener {

    void editTask(String status, String date, String time, int id);
   // void deleteTask(String status, int id);

    void deleteTask(String status, int id, String date, String time);
}
