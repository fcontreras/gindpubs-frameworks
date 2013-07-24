package com.giniem.gindpubs;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.giniem.gindpubs.client.GindClientTask;
import com.giniem.gindpubs.views.MagazineThumb;

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

			this.setContentView(R.layout.activity_gind);
			
			TableLayout tableLayout = (TableLayout) findViewById(R.id.mainTable);
			
			TableRow tableRow = null;
			
			int length = jsonArray.length();
			for (int i = 0; i < length; i++) {
				json = new JSONObject(jsonArray.getString(i));
				Log.i(this.getClass().getName(), "Parsing JSON object " + json);
				
				MagazineThumb thumb = new MagazineThumb(this);
				thumb.setName(json.getString("name"));
				thumb.setTitle(json.getString("title"));
				thumb.setInfo(json.getString("info"));
				
				SimpleDateFormat sdfInput = new SimpleDateFormat(getString(R.string.inputDateFormat));
				SimpleDateFormat sdfOutput = new SimpleDateFormat(getString(R.string.outputDateFormat));
				Date date = sdfInput.parse(json.getString("date"));
				
				String dateString = sdfOutput.format(date);
				thumb.setDate(dateString);
				thumb.setSize(json.getInt("size"));
				thumb.setCover(json.getString("cover"));
				thumb.setUrl(json.getString("url"));
				//thumb.setMeasureWithLargestChildEnabled(true);
				thumb.setPaddingRelative(5, 10, 5, 20);
				thumb.init(this, null);
				
				thumb.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.0f));

				if (i % 2 == 0) {
					if (null != tableRow) {
						tableLayout.addView(tableRow);
					}
					tableRow = new TableRow(this);
					tableRow.setGravity(Gravity.CENTER_HORIZONTAL);
					tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
				}
				
				tableRow.addView(thumb);
			}
			// Add the last row
			if (null != tableRow) {
				if (length % 2 != 0) {
					View view = new View(this);
					view.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
					tableRow.addView(view);
				}
				tableLayout.addView(tableRow);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}