package com.giniem.gindpubs;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class BitmapCache extends AsyncTask<String, Integer, Bitmap> {

	private File cacheDirectory;

	private ImageView imageView;

	public BitmapCache(Context context, ImageView imageView) {
		this.cacheDirectory = Configuration.getDiskCacheDir(context);
		this.imageView = imageView;
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		try {

			Bitmap bmp = null;
			File directory = this.cacheDirectory;
			
			if (!directory.exists()) {
				directory.mkdirs();
			}
			
			File output = new File(directory.getPath() + File.separator + params[1]);
			if (!output.exists()) {
				URL url = new URL(params[0]);
				bmp = BitmapFactory.decodeStream(url.openConnection()
						.getInputStream());
				
				FileOutputStream out = new FileOutputStream(output);
				bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
				
			} else {
				bmp = BitmapFactory.decodeFile(output.getPath());
			}
			
			return bmp;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPostExecute(final Bitmap result) {
		if (null != result) {
			imageView.setImageBitmap(result);
		}
	}
}
