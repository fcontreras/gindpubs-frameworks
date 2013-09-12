package com.giniem.gindpubs.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.DownloadManager;


import com.giniem.gindpubs.client.GindMandator;
import com.giniem.gindpubs.model.BookJson;
import com.giniem.gindpubs.GindActivity;
import com.giniem.gindpubs.R;
import com.giniem.gindpubs.model.Magazine;
import com.giniem.gindpubs.workers.BitmapCache;
import com.giniem.gindpubs.workers.BookJsonParserTask;
import com.giniem.gindpubs.workers.UnzipperTask;
import com.giniem.gindpubs.workers.DownloaderTask;
import com.giniem.gindpubs.Configuration;


public class MagazineThumb extends LinearLayout implements GindMandator {

    private Magazine magazine;
	private BookJson book;
	private LinearLayout parent;
    private DownloadManager dm;
    private String dirPath;
    private String cachePath;
    private DownloaderTask packDownloader;
    private DownloaderTask thumbDownloader;

    private final int THUMB_DOWNLOAD_TASK = 0;
    private final int THUMB_DOWNLOAD_VISIBILITY = DownloadManager.Request.VISIBILITY_HIDDEN;
    private final int MAGAZINE_DOWNLOAD_TASK = 1;
    private final int MAGAZINE_DOWNLOAD_VISIBILITY = DownloadManager.Request.VISIBILITY_VISIBLE;

    /**
     * Creates an instance of MagazineThumb to with an activity context.
     * @param context the parent Activity context.
     */
	public MagazineThumb(Context context, Magazine mag) {
		super(context);

        //Constructor initialization
        dm = (DownloadManager) context.getSystemService(Activity.DOWNLOAD_SERVICE);
        dirPath = Configuration.getApplicationRelativeMagazinesPath(context);
        cachePath = Configuration.getCacheDirectory(context);
        this.magazine = mag;
        packDownloader = new DownloaderTask(context,
                this,
                this.MAGAZINE_DOWNLOAD_TASK,
                this.magazine.getUrl(),
                this.magazine.getName(),
                this.magazine.getTitle(),
                this.magazine.getInfo(),
                this.dirPath,
                this.MAGAZINE_DOWNLOAD_VISIBILITY);
        thumbDownloader = new DownloaderTask(context,
                this,
                this.THUMB_DOWNLOAD_TASK,
                this.magazine.getCover(),
                this.magazine.getName(), //Set the same name as the magazine
                this.magazine.getTitle().concat(" cover"),
                "File to show on the self",
                cachePath,
                this.THUMB_DOWNLOAD_VISIBILITY);
        //Logging initialization
        Log.d(this.getClass().getName(), "Magazines relative dir: " + dirPath);
    }

	public void init(final Context context, AttributeSet attrs) {
		setOrientation(LinearLayout.HORIZONTAL);
		setGravity(Gravity.CENTER_VERTICAL);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.magazine_thumb_options, this, true);
		parent = (LinearLayout) getChildAt(0);

        //Calculating size in MB
		this.magazine.setSizeMB(this.magazine.getSize() / 1048576);
		
		ImageView imageView = (ImageView) parent.getChildAt(0);
		try {
			BitmapCache dit = new BitmapCache(context, imageView);
			// We pass the cover URL and the name to save the cached image with that
			// name.
			dit.execute(this.magazine.getCover(), this.magazine.getName());
		} catch (Exception ex) {
		}
		TextView tvTitle = (TextView) ((LinearLayout) parent.getChildAt(1))
				.getChildAt(0);
		tvTitle.setEllipsize(null);
		tvTitle.setSingleLine(false);
		tvTitle.setText(this.magazine.getTitle());

		TextView tvInfo = (TextView) ((LinearLayout) parent.getChildAt(1))
				.getChildAt(1);
		tvInfo.setEllipsize(null);
		tvInfo.setSingleLine(false);
		tvInfo.setText(this.magazine.getInfo());

		TextView tvDate = (TextView) ((LinearLayout) parent.getChildAt(1))
				.getChildAt(2);
		tvDate.setEllipsize(null);
		tvDate.setSingleLine(false);
		tvDate.setText(this.magazine.getDate());

