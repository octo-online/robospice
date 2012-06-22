package com.octo.android.rest.client.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.app.Application;
import android.content.res.AssetManager;
import android.util.Log;
import android.widget.Toast;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.octo.android.rest.client.BuildConfig;

/**
 * Static class used to load env.properties file to know the actual environment and environment configuration<br />
 * The properties file is in assets folder, it contains the variable "env" with value "DEV" or "PROD"<br/>
 * The file is commit in source control with the "PROD" value<br/>
 * In Eclipse, edit the file to set "DEV" value
 * 
 * @author jva
 * 
 */
@Singleton
public final class EnvironmentConfigService {

	// ============================================================================================
	// CONSTANTS
	// ============================================================================================

	private static final String CONFIG_FILE = "env.properties";

	private static final String ENV_KEY = "env";
	private static final String DEFAULT_ENV_VALUE = "PROD";
	private static final String WS_URL_KEY = "ws.url";
	private static final String WEB_URL_KEY = "web.url";

	private static final String WS_URL_KEY_OTHER = "ws.url.other";
	private static final String WEB_URL_KEY_OTHER = "web.url.other";

	// ============================================================================================
	// ATTRIBUTES
	// ============================================================================================
	private Properties properties;

	@Inject
	private Application mApplication;

	// ============================================================================================
	// ENUM
	// ============================================================================================
	public static enum Environment {
		DEV,
		PROD;
	}

	// ============================================================================================
	// METHODS
	// ============================================================================================

	@Inject
	protected EnvironmentConfigService(AssetManager assetManager) {
		if (properties == null) {
			try {
				InputStream inputStream = assetManager.open(CONFIG_FILE);
				properties = new Properties();
				properties.load(inputStream);
			}
			catch (IOException e) {
				Log.e("Cetelem", "Configuration file loading failed");
			}
		}
	}

	public Environment getEnvironment() {
		String env = properties.getProperty(ENV_KEY, DEFAULT_ENV_VALUE);
		return Environment.valueOf(env);
	}

	public String getWebServiceUrl() {
		String key = BuildConfig.DEBUG ? WS_URL_KEY : WS_URL_KEY_OTHER;

		String url = properties.getProperty(key, null);
		if (url == null) {
			Log.e("Cetelem", "Configuration file loading failed");
			Toast.makeText(mApplication,"Configuration file loading failed",Toast.LENGTH_SHORT).show();
		}
		return url;
	}

	public String getWebUrl() {
		String key = BuildConfig.DEBUG ? WS_URL_KEY : WS_URL_KEY_OTHER;

		String url = properties.getProperty(key, null);
		if (url == null) {
			Log.e("Cetelem", "Configuration file loading failed");
			Toast.makeText(mApplication,"Configuration file loading failed",Toast.LENGTH_SHORT).show();
		}
		return url;
	}

	public boolean isDev() {
		return getEnvironment() == Environment.DEV;
	}
}
