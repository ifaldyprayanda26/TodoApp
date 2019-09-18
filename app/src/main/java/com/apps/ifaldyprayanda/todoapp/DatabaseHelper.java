package com.apps.ifaldyprayanda.todoapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "dbtodo";

    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_TODO_NAME = String.format("CREATE TABLE %s" + " (%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL)",
            DatabaseContract.TABLE_TODO,
            DatabaseContract.TodoColumns._ID,
            DatabaseContract.TodoColumns.TITLE,
            DatabaseContract.TodoColumns.DESCRIPTION,
            DatabaseContract.TodoColumns.DATE
    );

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // digunakan untuk membuat tabel pada database
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_TODO_NAME);
    }

    //    digunakan ketika ada upgrade versi database terjadi
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_TODO);
        onCreate(db);
    }
}
