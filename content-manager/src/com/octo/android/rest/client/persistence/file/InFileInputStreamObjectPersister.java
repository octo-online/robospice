package com.octo.android.rest.client.persistence.file;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.Application;
import android.util.Log;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.octo.android.rest.client.exception.CacheLoadingException;
import com.octo.android.rest.client.exception.CacheSavingException;

public final class InFileInputStreamObjectPersister extends InFileObjectPersister<InputStream> {

	private final static String LOG_CAT = InFileInputStreamObjectPersister.class.getSimpleName();

	public InFileInputStreamObjectPersister(Application application) {
		super(application);
	}

	@Override
	public InputStream loadDataFromCache(Object cacheKey, long maxTimeInCacheBeforeExpiry) throws CacheLoadingException {
		File file = getCacheFile(cacheKey);
		if (file.exists()) {
			long timeInCache = System.currentTimeMillis() - file.lastModified();
			if (maxTimeInCacheBeforeExpiry == 0 || timeInCache <= maxTimeInCacheBeforeExpiry) {
				try {
					return new FileInputStream(file);
				}
				catch (FileNotFoundException e) {
					// Should not occur (we test before if file exists)
					// Do not throw, file is not cached
					Log.w(LOG_CAT, "file " + file.getAbsolutePath() + " does not exists", e);
					return null;
				}
			}
		}
		Log.v(LOG_CAT, "file " + file.getAbsolutePath() + " does not exists");
		return null;
	}

	@Override
	public InputStream saveDataToCacheAndReturnData(InputStream data, Object cacheKey) throws CacheSavingException {
		// special case for inputstream object : as it can be read only once,
		// 0) we extract the content of the input stream as a byte[]
		// 1) we save it in file asynchronously
		// 2) the result will be a new InputStream on the byte[]
		byte[] byteArray;
		try {
			byteArray = ByteStreams.toByteArray(data);
			ByteStreams.write(byteArray, Files.newOutputStreamSupplier(getCacheFile(cacheKey)));
			return new ByteArrayInputStream(byteArray);
		}
		catch (IOException e) {
			throw new CacheSavingException(e);
		}
	}

	public boolean canHandleClass(Class<?> clazz) {
		try {
			clazz.asSubclass(InputStream.class);
			return true;
		}
		catch (ClassCastException ex) {
			return false;
		}
	}
}
