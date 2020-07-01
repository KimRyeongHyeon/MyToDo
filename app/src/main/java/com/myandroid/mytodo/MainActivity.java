package com.myandroid.mytodo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    DBHelper dbHelper;
    LinearLayout emptyLayout, todayLinearLayout, tomorrowLinearLayout, upComingLinearLayout;
    LinearLayout listLinearLayout;
    ListView todayTaskListView, tomorrowTaskListView, upComingTaskListView;
    FloatingActionButton addBtn;
    ArrayList<HashMap<String, String>> todayList = new ArrayList<>();
    ArrayList<HashMap<String, String>> tomorrowList = new ArrayList<>();
    ArrayList<HashMap<String, String>> upcomingList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emptyLayout = (LinearLayout)findViewById(R.id.emptyLayout);
        todayLinearLayout = (LinearLayout)findViewById(R.id.todayLinearLayout);
        tomorrowLinearLayout = (LinearLayout)findViewById(R.id.tomorrowLinearLayout);
        upComingLinearLayout = (LinearLayout)findViewById(R.id.upComingLinearLayout);

        listLinearLayout = (LinearLayout)findViewById(R.id.listLinearLayout);

        todayTaskListView = (ListView) findViewById(R.id.todayTaskListView);
        tomorrowTaskListView = (ListView) findViewById(R.id.tomorrowTaskListView);
        upComingTaskListView = (ListView) findViewById(R.id.upComingTaskListView);

        addBtn = (FloatingActionButton)findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddTaskActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateData();
    }

    public void populateData() {
        dbHelper = new DBHelper(this);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fetchDataFromDB();
            }
        });
    }

    public void fetchDataFromDB() {
        todayList.clear();
        tomorrowList.clear();
        upcomingList.clear();

        Cursor today = dbHelper.getTodayTask();
        Cursor tomorrow= dbHelper.getTomorrowTask();
        Cursor upcoming = dbHelper.getUpcomingTask();

        loadDataList(today, todayList);
        loadDataList(tomorrow, tomorrowList);
        loadDataList(upcoming, upcomingList);

        if(todayList.isEmpty() && tomorrowList.isEmpty() && upcomingList.isEmpty()) {
            emptyLayout.setVisibility(View.VISIBLE);
            listLinearLayout.setVisibility(View.GONE);
        } else {
            emptyLayout.setVisibility(View.GONE);
            listLinearLayout.setVisibility(View.VISIBLE);

            if(todayList.isEmpty()) {
                todayLinearLayout.setVisibility(View.GONE);
            } else {
                todayLinearLayout.setVisibility(View.VISIBLE);
                loadListView(todayTaskListView, todayList);
            }

            if(tomorrowList.isEmpty()) {
                tomorrowLinearLayout.setVisibility(View.GONE);
            } else {
                tomorrowLinearLayout.setVisibility(View.VISIBLE);
                loadListView(tomorrowTaskListView, tomorrowList);
            }

            if(upcomingList.isEmpty()) {
                upComingLinearLayout.setVisibility(View.GONE);
            } else {
                upComingLinearLayout.setVisibility(View.VISIBLE);
                loadListView(upComingTaskListView, upcomingList);
            }
        }
    }

    public void loadDataList(Cursor cursor, ArrayList<HashMap<String, String>> dataList) {
        if(cursor != null) {
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false) {
                HashMap<String, String> mapToday = new HashMap<>();
                mapToday.put("id", cursor.getString(0));
                mapToday.put("task", cursor.getString(1));
                mapToday.put("date", cursor.getString(2));
                mapToday.put("status", cursor.getString(3));
                dataList.add(mapToday);
                cursor.moveToNext();
            }
        }
    }

    public void loadListView(ListView listView, final ArrayList<HashMap<String, String>> dataList) {
        ListTaskAdapter adapter = new ListTaskAdapter(this, dataList, dbHelper);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
                intent.putExtra("isModify", true);
                intent.putExtra("id", dataList.get(position).get("id"));
                startActivity(intent);
                finish();
            }
        });
    }
}