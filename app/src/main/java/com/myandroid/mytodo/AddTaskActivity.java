package com.myandroid.mytodo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class AddTaskActivity extends AppCompatActivity {

    EditText taskEditText;
    TextView dateTextView, deleteText;
    Button okBtn;

    Boolean isModify = false;
    String task_id;

    Calendar calendar;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        dbHelper = new DBHelper(getApplicationContext());
        calendar = new GregorianCalendar();
        taskEditText = (EditText)findViewById(R.id.taskEditText);
        dateTextView = (TextView)findViewById(R.id.dateTextView);
        okBtn = (Button)findViewById(R.id.okBtn);

        deleteText = (TextView)findViewById(R.id.deleteTask);

        dateTextView.setText(new SimpleDateFormat("E, yyyy MM dd").format(calendar.getTime()));

        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseDate();
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTask();
                startActivity(new Intent(AddTaskActivity.this, MainActivity.class));
            }
        });

        Intent intent = getIntent();
        if (intent.hasExtra("isModify")) {
            isModify = intent.getBooleanExtra("isModify", false);
            task_id = intent.getStringExtra("id");
            init_modify();
        }

        deleteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.deleteTask(task_id);
                Toast.makeText(getApplicationContext(), "Task Removed", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }

    public void init_modify() {
        okBtn.setText("Update");
        deleteText.setVisibility(View.VISIBLE);
        Cursor task = dbHelper.getSingleTask(task_id);
        if (task != null) {
            task.moveToFirst();
            taskEditText.setText(task.getString(1));
            SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd");

            try {
                calendar.setTime(iso8601Format.parse(task.getString(2)));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            dateTextView.setText(new SimpleDateFormat("E, yyyy MM dd").format(calendar.getTime()));
        }
    }

    private void saveTask() {
        if(taskEditText.getText().toString().trim().length() > 0) {
            if(isModify) {
                dbHelper.updateTask(task_id, taskEditText.getText().toString(), new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
                Toast.makeText(getApplicationContext(), "Task Updated.", Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.insertTask(taskEditText.getText().toString(), new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
                Toast.makeText(getApplicationContext(), "Task Added.", Toast.LENGTH_SHORT).show();
            }
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "할 일을 적어주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private void chooseDate() {
        final View dialogView = View.inflate(this, R.layout.date_picker, null);
        final DatePicker datePicker = dialogView.findViewById(R.id.date_picker);
        datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setTitle("Choose Date");
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                calendar = new GregorianCalendar(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                dateTextView.setText(new SimpleDateFormat("E, yyyy MM dd").format(calendar.getTime()));
            }
        });

        builder.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}