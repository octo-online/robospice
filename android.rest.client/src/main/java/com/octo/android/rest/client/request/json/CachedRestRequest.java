package com.octo.android.rest.client.request.json;


import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.web.client.RestTemplate;

import android.content.Context;

import com.octo.android.rest.client.persistence.DataPersistenceManager;
import com.octo.android.rest.client.request.ContentRequest;
import com.octo.android.rest.client.restservice.RestTemplateFactory;

public abstract class CachedRestRequest<RESULT> extends ContentRequest<RESULT> {

	private DataPersistenceManager persistenceManager;
	private RestTemplateFactory restTemplateFactory;

	public CachedRestRequest( Context context, Class<RESULT> clazz, DataPersistenceManager persistenceManager, RestTemplateFactory restTemplateFactory) {
		super(clazz);
		this.persistenceManager = persistenceManager;
		this.restTemplateFactory = restTemplateFactory;
	}

	protected RestTemplate getRestTemplate() {
		return restTemplateFactory.createRestTemplate();
	}

	@Override
	public RESULT loadDataFromCache(String cacheFileName)
			throws FileNotFoundException, IOException {
		return persistenceManager.getDataClassPersistenceManager(getResultType()).loadDataFromCache(cacheFileName);
	}

	@Override
	public RESULT saveDataToCacheAndReturnData(RESULT data, String cacheFileName)
			throws FileNotFoundException, IOException {
		return persistenceManager.getDataClassPersistenceManager(getResultType()).saveDataToCacheAndReturnData(data, cacheFileName);
	}

}