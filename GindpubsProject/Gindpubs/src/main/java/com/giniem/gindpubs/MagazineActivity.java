package com.giniem.gindpubs;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewPager;
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

import com.giniem.gindpubs.model.BookJson;
import com.giniem.gindpubs.views.CustomWebView;
import com.giniem.gindpubs.views.CustomWebViewPager;
import com.giniem.gindpubs.views.WebViewFragment;
import com.giniem.gindpubs.views.WebViewFragmentPagerAdapter;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class MagazineActivity extends FragmentActivity {

    private boolean doubleTap = false;

	private GestureDetectorCompat gestureDetector;
	private WebViewFragmentPagerAdapter webViewPagerAdapter;
	private CustomWebViewPager pager;
    private BookJson jsonBook;

    private final String LANDSCAPE = "LANDSCAPE";
    private final String PORTRAIT = "PORTRAIT";

    private String orientation;

    public BookJson getJsonBook() {
        return this.jsonBook;
    }

    public CustomWebViewPager getPager() {
        return this.pager;
    }

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
			jsonBook = new BookJson();
            jsonBook.setMagazineName(intent
					.getStringExtra(GindActivity.MAGAZINE_NAME));
            jsonBook.fromJson(intent.getStringExtra(GindActivity.BOOK_JSON_KEY));

            this.setOrientation(jsonBook.getOrientation());
            this.setPagerView(jsonBook);

			gestureDetector = new GestureDetectorCompat(this,
					new MyGestureListener());
		} catch (Exception ex) {
			ex.printStackTrace();
			Toast.makeText(this, "Not valid book.json found!",
					Toast.LENGTH_LONG).show();
		}
	}

    private void setOrientation(String _orientation) {

        _orientation = _orientation.toUpperCase();
        this.orientation = _orientation;

        if (PORTRAIT.equals(_orientation)) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (LANDSCAPE.equals(_orientation)) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }

    public void resetOrientation() {
        this.setOrientation(this.orientation);
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

		String path = "file://" + Configuration.getMagazinesDirectory(this) + File.separator;

		// ViewPager and its adapters use support library
		// fragments, so use getSupportFragmentManager.
		webViewPagerAdapter = new WebViewFragmentPagerAdapter(
				getSupportFragmentManager(), book, path, this);
		pager = (CustomWebViewPager) findViewById(R.id.pager);
		pager.setAdapter(webViewPagerAdapter);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Log.d(this.getClass().getName(), "Loading page at: " + position);
            }
        });

		CustomWebView viewIndex = (CustomWebView) findViewById(R.id.webViewIndex);
		viewIndex.getSettings().setJavaScriptEnabled(true);
		viewIndex.getSettings().setUseWideViewPort(true);
		viewIndex.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String stringUrl) {

                // mailto links will be handled by the OS.
                if (stringUrl.startsWith("mailto:")) {
                    Uri uri = Uri.parse(stringUrl);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                } else {
                    try {
                        URL url = new URL(stringUrl);

                        // We try to remove the referrer string to avoid passing it to the server in case the URL is an external link.
                        String referrer = "";
                        if (url.getQuery() != null) {
                            Map<String, String> variables = Configuration.splitUrlQueryString(url);
                            String finalQueryString = "";
                            for (Map.Entry<String, String> entry : variables.entrySet()) {
                                if (entry.getKey().equals("referrer")) {
                                    referrer = entry.getValue();
                                } else {
                                    finalQueryString += entry.getKey() + "=" + entry.getValue() + "&";
                                }
                            }
                            if (!finalQueryString.isEmpty()) {
                                finalQueryString = "?" + finalQueryString.substring(0, finalQueryString.length() - 1);
                            }
                            stringUrl = stringUrl.replace("?" + url.getQuery(), finalQueryString);
                        }
                        // Aaaaand that was the process of removing the referrer from the query string.

                        if (!url.getProtocol().equals("file")) {
                            Uri uri = Uri.parse(stringUrl);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        } else {
                            Log.d(">>>URL_DATA", "FINAL INTERNAL URL = " + stringUrl);
                            stringUrl = url.getPath().substring(url.getPath().lastIndexOf("/") + 1);

                            int index = book.getContents().indexOf(stringUrl);

                            if (index != -1) {
                                Log.d(this.getClass().toString(), "Index to load: " + index
                                        + ", page: " + stringUrl);

                                pager.setCurrentItem(index);
                                view.setVisibility(View.GONE);
                            } else {

                                // If the file DOES NOT exist, we won't load it.
                                File htmlFile = new File(url.getPath());
                                if (htmlFile.exists()) {
                                    return false;
                                }
                            }
                        }
                    } catch (MalformedURLException ex) {
                        Log.d(">>>URL_DATA", ex.getMessage());
                    } catch (UnsupportedEncodingException ex) {
                    }
                }

				return true;
			}
		});
		viewIndex.loadUrl(path + book.getMagazineName() + File.separator
				+ "index.html");
        viewIndex.setBackgroundColor(0x00000000);
        viewIndex.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
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

        if (doubleTap) {
            //No need to pass double tap to children
            doubleTap = false;
        } else {
            // We call the superclass implementation for the touch
            // events to continue along children.
            return super.dispatchTouchEvent(event);
        }
        return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		this.gestureDetector.onTouchEvent(event);

        if (doubleTap) {
            //No need to pass double tap to children
            doubleTap = false;
        } else {
            // We call the superclass implementation.
            return super.onTouchEvent(event);
        }
        return true;
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
            doubleTap = true;
			CustomWebView viewIndex = (CustomWebView) findViewById(R.id.webViewIndex);

            //Disable Index Zoom
            viewIndex.getSettings().setSupportZoom(false);

			if (viewIndex.isShown()) {
				viewIndex.setVisibility(View.GONE);
			} else {

                WebViewFragment fragment = (WebViewFragment) MagazineActivity.this.webViewPagerAdapter
                        .instantiateItem(pager, pager.getCurrentItem());
                if (!fragment.inCustomView()) {
                    viewIndex.setVisibility(View.VISIBLE);
                }
			}

			return true;
		}
	}
}
