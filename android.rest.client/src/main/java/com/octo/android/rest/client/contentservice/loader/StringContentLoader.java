package com.octo.android.rest.client.contentservice.loader;

import java.io.IOException;

import android.util.Log;

import com.google.inject.Singleton;
import com.octo.android.rest.client.utils.CacheFileUtils;

@Singleton
public final class StringContentLoader extends DataContentLoader<String> {
	
	@Override
	public String loadDataFromCache(String cacheFileName) {
		return CacheFileUtils.readStringContentFromFile(getApplication(), cacheFileName);
	}

	@Override
	public final void saveDataToCache(String data, String cacheFileName) {
		try {
			CacheFileUtils.saveStringToFile(getApplication(), data, cacheFileName);
		}
		catch (IOException e) {
			Log.e(getClass().getName(),"Unable to save web service result into the cache",e);
		}
	}

	@Override
	public boolean canHandleData(Class<?> clazz) {
		return clazz.equals( String.class );
	}

}
