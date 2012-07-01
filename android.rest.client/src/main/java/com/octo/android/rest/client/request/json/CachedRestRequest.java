package com.octo.android.rest.client.request.json;


import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import android.content.Context;

import com.octo.android.rest.client.persistence.DataPersistenceManager;
import com.octo.android.rest.client.request.ContentRequest;
import com.octo.android.rest.client.restservice.RestTemplateFactory;

public abstract class CachedRestRequest<RESULT> extends ContentRequest<RESULT> {
	private static final int FINISHED_REQUEST_ID = -1;
	protected int mRequestId;

	private DataPersistenceManager persistenceManager;
	private RestTemplateFactory restTemplateFactory;

	public CachedRestRequest( Context context, Class<RESULT> clazz, DataPersistenceManager persistenceManager, RestTemplateFactory restTemplateFactory) {
		super(clazz);
		this.persistenceManager = persistenceManager;
		this.restTemplateFactory = restTemplateFactory;
	}



	// ============================================================================================
	// METHODS
	// ============================================================================================

	public int getRequestId() {
		return mRequestId;
	}

	public void setRequestId(int requestId) {
		this.mRequestId = requestId;
	}

	/**
	 * Indicates whether or not request is finished or still pending.
	 * 
	 * @return true if request is finished or false if it is still pending.
	 */
	public boolean isRequestFinished() {
		return mRequestId == FINISHED_REQUEST_ID;
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

	@Override
	public RESULT loadDataFromNetwork() throws RestClientException {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	protected void onRequestFailure(int resultCode) {
		// TODO Auto-generated method stub

	}



	@Override
	protected void onRequestSuccess(RESULT result) {
		// TODO Auto-generated method stub

	}



	@Override
	public String getCacheKey() {
		// TODO Auto-generated method stub
		return null;
	}


}