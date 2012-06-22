package com.octo.android.rest.client.contentservice;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.octo.android.rest.client.utils.CacheFileUtils;

public abstract class BinaryContentService extends AbstractContentService<InputStream> {

	// ============================================================================================
	// CONSTRUCTOR
	// ============================================================================================

	public BinaryContentService(String name) {
		super(name);
	}

	// ============================================================================================
	// PUBLIC METHODS
	// ============================================================================================

	@Override
	protected final InputStream loadDataFromCache(String cacheFileName) {
		return CacheFileUtils.readInputStreamFromFile(this, cacheFileName);
	}

	@Override
	protected final void saveDataToCache(InputStream data, String cacheFileName) {
		CacheFileUtils.writeInputStreamToFile(this, cacheFileName, data);
	}

	@Override
	protected final InputStream asyncSaveDataToCacheAndReturnData(InputStream result, String cacheFilename) throws IOException {
		// special case for inputstream object : as it can be read only once,
		// 0) we extract the content of the input stream as a byte[]
		// 1) we save it in file asynchronously
		// 2) the result will be a new InputStream on the byte[]
		byte[] byteArray = IOUtils.toByteArray(result);
		new CacheWritingThread(new ByteArrayInputStream(byteArray), cacheFilename).start();
		return new ByteArrayInputStream(byteArray);
	}
}
