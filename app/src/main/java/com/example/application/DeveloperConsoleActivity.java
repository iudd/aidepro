package com.example.application;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DeveloperConsoleActivity extends AppCompatActivity {

    private SwitchMaterial logSwitch;
    private RecyclerView logRecyclerView;
    private MaterialButton clearLogsButton;
    private List<String> logList;
    private LogAdapter adapter;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_console);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefs = getSharedPreferences("browser_prefs", MODE_PRIVATE);
        logList = loadLogs();

        logSwitch = findViewById(R.id.log_switch);
        logRecyclerView = findViewById(R.id.log_recycler_view);
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
        logRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        logRecyclerView.setAdapter(adapter);

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

    private class LogAdapter extends RecyclerView.Adapter<LogViewHolder> {

        private List<String> logs;

        public LogAdapter(List<String> logs) {
            this.logs = logs;
        }

        @NonNull
        @Override
        public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_item, parent, false);
            return new LogViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LogViewHolder holder, final int position) {
            final String log = logs.get(position);
            holder.logTextView.setText(log);
            holder.copyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Log", log);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(DeveloperConsoleActivity.this, "已复制到剪贴板", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return logs.size();
        }
    }

    private static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView logTextView;
        MaterialButton copyButton;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            logTextView = itemView.findViewById(R.id.log_text_view);
            copyButton = itemView.findViewById(R.id.copy_button);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}