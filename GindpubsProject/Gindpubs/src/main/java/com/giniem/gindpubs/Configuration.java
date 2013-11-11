package com.giniem.gindpubs;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import java.io.File;

public class Configuration {

    /**
     * Sets the name of the cache folder to be used by the application.
     */
    public static final String CACHE_FILES_DIR = "shelf.cached";

    /**
     * Sets the name of the cache folder to be used by the application.
     */
    public static final String MAGAZINES_FILES_DIR = "magazines";

    /**
     * Empty constructor not to be used since the class is utils only.
     */
	private Configuration() {}

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
				+ Configuration.CACHE_FILES_DIR);
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
				+ Configuration.MAGAZINES_FILES_DIR);
	}

    /**
     * Gets the absolute cache dir for accessing files.
     * @param context
     * @return The absolute cache dir, either on external or internal storage.
     */
    public static String getAbsoluteCacheDir(Context context) {
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
        return cachePath + File.separator
                + Configuration.CACHE_FILES_DIR;
    }


    /**
     * Returns the cache directory path.
     *
     * @param context
     * @return A String with the path to the cache directory.
     */
    public static String getCacheDirectory(Context context) {
        String cachePath = "";
        String rootPath = "";

        //Using External SD
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            File extCacheDir = context.getExternalCacheDir();

            if (null != extCacheDir) {
                rootPath = Environment.getExternalStorageDirectory().getPath();
                cachePath = extCacheDir.getPath();
            } else {
                //TODO: verify if data directory is the root path to remove;
                rootPath = Environment.getDataDirectory().getPath();
                cachePath = context.getCacheDir().getPath();
            }
        }

        //Using Internal Storage
        else {
            //TODO: verify if data directory is the root path to remove;
            rootPath = Environment.getDataDirectory().getPath();
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath.substring(rootPath.length()).concat(File.separator)
                .concat(Configuration.CACHE_FILES_DIR);
    }

    /**
     * Returns the relative to root path to the magazines folder
     * where the magazines packages will be downloaded.
     *
     * @param context
     * @return A string with the path to the magazines folder
     */
    public static String getApplicationRelativeMagazinesPath(Context context) {
        String path = "";
        String rootPath = "";

        //Using external SD
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            File externalDir = context.getExternalFilesDir(null);
            if (null != externalDir) {
                rootPath = Environment.getExternalStorageDirectory().getPath();
                path = externalDir.getPath();
            } else {
                //TODO: verify if data directory is the root path to remove;
                rootPath = Environment.getDataDirectory().getPath();
                path = context.getFilesDir().getPath();
            }
        }

        //Using Internal Storage
        else {
            //TODO: verify if data directory is the root path to remove;
            rootPath = Environment.getDataDirectory().getPath();
            path = context.getFilesDir().getPath();
        }

        return path.substring(rootPath.length()).concat(File.separator)
                .concat(Configuration.MAGAZINES_FILES_DIR);
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

    public static boolean deleteDirectory(final String path) {
        File directory = new File(path);

        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files == null) {
                return true;
            }

            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file.getPath());
                } else {
                    file.delete();
                }
            }
        } else {
            return false;
        }

        return (directory.delete());
    }
}
