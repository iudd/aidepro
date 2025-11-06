package com.example.application;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HistoryActivity extends AppCompatActivity {

    private ListView historyListView;
    private Button clearHistoryButton;
    private List<String> historyList;
    private HistoryAdapter adapter;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefs = getSharedPreferences("browser_prefs", MODE_PRIVATE);
        historyList = loadHistory();

        historyListView = findViewById(R.id.history_list_view);
        clearHistoryButton = findViewById(R.id.clear_history_button);

        adapter = new HistoryAdapter(historyList);
        historyListView.setAdapter(adapter);

        if (historyList.isEmpty()) {
            Toast.makeText(this, R.string.no_history, Toast.LENGTH_SHORT).show();
        }

        clearHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearHistory();
            }
        });
    }

    private List<String> loadHistory() {
        Set<String> historySet = prefs.getStringSet("history", new HashSet<String>());
        return new ArrayList<>(historySet);
    }

    private void saveHistory(List<String> history) {
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> historySet = new HashSet<>(history);
        editor.putStringSet("history", historySet);
        editor.apply();
    }

    private void clearHistory() {
        historyList.clear();
        saveHistory(historyList);
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "历史记录已清除", Toast.LENGTH_SHORT).show();
    }

    private class HistoryAdapter extends ArrayAdapter<String> {

        public HistoryAdapter(List<String> history) {
            super(HistoryActivity.this, 0, history);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.history_item, parent, false);
            }

            final String url = getItem(position);

            TextView urlTextView = convertView.findViewById(R.id.url_text_view);
            Button deleteButton = convertView.findViewById(R.id.delete_button);

            urlTextView.setText(url);
            urlTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
                    intent.putExtra("url", url);
                    startActivity(intent);
                    finish();
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    historyList.remove(position);
                    saveHistory(historyList);
                    notifyDataSetChanged();
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