 		TextView tvSize = (TextView) ((LinearLayout) parent.getChildAt(1))
				.getChildAt(3);
		tvSize.setEllipsize(null);
		tvSize.setSingleLine(false);
		tvSize.setText(this.magazine.getSizeMB() + " MB");

		LinearLayout downloadLayout = (LinearLayout) ((LinearLayout) parent.getChildAt(1))
				.getChildAt(5);
		TextView tvProgress = (TextView) downloadLayout.getChildAt(0);
		tvProgress.setText("0 MB / " + this.magazine.getSizeMB() + " MB");

		// Click on the DOWNLOAD button.
		Button buttonDownload = (Button) ((LinearLayout) parent.getChildAt(1))
				.getChildAt(4);
		buttonDownload.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				Button button = (Button) v;
				button.setVisibility(View.GONE);
				LinearLayout progress = (LinearLayout) ((LinearLayout) parent.getChildAt(1))
						.getChildAt(5);
				progress.setVisibility(View.VISIBLE);

                packDownloader.execute();
			}
		});

		// Click on the VIEW button.
		LinearLayout actionsLayout = (LinearLayout) ((LinearLayout) parent.getChildAt(1))
				.getChildAt(6);
		Button buttonView = (Button) (actionsLayout.getChildAt(0));
		buttonView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				BookJsonParserTask parser = new BookJsonParserTask(
						MagazineThumb.this);
				parser.execute(magazine.getName());
			}
		});

	}

	public void startUnzip(String filePath, String name) {
		LinearLayout actionsUI = (LinearLayout) ((LinearLayout) parent.getChildAt(1))
				.getChildAt(5);
		TextView tvProgress = (TextView) actionsUI.getChildAt(0);
		tvProgress.setText(R.string.unzipping);

		UnzipperTask unzipper = new UnzipperTask(this);
		unzipper.execute(filePath, name);
	}

	public void showActions() {
		Button buttonDownload = (Button) ((LinearLayout) parent.getChildAt(1))
				.getChildAt(4);
			buttonDownload.setVisibility(View.GONE);
		
		LinearLayout downloadingUI = (LinearLayout) ((LinearLayout) parent.getChildAt(1))
				.getChildAt(5);
		downloadingUI.setVisibility(View.GONE);

		LinearLayout actionsUI = (LinearLayout) ((LinearLayout) parent.getChildAt(1))
				.getChildAt(6);
		actionsUI.setVisibility(View.VISIBLE);
	}

	public void setBookJson(BookJson bookJson) {
		this.book = bookJson;

		if (null != this.book) {
			GindActivity activity = (GindActivity) this.getContext();
			activity.viewMagazine(this.book);

		} else {
			Toast.makeText(this.getContext(), "Not valid book.json found!",
					Toast.LENGTH_LONG).show();
		}
	}

    public Magazine getMagazine() {
        return magazine;
    }

    public void setMagazine(Magazine magazine) {
        this.magazine = magazine;
    }

    public void updateProgress(final int taskId, Long... progress) {
        //Update only when downloading the magazines
        if (taskId == this.MAGAZINE_DOWNLOAD_TASK) {
            LinearLayout progressLayout = (LinearLayout) ((LinearLayout) parent.getChildAt(1))
                    .getChildAt(5);

            long fileProgress = progress[1] / 1048576;
            long length = progress[2] / 1048576;
            TextView tvProgress = (TextView) progressLayout.getChildAt(0);
            tvProgress.setText(String.valueOf(fileProgress) + " MB / " + length
                    + " MB");
            ProgressBar progressBar = (ProgressBar) progressLayout.getChildAt(1);
            Integer intProgress = (int) (long) progress[0];
            progressBar.setProgress(intProgress);
        }
    };

    public void postExecute(final int taskId, String... results) {
        switch (taskId) {
            case MAGAZINE_DOWNLOAD_TASK:
                if (results[0] == "SUCCESS") {
                    startUnzip(results[1], this.magazine.getName());
                }
                //TODO: See how to handle failures on download
                break;
        }
    };
}
