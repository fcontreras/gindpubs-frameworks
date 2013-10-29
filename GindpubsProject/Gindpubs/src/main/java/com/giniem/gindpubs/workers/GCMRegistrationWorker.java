package com.giniem.gindpubs.workers;

import android.content.Context;
import android.os.AsyncTask;

import com.giniem.gindpubs.R;
import com.giniem.gindpubs.client.GindMandator;
import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by Holland on 10-23-13.
 */
public class GCMRegistrationWorker extends AsyncTask<Void, Long, String[]> {

    private GoogleCloudMessaging gcm;
    private Context context;
    private GindMandator mandator;
    private int taskId;

    public void setGcm(GoogleCloudMessaging _gcm) {
        this.gcm = _gcm;
    }

    public GCMRegistrationWorker(Context _context,
                                 GoogleCloudMessaging _gcm,
                                 int _taskId,
                                 GindMandator _mandator) {
        this.gcm = _gcm;
        this.context = _context;
        this.taskId = _taskId;
        this.mandator = _mandator;
    }

    @Override
    protected String[] doInBackground(Void... params) {
        String msg = "";
        String regid = "";
        try {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(context);
            }
            regid = gcm.register(context.getString(R.string.sender_id));
            msg = "SUCCESS";

            // You should send the registration ID to your server over HTTP,
            // so it can use GCM/HTTP or CCS to send messages to your app.
            // The request to your server should be authenticated if your app
            // is using accounts.
            sendRegistrationIdToBackend();

            // For this demo: we don't need to send it because the device
            // will send upstream messages to a server that echo back the
            // message using the 'from' address in the message.

            // Persist the regID - no need to register again.
            //storeRegistrationId(context, regid);
        } catch (Exception ex) {
            msg = "ERROR";
            // If there is an error, don't just keep trying to register.
            // Require the user to click a button again, or perform
            // exponential back-off.
        }

        String results[] = {msg, regid};

        return results;
    }

    @Override
    protected void onPostExecute(String[] results) {
        this.mandator.postExecute(this.taskId, results);
    }

    private void sendRegistrationIdToBackend() {
        // Your implementation here.
    }
}
