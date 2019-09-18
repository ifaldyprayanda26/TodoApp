package com.apps.ifaldyprayanda.todoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import static android.provider.BaseColumns._ID;
import static com.apps.ifaldyprayanda.todoapp.DatabaseContract.TABLE_TODO;
import static com.apps.ifaldyprayanda.todoapp.DatabaseContract.TodoColumns.DATE;
import static com.apps.ifaldyprayanda.todoapp.DatabaseContract.TodoColumns.DESCRIPTION;
import static com.apps.ifaldyprayanda.todoapp.DatabaseContract.TodoColumns.TITLE;

public class TodoHelper {
    private static final String DATABASE_NAME = TABLE_TODO;
    private  static DatabaseHelper databaseHelper;
    private static TodoHelper INSTANCE;

    private static SQLiteDatabase database;

    // kelas TodoHelper merupakan Singleton sebuah objek yang ditandai dengan adanya private modifier pada konstruktornya
    private TodoHelper(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    // method yang digunakan untuk menginisiasi Database
    public static TodoHelper getInstance(Context context)
    {
        if(INSTANCE == null)
        {
            synchronized (SQLiteOpenHelper.class)
            {
                if (INSTANCE == null)
                {
                    INSTANCE = new TodoHelper(context);
                }
            }
        }
        return INSTANCE;
    }

    public void open() throws SQLException
    {
        database = databaseHelper.getWritableDatabase();
    }

    public void close()
    {
        databaseHelper.close();

        if(database.isOpen())
            database.close();
    }

    public ArrayList<Todo> getAllTodo()
    {
        ArrayList<Todo> arrayList = new ArrayList<>();
        Cursor cursor  = database.query(DATABASE_NAME, null, null, null, null, null, _ID + " ASC", null);
        cursor.moveToFirst();
        Todo todo;
        if (cursor.getCount() > 0)
        {
            do{
                todo = new Todo();
                todo.setId(cursor.getInt(cursor.getColumnIndexOrThrow(_ID)));
                todo.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(TITLE)));
                todo.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)));
                todo.setDate(cursor.getString(cursor.getColumnIndexOrThrow(DATE)));

                arrayList.add(todo);
                cursor.moveToNext();
            }while(!cursor.isAfterLast());
        }
        cursor.close();
        return arrayList;
    }
    // method yang digunakan untuk memasukkan data kedalam database
    public long insertTodo(Todo todo) {
        ContentValues args = new ContentValues();
        args.put(TITLE, todo.getTitle());
        args.put(DESCRIPTION, todo.getDescription());
        args.put(DATE, todo.getDate());
        return database.insert(DATABASE_NAME, null, args);
    }

    // method yang digunakan untuk mengubah / mengupdate data
    public int updateTodo(Todo todo) {
        ContentValues args = new ContentValues();
        args.put(TITLE, todo.getTitle());
        args.put(DESCRIPTION, todo.getDescription());
        args.put(DATE, todo.getDate());
        return database.update(DATABASE_NAME, args, _ID + "= '" + todo.getId() + "'", null);
    }
    // method yang digunakan untuk menghapus data
    public int deleteTodo(int id) {
        return database.delete(TABLE_TODO, _ID + " = '" + id + "'", null);
    }
}
