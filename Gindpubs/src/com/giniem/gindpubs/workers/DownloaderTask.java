package com.giniem.gindpubs.workers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.giniem.gindpubs.Configuration;
import com.giniem.gindpubs.views.MagazineThumb;

public class DownloaderTask extends AsyncTask<String, Long, String> {

	private File magazinesDirectory;
	
	private MagazineThumb magThumb;
	
	public DownloaderTask(Context context) {
		this.magazinesDirectory = Configuration.getDiskDir(context);
	}
	
	public DownloaderTask(MagazineThumb thumb) {
		this(thumb.getContext());
		this.magThumb = thumb;
	}
	
	@Override
	protected String doInBackground(String... params) {
		try {
			
			if (!this.magazinesDirectory.exists()) {
				this.magazinesDirectory.mkdirs();
			}
			
			URL url = new URL(params[0]);
			URLConnection connection = url.openConnection();
			connection.connect();

			long fileLength = connection.getContentLength();
			String outputFilename = this.magazinesDirectory.getPath()
					+ File.separator + params[1] + ".zip";

			InputStream input = new BufferedInputStream(url.openStream());
			OutputStream output = new FileOutputStream(outputFilename);

			byte buffer[] = new byte[1024];
			long total = 0;
			int read;
			while ((read = input.read(buffer)) != -1) {
				total += read;
				publishProgress((long) (total * 100 / fileLength), total, fileLength);
				output.write(buffer, 0, read);
			}

			output.flush();
			output.close();
			input.close();
			return outputFilename;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	protected void onProgressUpdate(Long... progress) {
		if (null != this.magThumb) {
			Log.e("PROGRESS", "" + progress[0]);
			this.magThumb.updateProgress(progress[0], progress[1], progress[2]);
		}
    }
	
	@Override
	protected void onPostExecute(final String result) {
		this.magThumb.showActions();
	}

}
