package com.giniem.gindpubs;

import java.io.File;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

public class Configuration {

	private Configuration() {
	}

	// Tries to use external storage, if not available then fallback to intenal.
	public static File getDiskCacheDir(Context context) {
		String cachePath = "";

		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {

			File extCacheDir = context.getExternalCacheDir();
			if (null != extCacheDir) {
				cachePath = extCacheDir.getPath();
			} else {
				cachePath = context.getCacheDir().getPath();
			}
		} else {
			cachePath = context.getCacheDir().getPath();
		}
		return new File(cachePath + File.separator
				+ context.getString(R.string.thumbnails));
	}

	public static File getDiskDir(Context context) {
		String appPath = "";

		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {

			File externalDir = context.getExternalFilesDir(null);
			if (null != externalDir) {
				appPath = externalDir.getPath();
			} else {
				appPath = context.getFilesDir().getPath();
			}
		} else {
			appPath = context.getFilesDir().getPath();
		}
		return new File(appPath + File.separator
				+ context.getString(R.string.magazines));
	}
	
	public static boolean hasInternetConnection(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}
}
