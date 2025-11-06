package com.example.application;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    
    private WebView webView;
    private EditText urlEditText;
    private Button backButton, forwardButton, refreshButton, homeButton;
    
    private List<String> historyList = new ArrayList<>();
    private int currentIndex = -1;
    private static final String HOME_URL = "https://www.baidu.com";
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
        
        prefs = getSharedPreferences("browser_prefs", MODE_PRIVATE);
        loadHistory();
        
        initViews();
        initWebView();
        
        String url = getIntent().getStringExtra("url");
        if (url != null) {
            loadUrl(url);
        } else {
            loadUrl(HOME_URL);
        }
    }
    
    private void initViews() {
        urlEditText = findViewById(R.id.url_edit_text);
        webView = findViewById(R.id.web_view);
        backButton = findViewById(R.id.back_button);
        forwardButton = findViewById(R.id.forward_button);
        refreshButton = findViewById(R.id.refresh_button);
        homeButton = findViewById(R.id.home_button);
        
        urlEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    String url = urlEditText.getText().toString().trim();
                    if (!url.isEmpty()) {
                        loadUrl(url);
                    }
                    return true;
                }
                return false;
            }
        });
        
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        
        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goForward();
            }
        });
        
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.reload();
            }
        });
        
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUrl(HOME_URL);
            }
        });
    }
    
    private void initWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                urlEditText.setText(url);
                addToHistory(url);
                updateButtons();
            }
        });
        
        webView.setWebChromeClient(new WebChromeClient());
    }
    
    private void loadUrl(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }
        webView.loadUrl(url);
    }
    
    private void addToHistory(String url) {
        if (currentIndex < historyList.size() - 1) {
            // Remove forward history if going to new page
            historyList = historyList.subList(0, currentIndex + 1);
        }
        if (!historyList.contains(url)) {
            historyList.add(url);
        }
        currentIndex = historyList.size() - 1;
        saveHistory();
    }
    
    private void loadHistory() {
        Set<String> historySet = prefs.getStringSet("history", new HashSet<String>());
        historyList.addAll(historySet);
        currentIndex = historyList.size() - 1;
    }
    
    private void saveHistory() {
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> historySet = new HashSet<>(historyList);
        editor.putStringSet("history", historySet);
        editor.apply();
    }
    
    private void goBack() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else if (currentIndex > 0) {
            currentIndex--;
            loadUrl(historyList.get(currentIndex));
        } else {
            Toast.makeText(this, "已经是第一页", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void goForward() {
        if (webView.canGoForward()) {
            webView.goForward();
        } else if (currentIndex < historyList.size() - 1) {
            currentIndex++;
            loadUrl(historyList.get(currentIndex));
        } else {
            Toast.makeText(this, "已经是最后一页", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void updateButtons() {
        backButton.setEnabled(webView.canGoBack() || currentIndex > 0);
        forwardButton.setEnabled(webView.canGoForward() || currentIndex < historyList.size() - 1);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_history) {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
    
    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }
}