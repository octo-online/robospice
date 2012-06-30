package com.octo.android.rest.client.persistence.simple;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

import android.app.Application;

import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.octo.android.rest.client.persistence.DataClassPersistenceManager;

@Singleton
public final class StringPersistenceManager extends DataClassPersistenceManager<String> {

	@Inject
	Application application;

	@Override
	public String loadDataFromCache(String cacheFileName) throws FileNotFoundException, IOException {
		return CharStreams.toString( Files.newReader( new File(application.getCacheDir(), cacheFileName), Charset.forName("UTF-8") ) );
	}

	@Override
	public String saveDataToCacheAndReturnData(String data, String cacheFileName)
			throws FileNotFoundException, IOException {	
		Files.write(data, new File(application.getCacheDir(), cacheFileName), Charset.forName("UTF-8"));
		return data;
	}

	@Override
	public boolean canHandleData(Class<?> clazz) {
		return clazz.equals( String.class );
	}

}
