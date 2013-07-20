package com.giniem.gindpubs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.giniem.gindpubs.client.GindClientTask;

public class GindActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			// Load configuration, then you are ready to get the properties.
			Configuration.load(this);

			// We get the shelf json asynchronously.
			GindClientTask asyncClient = new GindClientTask(this);

			asyncClient.execute(Configuration.getNEWSSTAND_MANIFEST_URL());

			// Log.i(this.getClass().getName(), "App ID: " +
			// this.getPackageName());
			// AccountManager manager = AccountManager.get(this);
			// Account[] accounts = manager.getAccounts();
			//
			// for (Account account : accounts) {
			// Log.i(this.getClass().getName(), account.name);
			// }

			Log.e(this.getClass().getName(),
					String.valueOf(asyncClient.getStatus()));

		} catch (Exception e) {
			e.printStackTrace();
			Log.e(this.getClass().getName(), "Cannot load configuration.");
		}

		loadingScreen();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gind, menu);
		return true;
	}

	public void loadingScreen() {
		setContentView(R.layout.loading);
		WebView webview = (WebView) findViewById(R.id.webView1);
		webview.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		webview.loadUrl(getString(R.string.loadingUrl));
	}

	public void parseShelf(final JSONArray jsonArray) {
		Log.i(this.getClass().getName(),
				"Shelf json contains " + jsonArray.length() + " elements.");

		JSONObject json = null;
		try {
			for (int i = 0; i < jsonArray.length(); i++) {
				json = new JSONObject(jsonArray.getString(i));

				Log.i(this.getClass().getName(), "Parsing JSON object " + json);
			}
		} catch (JSONException e) {
		}

		this.setContentView(R.layout.activity_gind);
	}
}