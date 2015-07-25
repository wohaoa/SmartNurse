package com.magicare.smartnurse.activity;

import com.magicare.smartnurse.R;
import com.magicare.smartnurse.logic.UpgradeApp;
import com.magicare.smartnurse.utils.ConfigManager;
import com.magicare.smartnurse.utils.PromptManager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

/**
 * 关于我们
 * 
 * @author rice 12433455
 * 
 */
public class AboutActivity extends BaseActivity implements OnClickListener {
	private WebView webView;
	private ProgressBar pb;
	private static String URL = "http://www.maimai100.cn/";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_aboutus);
		webView = (WebView) findViewById(R.id.magicare_view);
		
		WebSettings ws = webView.getSettings();
		ws.setJavaScriptEnabled(true); // 设置支持javascript脚本
        ws.setAllowFileAccess(true); // 允许访问文件
        ws.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
        ws.setDefaultTextEncodingName("utf-8"); //设置文本编码
        ws.setAppCacheEnabled(true);
        ws.setCacheMode(WebSettings.LOAD_DEFAULT);//设置缓存模式         
        
        pb = (ProgressBar) findViewById(R.id.pb);  
        pb.setMax(100); 
        
        webView.setWebViewClient(new WebViewClientDemo());
        webView.setWebChromeClient(new WebViewChromeClientDemo());
        webView.loadUrl(URL);
	}
	
    private class WebViewClientDemo extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);// 当打开新链接时，使用当前的 WebView，不会使用系统其他浏览器
            return true;
        }
    }
    
    private class WebViewChromeClientDemo extends WebChromeClient {
        // 设置网页加载的进度条
        public void onProgressChanged(WebView view, int newProgress) {
            pb.setProgress(newProgress);  
            if(newProgress==100){  
                pb.setVisibility(View.GONE);  
            }  
            super.onProgressChanged(view, newProgress); 
        }
 
        // 获取网页的标题
        public void onReceivedTitle(WebView view, String title) {
        }
 
        // JavaScript弹出框
        @Override
        public boolean onJsAlert(WebView view, String url, String message,
                JsResult result) {
            return super.onJsAlert(view, url, message, result);
        }
 
        // JavaScript输入框
        @Override
        public boolean onJsPrompt(WebView view, String url, String message,
                String defaultValue, JsPromptResult result) {
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }
 
        // JavaScript确认框
        @Override
        public boolean onJsConfirm(WebView view, String url, String message,
                JsResult result) {
            return super.onJsConfirm(view, url, message, result);
        }
    }
}
