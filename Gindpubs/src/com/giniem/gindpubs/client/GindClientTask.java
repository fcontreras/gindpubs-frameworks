package com.giniem.gindpubs.client;

import java.io.IOException;

import org.apache.http.ParseException;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;

public class GindClientTask extends AsyncTask<String, Integer, JSONArray> {
	
	private Activity activity;
	
	private GindClient client;
	
	public GindClientTask(Activity _activity) {
		this.activity = _activity;
		client = new GindClient();
	}
	
	public GindClient getGindClient() {
		return this.client;
	}

	protected void onPostExecute(final JSONArray result) {
		EditText et=new EditText(activity);
		et.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		et.setText(result.toString());
		
		activity.addContentView(et, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
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