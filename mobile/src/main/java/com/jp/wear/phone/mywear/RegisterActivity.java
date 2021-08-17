package com.jp.wear.phone.mywear;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class RegisterActivity extends AppCompatActivity {

    Button btn_ir_login;
    WebView webView;
    private String webUrl="https://www.google.com.mx/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btn_ir_login=findViewById(R.id.btn_ir_login);
        btn_ir_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(RegisterActivity.this,LoginActivity.class));
                finish();
            }
        });

        webView = findViewById(R.id.webview);
        webView.loadUrl(webUrl);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request){
                view.loadUrl(String.valueOf(request));
                return true;
            }
        });
        if(savedInstanceState!=null){
            webView.restoreState(savedInstanceState);
        }else{
            webView.getSettings().setJavaScriptEnabled(true);
        }

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress){
                super.onProgressChanged(view, newProgress);
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }
}