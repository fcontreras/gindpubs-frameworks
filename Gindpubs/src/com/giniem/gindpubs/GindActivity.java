package com.giniem.gindpubs;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.giniem.gindpubs.client.GindClientTask;

public class GindActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
        	// Load configuration, then you are ready to get the properties.
			Configuration.load(this);
			
			GindClientTask asyncClient = new GindClientTask(this);
			asyncClient.execute(Configuration.getNEWSSTAND_MANIFEST_URL());
	        
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(this.getClass().getName(), "Cannot load configuration.");
		}
        
        setContentView(R.layout.activity_gind);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gind, menu);
        return true;
    }
    
    public void addTexts() {
    	
    }
}