package com.octo.android.rest.client.request;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.Context;

import com.octo.android.rest.client.persistence.DataPersistenceManager;

public abstract class CachedContentRequest<RESULT> extends ContentRequest<RESULT> {

	protected DataPersistenceManager persistenceManager;

	public CachedContentRequest(Context context, Class clazz, DataPersistenceManager dataPersistenceManager) {
		super(clazz);
		this.persistenceManager = dataPersistenceManager;
	}

	@Override
	public RESULT loadDataFromCache(String cacheFileName) throws FileNotFoundException,
			IOException {
				return persistenceManager.getDataClassPersistenceManager(getResultType()).loadDataFromCache(cacheFileName);
			}

	@Override
	public RESULT saveDataToCacheAndReturnData(RESULT data, String cacheFileName)
			throws FileNotFoundException, IOException {
				return persistenceManager.getDataClassPersistenceManager(getResultType()).saveDataToCacheAndReturnData(data, cacheFileName);
			}

}