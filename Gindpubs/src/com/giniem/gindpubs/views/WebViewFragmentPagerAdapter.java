package com.giniem.gindpubs.views;

import java.io.File;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.giniem.gindpubs.BookJson;

public class WebViewFragmentPagerAdapter extends FragmentStatePagerAdapter {

	private BookJson book;

	private String magazinePath;
	
	public WebViewFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	public WebViewFragmentPagerAdapter(FragmentManager fm, BookJson book,
			final String magazinePath) {
		super(fm);
		if (null == book) {
			this.book = new BookJson();
		} else {
			this.book = book;
		}
		if (null == magazinePath) {
			this.magazinePath = "";
		} else {
			this.magazinePath = magazinePath;
		}
	}

	@Override
	public Fragment getItem(int i) {
		Fragment fragment = new WebViewFragment();
		Bundle args = new Bundle();

		args.putString(WebViewFragment.ARG_OBJECT,
				this.magazinePath + book.getMagazineName() + File.separator
						+ book.getContents().get(i));
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int getCount() {
		return book.getContents().size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return "OBJECT " + (position + 1);
	}
}
