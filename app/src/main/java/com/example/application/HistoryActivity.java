package com.example.application;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView historyRecyclerView;
    private MaterialButton clearHistoryButton;
    private List<String> historyList;
    private HistoryAdapter adapter;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefs = getSharedPreferences("browser_prefs", MODE_PRIVATE);
        historyList = loadHistory();

        historyRecyclerView = findViewById(R.id.history_recycler_view);
        clearHistoryButton = findViewById(R.id.clear_history_button);

        adapter = new HistoryAdapter(historyList);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyRecyclerView.setAdapter(adapter);

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

    private class HistoryAdapter extends RecyclerView.Adapter<HistoryViewHolder> {

        private List<String> history;

        public HistoryAdapter(List<String> history) {
            this.history = history;
        }

        @NonNull
        @Override
        public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
            return new HistoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull HistoryViewHolder holder, final int position) {
            final String url = history.get(position);
            holder.urlTextView.setText(url);
            holder.urlTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
                    intent.putExtra("url", url);
                    startActivity(intent);
                    finish();
                }
            });
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    history.remove(position);
                    saveHistory(history);
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return history.size();
        }
    }

    private static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView urlTextView;
        MaterialButton deleteButton;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            urlTextView = itemView.findViewById(R.id.url_text_view);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}