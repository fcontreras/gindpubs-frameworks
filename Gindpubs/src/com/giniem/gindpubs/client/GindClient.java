package com.giniem.gindpubs.client;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

public class GindClient {
	
	private String shelfJson;
	
	public String getShelfJson() {
		return shelfJson;
	}

	public JSONArray shelfJsonGet(final String url) throws JSONException,
			ParseException, IOException {
		JSONArray json = new JSONArray();
		BasicResult basicResult = new BasicResult();

		Log.d(this.getClass().getName(),
				"Sending request to get the shelf JSON data to URL " + url);

		HttpResponse response = this.get(url);
		if (null != response) {
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				
				String value = EntityUtils.toString(entity);
				
				json = new JSONArray(value);
				this.shelfJson = value;
			} else {
				json = new JSONArray();
				json.put(basicResult.errorJson(response.getStatusLine()
						.getStatusCode()
						+ ": "
						+ response.getStatusLine().getReasonPhrase()));
			}
		} else {
			json = new JSONArray();
			json.put(basicResult.errorJson("Error in HTTP response."));
		}

		return json;
	}
	
	public HttpResponse get(final String url) {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = null;
		try {
			HttpGet httpGet = new HttpGet(url);

			response = httpClient.execute(httpGet);

		} catch (IOException ex) {
			ex.printStackTrace();
			response = null;
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return response;
	}
}
