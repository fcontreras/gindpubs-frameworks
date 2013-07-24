package com.giniem.gindpubs.client;

import java.io.IOException;

import org.apache.http.ParseException;
import org.json.JSONArray;
import org.json.JSONException;

import android.os.AsyncTask;

import com.giniem.gindpubs.GindActivity;

public class GindClientTask extends AsyncTask<String, Integer, JSONArray> {
	
	private GindClient client;
	
	private GindActivity activity;
	
	public GindClientTask(GindActivity parent) {
		client = new GindClient();
		activity = parent;
	}
	
	public GindClient getGindClient() {
		return this.client;
	}

	@Override
	protected void onPostExecute(final JSONArray result) {
		activity.parseShelf(result);
	}

	@Override
	protected JSONArray doInBackground(String... params) {
		JSONArray json = new JSONArray();
		try {
			return client.shelfJsonGet(params[0]);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return json;
	}
}