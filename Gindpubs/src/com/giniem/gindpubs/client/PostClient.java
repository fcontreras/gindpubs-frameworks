package com.giniem.gindpubs.client;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;

public class PostClient extends AsyncTask<String, Long, String> {

    private GindMandator executor;

    private final int asyncTaskId;

    private ArrayList<BasicNameValuePair> parameters;

    public PostClient(int taskId, GindMandator _executor) {
        this.asyncTaskId = taskId;
        this.executor = _executor;
        parameters = new ArrayList();
    }

    public void addParameter(String name, String value) {
        this.parameters.add(new BasicNameValuePair(name, value));
    }

    @Override
    protected String doInBackground(final String... params) {
        String result = "ERROR";

        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(params[0]);

            ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();

            postParameters.addAll(this.parameters);

            httpPost.setEntity(new UrlEncodedFormEntity(postParameters));

            HttpResponse response = httpClient.execute(httpPost);

            if (null != response) {
                if (response.getStatusLine().getStatusCode() == 200) {

                    result = EntityUtils.toString(response.getEntity());
                }
            }

        } catch (Exception ex) {
        }

        return result;
    }

    @Override
    protected void onPostExecute(final String result) {
        this.executor.postExecute(this.asyncTaskId, result);
    }

}
