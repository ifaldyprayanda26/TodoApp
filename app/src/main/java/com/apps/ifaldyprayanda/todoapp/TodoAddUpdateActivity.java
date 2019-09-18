package com.apps.ifaldyprayanda.todoapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TodoAddUpdateActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtTitle, edtDesc;
    private Button btnSubmit;

    public static final String EXTRA_TODO = "extra_todo";
    public static final String EXTRA_POSITION = "extra_position";
    private boolean isEdit = false;
    public static final int REQUEST_ADD = 100;
    public static final int RESULT_ADD = 101;
    public static final int REQUEST_UPDATE = 200;
    public static final int RESULT_UPDATE = 201;
    public static final int RESULT_DELETE = 301;
    private Todo todo;
    private int position;
    private TodoHelper todoHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_add_update);

        edtTitle = findViewById(R.id.edt_title);
        edtDesc = findViewById(R.id.edt_description);
        btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(this);

        todoHelper = TodoHelper.getInstance(getApplicationContext());

        todo = getIntent().getParcelableExtra(EXTRA_TODO);
        if (todo != null) {
            position = getIntent().getIntExtra(EXTRA_POSITION, 0);
            isEdit = true;
        } else {
            todo = new Todo();
        }

        String actionBarTitle;
        String btnTitle;

        if (isEdit) {
            actionBarTitle = "Changes ToDo";
            btnTitle = "Update";
            if (todo != null) {
                edtTitle.setText(todo.getTitle());
                edtDesc.setText(todo.getDescription());
            }
        } else {
            actionBarTitle = "Add ToDo";
            btnTitle = "Save";
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(actionBarTitle);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        btnSubmit.setText(btnTitle);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_submit) {
            String title = edtTitle.getText().toString().trim();
            String description = edtDesc.getText().toString().trim();
            if (TextUtils.isEmpty(title)) {
                edtTitle.setError("Field can not be blank");
                return;
            }
            todo.setTitle(title);
            todo.setDescription(description);
            Intent intent = new Intent();
            intent.putExtra(EXTRA_TODO, todo);
            intent.putExtra(EXTRA_POSITION, position);
            if (isEdit) {
                long result = todoHelper.updateTodo(todo);
                if (result > 0) {
                    setResult(RESULT_UPDATE, intent);
                    finish();
                } else {
                    Toast.makeText(TodoAddUpdateActivity.this, "Faile to Update Data", Toast.LENGTH_SHORT).show();
                }
            } else {
                todo.setDate(getCurrentDate());
                long result = todoHelper.insertTodo(todo);

                if (result > 0) {
                    todo.setId((int) result);
                    setResult(RESULT_ADD, intent);
                    finish();
                } else {
                    Toast.makeText(TodoAddUpdateActivity.this, "Failed to add Data", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isEdit) {
            getMenuInflater().inflate(R.menu.menu_form, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                showAlertDialog(ALERT_DIALOG_DELETE);
                break;
            case android.R.id.home:
                showAlertDialog(ALERT_DIALOG_CLOSE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    // method yang digunakan untuk memunculkan alert dialog ketika tombol back pada perangkat ditekan
    @Override
    public void onBackPressed() {
        showAlertDialog(ALERT_DIALOG_CLOSE);
    }

    private final int ALERT_DIALOG_CLOSE = 10;
    private final int ALERT_DIALOG_DELETE = 20;

    private void showAlertDialog(int type) {
        final boolean isDialogClose = type == ALERT_DIALOG_CLOSE;
        String dialogTitle, dialogMessage;
        if (isDialogClose) {
            dialogTitle = "Cancel";
            dialogMessage = "Do you want to cancel the changes to the form?";
        } else {
            dialogMessage = "Are you sure you want to delete this item?";
            dialogTitle = "Delete Todo";
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(dialogTitle);
        alertDialogBuilder
                .setMessage(dialogMessage)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (isDialogClose) {
                            finish();
                        } else {
                            long result = todoHelper.deleteTodo(todo.getId());
                            if (result > 0) {
                                Intent intent = new Intent();
                                intent.putExtra(EXTRA_POSITION, position);
                                setResult(RESULT_DELETE, intent);
                                finish();
                            } else {
                                Toast.makeText(TodoAddUpdateActivity.this, "Failed to Delete Data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    // method yang digunakan untuk mengambil tanggal dan waktu
    private String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
