package com.giniem.gindpubs.workers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.giniem.gindpubs.Configuration;

public class UnzipperTask extends AsyncTask<String, Long, Boolean> {

	private File magazinesDirectory;

	public UnzipperTask(Context context) {
		this.magazinesDirectory = Configuration.getDiskDir(context);
	}

	@Override
	protected Boolean doInBackground(String... params) {
		InputStream input;
		ZipInputStream zipInput;
		try {
			String zipEntryName;
			
			Log.d(this.getClass().toString(),"Started unzip process for file " + params[0]);
			
			// First we create a directory to hold the unzipped files.
			String workingDir = this.magazinesDirectory.getPath() + File.separator;
			File containerDir = new File(workingDir + params[1]);
			containerDir.mkdirs();
			
			input = new FileInputStream(workingDir + params[0]);
			
			workingDir = workingDir + params[1] + File.separator;
			zipInput = new ZipInputStream(new BufferedInputStream(input));
			ZipEntry zipEntry;
			byte[] buffer = new byte[1024];
			int read;

			while ((zipEntry = zipInput.getNextEntry()) != null) {
				
				zipEntryName = zipEntry.getName();
				Log.d(this.getClass().toString(), "Unzipping entry " + zipEntryName);
				if (zipEntry.isDirectory()) {
					File innerDirectory = new File(workingDir + zipEntryName);
					innerDirectory.mkdirs();
					continue;
				}

				FileOutputStream output = new FileOutputStream(workingDir + zipEntryName);
				while ((read = zipInput.read(buffer)) != -1) {
					output.write(buffer, 0, read);
				}

				output.close();
				zipInput.closeEntry();
			}

			zipInput.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		Log.d(this.getClass().toString(), "Unzip process finished successfully.");
		return true;
	}

	@Override
	protected void onProgressUpdate(Long... progress) {
	}

	@Override
	protected void onPostExecute(final Boolean result) {
	}

}
