package com.octo.android.rest.client.persistence.simple;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

import android.app.Application;

import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.octo.android.rest.client.persistence.CacheExpiredException;
import com.octo.android.rest.client.persistence.DataClassPersistenceManager;

public final class StringPersistenceManager extends DataClassPersistenceManager<String> {

	public StringPersistenceManager(Application application) {
		super(application);
	}

	@Override
	public String loadDataFromCache(Object cacheKey, long maxTimeInCacheBeforeExpiry) throws FileNotFoundException, IOException, CacheExpiredException {
		File file = getCacheFile(cacheKey);
		if( file.exists() ) {
			long timeInCache = System.currentTimeMillis() - file.lastModified();
			if( maxTimeInCacheBeforeExpiry == 0 || timeInCache <= maxTimeInCacheBeforeExpiry ) {
				return CharStreams.toString( Files.newReader( file, Charset.forName("UTF-8") ) );
			} else {
				throw new CacheExpiredException( "Cache content is expired since " + (maxTimeInCacheBeforeExpiry-timeInCache) );
			}
		}
		throw new FileNotFoundException( "File was not found in cache: " + file.getAbsolutePath() );
		
	}
	
	private File getCacheFile( Object cacheKey) {
		return new File(getApplication().getCacheDir(), cacheKey.toString());
	}

	@Override
	public String saveDataToCacheAndReturnData(String data, Object cacheKey)
			throws FileNotFoundException, IOException {	
		Files.write(data, getCacheFile(cacheKey), Charset.forName("UTF-8"));
		return data;
	}

	@Override
	public boolean canHandleData(Class<?> clazz) {
		return clazz.equals( String.class );
	}

}
