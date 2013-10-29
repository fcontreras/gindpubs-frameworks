package com.giniem.gindpubs.workers;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.giniem.gindpubs.Configuration;
import com.giniem.gindpubs.client.GindMandator;

import java.io.File;

public class MagazineDeleteTask extends AsyncTask<String, Long, String> {

    private Context context;

    private GindMandator mandator;

    private int taskId;

    public MagazineDeleteTask(Context context, GindMandator mandator, final int taskId) {
        this.context = context;
        this.mandator = mandator;
        this.taskId = taskId;
    }

    @Override
    protected String doInBackground(String... params) {
        String result = "ERROR";

        String filepath = Environment.getExternalStorageDirectory().getPath()
                + Configuration.getApplicationRelativeMagazinesPath(this.context)
                + File.separator + params[0];

        if (this.deleteDirectory(filepath)) {
            result = "SUCCESS";
            Log.d(this.getClass().toString(), "Delete process finished successfully.");
        } else {
            Log.e(this.getClass().toString(), "Could not delete directory: " + params[0]);
        }

        return result;
    }

    private boolean deleteDirectory(final String path) {
        File directory = new File(path);

        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files == null) {
                return true;
            }

            for (File file : files) {
                if (file.isDirectory()) {
                    this.deleteDirectory(file.getPath());
                } else {
                    file.delete();
                }
            }
        } else {
            return false;
        }

        return (directory.delete());
    }

    @Override
    protected void onProgressUpdate(Long... progress) {
    }

    @Override
    protected void onPostExecute(final String result) {
        mandator.postExecute(taskId, result);
    }

}
