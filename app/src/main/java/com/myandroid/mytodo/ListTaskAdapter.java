package com.myandroid.mytodo;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class ListTaskAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private DBHelper database;

    public ListTaskAdapter(Activity activity, ArrayList<HashMap<String, String>> data, DBHelper database) {
        this.activity = activity;
        this.data = data;
        this.database = database;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListTaskViewHolder holder = null;
        if(convertView == null) {
            holder = new ListTaskViewHolder();
            convertView = LayoutInflater.from(activity).inflate(R.layout.list_item, parent, false);
            holder.taskTextView = convertView.findViewById(R.id.taskTextView);
            holder.taskCheckBox = convertView.findViewById(R.id.taskCheckBox);
            convertView.setTag(holder);
        } else {
            holder = (ListTaskViewHolder)convertView.getTag();
        }

        final HashMap<String, String> singleTask = data.get(position);
        final ListTaskViewHolder tmpHolder = holder;

        holder.taskTextView.setId(position);
        holder.taskCheckBox.setId(position);

        try {
            holder.taskCheckBox.setOnCheckedChangeListener(null);

            if(singleTask.get("status").contentEquals("1")) {
                holder.taskTextView.setText(Html.fromHtml("<strike>" + singleTask.get("task") + "</strike>"));
                holder.taskCheckBox.setChecked(true);
            } else {
                holder.taskTextView.setText(singleTask.get("task"));
                holder.taskCheckBox.setChecked(false);
            }

            holder.taskCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        database.updateTaskStatus(singleTask.get("id"), 1);
                        tmpHolder.taskTextView.setText(Html.fromHtml("<strike>" + singleTask.get("task") + "</strike>"));
                    } else {
                        database.updateTaskStatus(singleTask.get("id"), 0);
                        tmpHolder.taskTextView.setText(singleTask.get("task"));
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }
}

class ListTaskViewHolder {
    TextView taskTextView;
    CheckBox taskCheckBox;
}