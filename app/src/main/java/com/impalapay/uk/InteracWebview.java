package com.impalapay.uk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class InteracWebview extends FragmentActivity {
    private WebView webView;
    private Button back_bt;
    private TextView done, back_tv;
    private String url = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_interac_webview);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            url = extras.getString("url");
        }

        webView = (WebView) findViewById(R.id.interac_webview);
        back_bt = (Button) findViewById(R.id.back_bt);
        done = (TextView) findViewById(R.id.done_tv);
        back_tv = (TextView) findViewById(R.id.back_tv);

        LinearLayout back_layout = (LinearLayout) findViewById(R.id.back_layout);
        back_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        back_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        back_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(InteracWebview.this, MainActivity.class);
                startActivity(i);

                finish();
            }
        });

        webView.loadUrl(url);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                ProgressBar progress;
                progress = (ProgressBar) findViewById(R.id.progress_web);
                progress.setProgress(newProgress);
                if (newProgress == 100){
                    progress.setVisibility(View.GONE);
                }else {
                    progress.setVisibility(View.VISIBLE);
                }
            }
        });
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        final ProgressDialog progressBar;
        progressBar = ProgressDialog.show(this,"Please wait...", "Loading Interac");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (progressBar.isShowing()){
                    progressBar.dismiss();
                }
            }
        });
    }

    private class MyWebViewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }
    }
}
