package com.magicare.smartnurse.activity;

import com.magicare.smartnurse.R;
import com.magicare.smartnurse.logic.UpgradeApp;
import com.magicare.smartnurse.utils.ConfigManager;
import com.magicare.smartnurse.utils.PromptManager;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
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
import android.widget.TextView;

/**
 * 关于我们
 * 
 * @author rice
 * 
 */
public class AboutFragment extends Fragment implements View.OnClickListener {
	View mView;
	Button btn_update;
	Button btn_feedback;
	Button introduction;
	Button help;
	LinearLayout hidden_linear, info_linear;
	WebView webView;
	TextView version;
	private ProgressBar pb;
	private static String URL1 = "http://www.maimai100.cn/";
	private static String URL2 = "http://www.maimai100.cn/index.php/Index/QA";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mView = View.inflate(getActivity(), R.layout.fragment_about, null);
		hidden_linear = (LinearLayout) mView.findViewById(R.id.hidden_linear);
		hidden_linear.setVisibility(View.GONE);
		info_linear =(LinearLayout) mView.findViewById(R.id.info_linear);
		info_linear.setVisibility(View.VISIBLE);
		webView = (WebView) mView.findViewById(R.id.magicare_view);
		version = (TextView) mView.findViewById(R.id.version);
		PackageManager pm = this.getActivity().getPackageManager();  
        PackageInfo pi;
		try {
			pi = pm.getPackageInfo(this.getActivity().getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			pi = null;
			e.printStackTrace();
		}  
        String versionName = pi.versionName; 
        version.setText("版本号："+versionName);
		
		WebSettings ws = webView.getSettings();
		ws.setJavaScriptEnabled(true); // 设置支持javascript脚本
        ws.setAllowFileAccess(true); // 允许访问文件
        ws.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
        ws.setDefaultTextEncodingName("utf-8"); //设置文本编码
        ws.setAppCacheEnabled(true);
        ws.setCacheMode(WebSettings.LOAD_DEFAULT);//设置缓存模式         
        
        pb = (ProgressBar) mView.findViewById(R.id.pb);  
        pb.setMax(100); 
        
        webView.setWebViewClient(new WebViewClientDemo());
        webView.setWebChromeClient(new WebViewChromeClientDemo());
		
		return mView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		btn_update = (Button) mView.findViewById(R.id.btn_update);
		btn_feedback = (Button) mView.findViewById(R.id.btn_feedback);
		introduction = (Button) mView.findViewById(R.id.introduction);
		help = (Button) mView.findViewById(R.id.help);
		btn_update.setOnClickListener(this);
		btn_feedback.setOnClickListener(this);
		introduction.setOnClickListener(this);
		help.setOnClickListener(this);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		info_linear.setVisibility(View.VISIBLE);
		hidden_linear.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_update:
			if (ConfigManager.getBooleanValue(getActivity(), ConfigManager.NETWORK_STARTUS, false)) {
				// 如何有网就检测 版本更新
				UpgradeApp upgrade = new UpgradeApp(getActivity(), "当前已是最新版本");
				upgrade.checkVersionCode();
			} else {

				PromptManager.showToast(getActivity(), false, "亲，请检查网络！");
			}
			break;
			
		case R.id.btn_feedback:
			Intent intent = new Intent(getActivity(), AdviceActivity.class);
			startActivity(intent);
			break;

		case R.id.introduction:
			info_linear.setVisibility(View.GONE);
			hidden_linear.setVisibility(View.VISIBLE);
			webView.loadUrl(URL1);
			webView.reload();
			break;
			
		case R.id.help:
			info_linear.setVisibility(View.GONE);
			hidden_linear.setVisibility(View.VISIBLE);
			webView.loadUrl(URL2);
			webView.reload();
			break;
			
		default:
			break;
		}
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
