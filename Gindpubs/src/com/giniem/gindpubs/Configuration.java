package com.giniem.gindpubs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

public class Configuration {

	private static String NEWSSTAND_MANIFEST_URL;
	private static String PURCHASE_CONFIRMATION_URL;
	private static String PURCHASES_URL;
	private static String POST_APNS_TOKEN_URL;
	private static String THUMBNAILS_SUBDIR;
	private static String JSON_FILENAME;
	private static String MAGAZINES_SUBDIR;

	public static String getNEWSSTAND_MANIFEST_URL() {
		return NEWSSTAND_MANIFEST_URL;
	}

	public static String getPURCHASE_CONFIRMATION_URL() {
		return PURCHASE_CONFIRMATION_URL;
	}

	public static String getPURCHASES_URL() {
		return PURCHASES_URL;
	}

	public static String getPOST_APNS_TOKEN_URL() {
		return POST_APNS_TOKEN_URL;
	}

	public static String getTHUMBNAILS_SUBDIR() {
		return THUMBNAILS_SUBDIR;
	}
	
	public static String getJSON_FILENAME() {
		return JSON_FILENAME;
	}

	public static String getMAGAZINES_SUBDIR() {
		return MAGAZINES_SUBDIR;
	}
	
	private Configuration() {
	}

	public static void load(Context context) throws IOException {
		AssetManager assetManager = context.getAssets();

		InputStream inputStream = assetManager.open("configuration.properties");

		Properties properties = new Properties();
		properties.load(inputStream);

		NEWSSTAND_MANIFEST_URL = properties
				.getProperty("NEWSSTAND_MANIFEST_URL");
		PURCHASE_CONFIRMATION_URL = properties
				.getProperty("PURCHASE_CONFIRMATION_URL");
		PURCHASES_URL = properties.getProperty("PURCHASES_URL");
		POST_APNS_TOKEN_URL = properties.getProperty("POST_APNS_TOKEN_URL");
		THUMBNAILS_SUBDIR = properties.getProperty("THUMBNAILS_SUBDIR");
		JSON_FILENAME = properties.getProperty("JSON_FILENAME");
		MAGAZINES_SUBDIR = properties.getProperty("MAGAZINES_SUBDIR");
	}

	// Tries to use external storage, if not available then fallback to intenal.
	public static File getDiskCacheDir(Context context) {
		String cachePath = "";

		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable()) {

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
				+ Configuration.getTHUMBNAILS_SUBDIR());
	}

	public static File getDiskDir(Context context) {
		String appPath = "";

		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable()) {

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
				+ Configuration.getMAGAZINES_SUBDIR());
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
