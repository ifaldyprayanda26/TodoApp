package com.apps.ifaldyprayanda.todoapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.apps.ifaldyprayanda.todoapp.TodoAddUpdateActivity.REQUEST_UPDATE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LoadTodoCallback {

    private RecyclerView rvTodo;
    private ProgressBar progressBar;
    private FloatingActionButton fabAdd;
    private static final String EXTRA_STATE = "EXTRA_STATE";
    private TodoAdapter adapter;
    private TodoHelper todoHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Todo");
        rvTodo = findViewById(R.id.rv_todo);
        rvTodo.setLayoutManager(new LinearLayoutManager(this));
        rvTodo.setHasFixedSize(true);

        todoHelper = TodoHelper.getInstance(getApplicationContext());

        todoHelper.open();

        progressBar = findViewById(R.id.progressbar);
        fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(this);

        adapter = new TodoAdapter(this);
        rvTodo.setAdapter(adapter);

        if (savedInstanceState == null) {
            new LoadTodoAsync(todoHelper, this).execute();
        } else {
            ArrayList<Todo> list = savedInstanceState.getParcelableArrayList(EXTRA_STATE);
            if (list != null) {
                adapter.setListTodo(list);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA_STATE, adapter.getListTodo());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab_add) {
            Intent intent = new Intent(MainActivity.this, TodoAddUpdateActivity.class);
            startActivityForResult(intent, TodoAddUpdateActivity.REQUEST_ADD);
        }
    }

    @Override
    public void preExecute() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void postExecute(ArrayList<Todo> todo) {
        progressBar.setVisibility(View.INVISIBLE);
        adapter.setListTodo(todo);
    }

    private static class LoadTodoAsync extends AsyncTask<Void, Void, ArrayList<Todo>> {
        private final WeakReference<TodoHelper> weakTodoHelper;
        private final WeakReference<LoadTodoCallback> weakCallback;

        private LoadTodoAsync(TodoHelper todoHelper, LoadTodoCallback callback) {
            weakTodoHelper = new WeakReference<>(todoHelper);
            weakCallback = new WeakReference<>(callback);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            weakCallback.get().preExecute();
        }

        @Override
        protected ArrayList<Todo> doInBackground(Void... voids) {
            return weakTodoHelper.get().getAllTodo();
        }

        @Override
        protected void onPostExecute(ArrayList<Todo> todo) {
            super.onPostExecute(todo);
            weakCallback.get().postExecute(todo);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (requestCode == TodoAddUpdateActivity.REQUEST_ADD) {
                if (resultCode == TodoAddUpdateActivity.RESULT_ADD) {
                    Todo todo = data.getParcelableExtra(TodoAddUpdateActivity.EXTRA_TODO);
                    adapter.addItem(todo);
                    rvTodo.smoothScrollToPosition(adapter.getItemCount() - 1);
                    showSnackbarMessage("One item Success added");
                }
            } else if (requestCode == REQUEST_UPDATE) {
                if (resultCode == TodoAddUpdateActivity.RESULT_UPDATE) {
                    Todo todo = data.getParcelableExtra(TodoAddUpdateActivity.EXTRA_TODO);
                    int position = data.getIntExtra(TodoAddUpdateActivity.EXTRA_POSITION, 0);
                    adapter.updateItem(position, todo);
                    rvTodo.smoothScrollToPosition(position);
                    showSnackbarMessage("One Item Success changed");
                } else if (resultCode == TodoAddUpdateActivity.RESULT_DELETE) {
                    int position = data.getIntExtra(TodoAddUpdateActivity.EXTRA_POSITION, 0);
                    adapter.removeItem(position);
                    showSnackbarMessage("One Item Success Deleted");
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        todoHelper.close();
    }

    private void showSnackbarMessage(String message) {
        Snackbar.make(rvTodo, message, Snackbar.LENGTH_SHORT).show();
    }
}
