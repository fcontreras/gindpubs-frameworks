package com.giniem.gindpubs;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.giniem.gindpubs.views.CustomWebView;
import com.giniem.gindpubs.views.CustomWebViewPager;
import com.giniem.gindpubs.views.WebViewFragment;
import com.giniem.gindpubs.views.WebViewFragmentPagerAdapter;

import java.io.File;

public class MagazineActivity extends FragmentActivity {

	private GestureDetectorCompat gestureDetector;
	private WebViewFragmentPagerAdapter webViewPagerAdapter;
	private CustomWebViewPager pager;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        WebViewFragment fragment = (WebViewFragment) webViewPagerAdapter.instantiateItem(pager, pager.getCurrentItem());
        fragment.getWebView().destroy();
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// We would like to keep the screen on while reading the magazine
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Remove notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.html_pager);

		// Show the Up button in the action bar.
		// setupActionBar();

		Intent intent = getIntent();

		try {
			BookJson book = new BookJson();
			book.setMagazineName(intent
					.getStringExtra(GindActivity.MAGAZINE_NAME));
			book.fromJson(intent.getStringExtra(GindActivity.BOOK_JSON_KEY));
			this.setPagerView(book);

			gestureDetector = new GestureDetectorCompat(this,
					new MyGestureListener());
		} catch (Exception ex) {
			ex.printStackTrace();
			Toast.makeText(this, "Not valid book.json found!",
					Toast.LENGTH_LONG).show();
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
		getMenuInflater().inflate(R.menu.magazine, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void setPagerView(final BookJson book) {

		String path = "file:///" + Configuration.getDiskDir(this).getPath()
				+ File.separator;

		// ViewPager and its adapters use support library
		// fragments, so use getSupportFragmentManager.
		webViewPagerAdapter = new WebViewFragmentPagerAdapter(
				getSupportFragmentManager(), book, path, this);
		pager = (CustomWebViewPager) findViewById(R.id.pager);
		pager.setAdapter(webViewPagerAdapter);

		CustomWebView viewIndex = (CustomWebView) findViewById(R.id.webViewIndex);
		viewIndex.getSettings().setJavaScriptEnabled(true);
		viewIndex.getSettings().setUseWideViewPort(true);
		viewIndex.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (!url.startsWith("file://")) {
					Uri uri = Uri.parse(url);
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);
				} else {
					url = url.substring(url.lastIndexOf("/") + 1);
					int index = book.getContents().indexOf(url);

					Log.d(this.getClass().toString(), "Index to load: " + index
							+ ", page: " + url);

					pager.setCurrentItem(index);
					view.setVisibility(View.GONE);
				}
				return true;
			}
		});
        viewIndex.setBackgroundColor(Color.TRANSPARENT);
		viewIndex.loadUrl(path + book.getMagazineName() + File.separator
				+ "index.html");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			WebViewFragment fragment = (WebViewFragment) this.webViewPagerAdapter
					.instantiateItem(pager, pager.getCurrentItem());

			if (fragment.inCustomView()) {
				fragment.hideCustomView();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		// Intercept the touch events.
		this.gestureDetector.onTouchEvent(event);

		// We call the superclass implementation for the touch
		// events to continue along children.
		return super.dispatchTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		this.gestureDetector.onTouchEvent(event);

		// We call the superclass implementation.
		return super.onTouchEvent(event);
	}

	/**
	 * Used to handle the gestures, but we will only need the onDoubleTap. The
	 * other events will be passed to children views.
	 * 
	 * @author Holland
	 * 
	 */
	class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onDoubleTap(MotionEvent event) {
			CustomWebView viewIndex = (CustomWebView) findViewById(R.id.webViewIndex);
			if (viewIndex.isShown()) {
				viewIndex.setVisibility(View.GONE);
			} else {
				viewIndex.setVisibility(View.VISIBLE);
			}

			return true;
		}
	}
}
