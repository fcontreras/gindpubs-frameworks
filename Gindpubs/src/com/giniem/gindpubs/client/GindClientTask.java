package com.giniem.gindpubs.client;

import android.os.AsyncTask;

import com.giniem.gindpubs.Configuration;
import com.giniem.gindpubs.GindActivity;
import com.giniem.gindpubs.R;

import org.apache.http.ParseException;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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
                String baseUrl = params[0];
                String appId = params[1];
                String userId = params[2];

                // We will replace the placeholders for the app_id and user_id.
                baseUrl = baseUrl.replace(":app_id", appId).replace(":user_id", userId);

				json = client.shelfJsonGet(baseUrl);

				File output = new File(directory.getPath() + File.separator + this.activity.getString(R.string.shelf));
				FileOutputStream out = new FileOutputStream(output);
				out.write(json.toString().getBytes());
				out.close();
				
			} else {
				File input = new File(directory.getPath() + File.separator + this.activity.getString(R.string.shelf));
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