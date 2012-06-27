package com.octo.android.rest.client.contentservice.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import android.app.Application;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public final class StringPersistenceManager extends DataClassPersistenceManager<String> {

	@Inject
	Application application;

	@Override
	public String loadDataFromCache(Class<String> clazz, String cacheFileName) throws FileNotFoundException, IOException {
		return IOUtils.toString( new FileInputStream( new File(application.getCacheDir(), cacheFileName) ) );
	}

	@Override
	public String saveDataToCacheAndReturnData(String data, String cacheFileName)
			throws FileNotFoundException, IOException {	
		IOUtils.write(data, new FileOutputStream( new File(application.getCacheDir(), cacheFileName) ) );
		return data;
	}

	@Override
	public boolean canHandleData(Class<?> clazz) {
		return clazz.equals( String.class );
	}

}
