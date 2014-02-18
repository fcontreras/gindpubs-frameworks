package com.giniem.gindpubs;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class Configuration {

    private static final String LOG_TITLE = ">>>CONFIGURATION";

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

	// Tries to use external storage, if not available then fallback to internal.
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

            Log.d(LOG_TITLE, "EXTERNAL STORAGE IS MOUNTED.");

			File externalDir = context.getExternalFilesDir("");
			if (null != externalDir) {
                Log.d(LOG_TITLE, "USING SD MEMORY CARD.");
				appPath = externalDir.getPath();
                Log.d(LOG_TITLE, "EXTERNAL PATH TO USE: " + appPath);
			} else {
                Log.d(LOG_TITLE, "USING INTERNAL STORAGE.");
				appPath = context.getFilesDir().getPath();
			}
		} else {
            Log.d(LOG_TITLE, "EXTERNAL STORAGE IS *NOT* MOUNTED.");
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
	
	public static boolean hasNetworkConnection(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			return true;
		}
		return false;
	}

    public static Map<String, String> splitUrlQueryString(URL url) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
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

    public static void copyFile(File source, File destination) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(destination);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }

    public static String readFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");

        while((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }

        return stringBuilder.toString();
    }
}
