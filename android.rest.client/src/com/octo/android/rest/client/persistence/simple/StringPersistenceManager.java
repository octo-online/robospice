package com.octo.android.rest.client.persistence.simple;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

import android.app.Application;

import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.octo.android.rest.client.persistence.DataClassPersistenceManager;

public final class StringPersistenceManager extends DataClassPersistenceManager<String> {

	public StringPersistenceManager(Application application) {
		super(application);
	}

	@Override
	public String loadDataFromCache(Object cacheFileName) throws FileNotFoundException, IOException {
		return CharStreams.toString( Files.newReader( new File(getApplication().getCacheDir(), cacheFileName.toString()), Charset.forName("UTF-8") ) );
	}

	@Override
	public String saveDataToCacheAndReturnData(String data, Object cacheFileName)
			throws FileNotFoundException, IOException {	
		Files.write(data, new File(getApplication().getCacheDir(), cacheFileName.toString()), Charset.forName("UTF-8"));
		return data;
	}

	@Override
	public boolean canHandleData(Class<?> clazz) {
		return clazz.equals( String.class );
	}

}
