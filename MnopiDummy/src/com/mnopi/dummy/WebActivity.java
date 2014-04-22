package com.mnopi.dummy;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class WebActivity extends Activity {

	private EditText addressBar;
	private Button btnOpenUrl; 
	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web);
		// Show the Up button in the action bar.
		setupActionBar();

		addressBar = (EditText) findViewById(R.id.text_address_bar);
		btnOpenUrl = (Button) findViewById(R.id.btn_open_url);
		webView = (WebView) findViewById(R.id.web_view);
		
		// Enable Javascript
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBuiltInZoomControls(true);
		
		webView.addJavascriptInterface(new JsHtmlInterface(this), "HtmlSender");
		
		// Handle URLs
		webView.setWebViewClient(new WebViewClient() {
			
			public void onPageFinished(WebView view, String url) {
				String molona = "javascript:HtmlSender.sendHtml" +
						"('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>', " + "'" + url + "');";
				webView.loadUrl("javascript:HtmlSender.sendHtml" +
						"('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>', " + "'" + url + "');");
			}
			
		});
		
		btnOpenUrl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String url = addressBar.getText().toString();
				if (!url.startsWith("http://", 0)){
					url = "http://" + url;
				}
				webView.loadUrl(url);
			}
		});
	}
	
	class JsHtmlInterface {
		
		private Context ctx;
		
		public JsHtmlInterface(Context ctx) {
			this.ctx = ctx;
		}

		@JavascriptInterface
		public void sendHtml(String html, String url) {
			
			Calendar c = Calendar.getInstance();
    		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		
    		String formattedDate = df.format(c.getTime());
    		
    		Intent pageIntent = new Intent("com.mnopi.services.DataCollectorService");
    		pageIntent.putExtra("url", url);
    		pageIntent.putExtra("html_code", html);
    		pageIntent.putExtra("date", formattedDate);
    		pageIntent.putExtra("handler_key", "page_visited");
    		
    		startService(pageIntent);
    		
			Toast toast = Toast.makeText(ctx, url, Toast.LENGTH_LONG);
			toast.show();
		}
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.web, menu);
		return true;
	}
}
