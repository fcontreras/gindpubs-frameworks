package com.giniem.gindpubs.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.ParseException;
import org.json.JSONArray;
import org.json.JSONException;

import android.os.AsyncTask;

import com.giniem.gindpubs.Configuration;
import com.giniem.gindpubs.GindActivity;

public class GindClientTask extends AsyncTask<String, Integer, JSONArray> {
	
	private GindClient client;
	
	private GindActivity activity;
	
	private File cacheDirectory;
	
	public GindClientTask(GindActivity parent) {
		this.client = new GindClient();
		this.cacheDirectory = Configuration.getDiskCacheDir(parent);
		this.activity = parent;
	}
	
	public GindClient getGindClient() {
		return this.client;
	}

	@Override
	protected void onPostExecute(final JSONArray result) {
		activity.createThumbnails(result);
	}

	@Override
	protected JSONArray doInBackground(String... params) {
		JSONArray json = new JSONArray();
		
		try {
			
			File directory = this.cacheDirectory;
			
			if (!directory.exists()) {
				directory.mkdirs();
			}
			
			if (Configuration.hasInternetConnection(this.activity)) {
				json = client.shelfJsonGet(params[0]);

				File output = new File(directory.getPath() + File.separator + Configuration.getJSON_FILENAME());
				FileOutputStream out = new FileOutputStream(output);
				out.write(json.toString().getBytes());
				out.close();
				
			} else {
				File input = new File(directory.getPath() + File.separator + Configuration.getJSON_FILENAME());
				FileInputStream in = new FileInputStream(input);
				byte[] buffer = new byte[1024];
				StringBuffer rawData = new StringBuffer("");
				
				while (in.read(buffer) != -1) {
					rawData.append(new String(buffer));
				}
				in.close();
				json = new JSONArray(rawData.toString());
			}
		} catch (ParseException e) {
		} catch (JSONException e) {
		} catch (IOException e) {
		}

		return json;
	}
}