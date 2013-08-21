package com.giniem.gindpubs;

import java.io.File;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.giniem.gindpubs.views.CustomWebViewPager;
import com.giniem.gindpubs.views.WebViewFragmentPagerAdapter;

public class MagazineActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Remove notification bar
		this.getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.html_pager);
		
		// Show the Up button in the action bar.
		//setupActionBar();
		
		Intent intent = getIntent();
	    	    
	    try {
	    	BookJson book = new BookJson();
	    	book.setMagazineName(intent.getStringExtra(GindActivity.MAGAZINE_NAME));
		    book.fromJson(intent.getStringExtra(GindActivity.BOOK_JSON_KEY));
			this.setPagerView(book);
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

	private void setPagerView(final BookJson book) {
		//setContentView(R.layout.html_pager);

		String path = "file:///" + Configuration.getDiskDir(this).getPath()
				+ File.separator;

		// ViewPager and its adapters use support library
		// fragments, so use getSupportFragmentManager.
		WebViewFragmentPagerAdapter mDemoCollectionPagerAdapter = new WebViewFragmentPagerAdapter(
				getSupportFragmentManager(), book, path);
		CustomWebViewPager mViewPager = (CustomWebViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mDemoCollectionPagerAdapter);
	}
}
