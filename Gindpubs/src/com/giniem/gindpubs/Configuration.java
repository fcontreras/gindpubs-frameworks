package com.giniem.gindpubs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.content.Context;
import android.content.res.AssetManager;

public class Configuration {
	
	private static String NEWSSTAND_MANIFEST_URL;
	private static String NEWSSTAND_MANIFEST_URL_IPHONE;
	private static String PURCHASE_CONFIRMATION_URL;
	private static String PURCHASES_URL;
	private static String POST_APNS_TOKEN_URL;
	private static String POST_APNS_TOKEN_URL_IPHONE;
	private static String FREE_SUBSCRIPTION_PRODUCT_ID;
	private static String AUTO_RENEWABLE_SUBSCRIPTION_PRODUCT_IDS;
	private static Integer REQUEST_TIMEOUT;

	public static String getNEWSSTAND_MANIFEST_URL() {
		return NEWSSTAND_MANIFEST_URL;
	}

	public static String getNEWSSTAND_MANIFEST_URL_IPHONE() {
		return NEWSSTAND_MANIFEST_URL_IPHONE;
	}

	public static String getPURCHASE_CONFIRMATION_URL() {
		return PURCHASE_CONFIRMATION_URL;
	}

	public static String getPURCHASES_URL() {
		return PURCHASES_URL;
	}

	public static String getPOST_APNS_TOKEN_URL() {
		return POST_APNS_TOKEN_URL;
	}

	public static String getPOST_APNS_TOKEN_URL_IPHONE() {
		return POST_APNS_TOKEN_URL_IPHONE;
	}

	public static String getFREE_SUBSCRIPTION_PRODUCT_ID() {
		return FREE_SUBSCRIPTION_PRODUCT_ID;
	}

	public static String getAUTO_RENEWABLE_SUBSCRIPTION_PRODUCT_IDS() {
		return AUTO_RENEWABLE_SUBSCRIPTION_PRODUCT_IDS;
	}

	public static Integer getREQUEST_TIMEOUT() {
		return REQUEST_TIMEOUT;
	}

	private Configuration() {
	}

	public static void load(Context context) throws IOException {
			AssetManager assetManager = context.getAssets();
			
			InputStream inputStream = assetManager.open("configuration.properties");
			
			Properties properties = new Properties();
			properties.load(inputStream);
			
			NEWSSTAND_MANIFEST_URL = properties.getProperty("NEWSSTAND_MANIFEST_URL");
			NEWSSTAND_MANIFEST_URL_IPHONE = properties.getProperty("NEWSSTAND_MANIFEST_URL_IPHONE");
			PURCHASE_CONFIRMATION_URL = properties.getProperty("PURCHASE_CONFIRMATION_URL");
			PURCHASES_URL = properties.getProperty("PURCHASES_URL");
			POST_APNS_TOKEN_URL = properties.getProperty("POST_APNS_TOKEN_URL");
			POST_APNS_TOKEN_URL_IPHONE = properties.getProperty("POST_APNS_TOKEN_URL_IPHONE");
			FREE_SUBSCRIPTION_PRODUCT_ID = properties.getProperty("FREE_SUBSCRIPTION_PRODUCT_ID");
			AUTO_RENEWABLE_SUBSCRIPTION_PRODUCT_IDS = properties.getProperty("AUTO_RENEWABLE_SUBSCRIPTION_PRODUCT_IDS");
			REQUEST_TIMEOUT = Integer.parseInt(properties.getProperty("REQUEST_TIMEOUT"));
	}
}
