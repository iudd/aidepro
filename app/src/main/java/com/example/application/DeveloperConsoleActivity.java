package com.example.application;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DeveloperConsoleActivity extends AppCompatActivity {

    private Switch logSwitch;
    private ListView logListView;
    private Button clearLogsButton;
    private List<String> logList;
    private LogAdapter adapter;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_console);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefs = getSharedPreferences("browser_prefs", MODE_PRIVATE);
        logList = loadLogs();

        logSwitch = findViewById(R.id.log_switch);
        logListView = findViewById(R.id.log_list_view);
        clearLogsButton = findViewById(R.id.clear_logs_button);

        logSwitch.setChecked(prefs.getBoolean("logging_enabled", false));
        logSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.edit().putBoolean("logging_enabled", isChecked).apply();
                Toast.makeText(DeveloperConsoleActivity.this, isChecked ? "日志记录已开启" : "日志记录已关闭", Toast.LENGTH_SHORT).show();
            }
        });

        adapter = new LogAdapter(logList);
        logListView.setAdapter(adapter);

        if (logList.isEmpty()) {
            Toast.makeText(this, R.string.no_logs, Toast.LENGTH_SHORT).show();
        }

        clearLogsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearLogs();
            }
        });
    }

    private List<String> loadLogs() {
        Set<String> logSet = prefs.getStringSet("logs", new HashSet<String>());
        return new ArrayList<>(logSet);
    }

    private void saveLogs(List<String> logs) {
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> logSet = new HashSet<>(logs);
        editor.putStringSet("logs", logSet);
        editor.apply();
    }

    private void clearLogs() {
        logList.clear();
        saveLogs(logList);
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "日志已清除", Toast.LENGTH_SHORT).show();
    }

    private class LogAdapter extends ArrayAdapter<String> {

        public LogAdapter(List<String> logs) {
            super(DeveloperConsoleActivity.this, 0, logs);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.log_item, parent, false);
            }

            final String log = getItem(position);

            TextView logTextView = convertView.findViewById(R.id.log_text_view);
            Button copyButton = convertView.findViewById(R.id.copy_button);

            logTextView.setText(log);
            copyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Log", log);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(DeveloperConsoleActivity.this, "已复制到剪贴板", Toast.LENGTH_SHORT).show();
                }
            });

            return convertView;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}