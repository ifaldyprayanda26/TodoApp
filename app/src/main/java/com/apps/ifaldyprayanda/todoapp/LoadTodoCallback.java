package com.apps.ifaldyprayanda.todoapp;

import java.util.ArrayList;

public interface LoadTodoCallback {

    void preExecute();
    void postExecute(ArrayList<Todo> todo);
}
