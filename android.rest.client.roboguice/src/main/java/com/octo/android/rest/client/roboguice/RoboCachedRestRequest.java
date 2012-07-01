package com.octo.android.rest.client.roboguice;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.web.client.RestTemplate;

import roboguice.RoboGuice;

import com.octo.android.rest.client.persistence.DataPersistenceManager;
import com.octo.android.rest.client.request.ContentRequest;
import com.octo.android.rest.client.restservice.RestTemplateFactory;

import android.content.Context;

public abstract class RoboCachedRestRequest<RESULT> extends ContentRequest<RESULT> {
	private DataPersistenceManager persistenceManager;
	private RestTemplateFactory restTemplateFactory;

	public RoboCachedRestRequest(Context context, Class<RESULT> clazz) {
		super(clazz);
		this.persistenceManager = RoboGuice.getInjector(context).getInstance(DataPersistenceManager.class);
		this.restTemplateFactory = RoboGuice.getInjector(context).getInstance(RestTemplateFactory.class);
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
