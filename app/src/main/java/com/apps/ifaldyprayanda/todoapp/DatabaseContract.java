package com.apps.ifaldyprayanda.todoapp;

import android.provider.BaseColumns;

public class DatabaseContract {
    static String TABLE_TODO = "todo";

    static final class TodoColumns implements BaseColumns
    {
        static String TITLE = "title";
        static String DESCRIPTION = "description";
        static String DATE = "date";

    }
}
