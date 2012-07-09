package com.octo.android.rest.client.persistence.simple;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.Application;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.octo.android.rest.client.persistence.DataClassPersistenceManager;

public final class BinaryPersistenceManager extends DataClassPersistenceManager<InputStream> {


	public BinaryPersistenceManager(Application application) {
		super(application);
	}

	@Override
	public InputStream loadDataFromCache(Object cacheFileName) throws FileNotFoundException {
		return new FileInputStream( new File(getApplication().getCacheDir(), cacheFileName.toString()) );
	}

	@Override
	public InputStream saveDataToCacheAndReturnData(InputStream data,
			Object cacheFileName) throws FileNotFoundException, IOException {
		// special case for inputstream object : as it can be read only once,
		// 0) we extract the content of the input stream as a byte[]
		// 1) we save it in file asynchronously
		// 2) the result will be a new InputStream on the byte[]
		byte[] byteArray = ByteStreams.toByteArray(data);
		ByteStreams.write(byteArray, Files.newOutputStreamSupplier(new File(getApplication().getCacheDir(), cacheFileName.toString())) );
		return new ByteArrayInputStream(byteArray);		
	}

	@Override
	public boolean canHandleData(Class<?> clazz) {
		try {
			clazz.asSubclass(InputStream.class);
			return true;
		}
		catch( ClassCastException ex ) {
			return false;
		}
	}
}
