package com.octo.android.rest.client.request.simple;


import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import roboguice.RoboGuice;
import android.content.Context;

import com.google.inject.Inject;
import com.octo.android.rest.client.persistence.DataPersistenceManager;
import com.octo.android.rest.client.request.ContentRequest;
import com.octo.android.rest.client.restservice.RestTemplateFactory;

public abstract class CachedRestContentRequest<RESULT> extends ContentRequest<RESULT> {
	private static final int FINISHED_REQUEST_ID = -1;
	protected int mRequestId;
	
	@Inject private DataPersistenceManager persistenceManager;
	@Inject private RestTemplateFactory webService;

	public CachedRestContentRequest( Context context, Class<RESULT> clazz) {
		super(clazz);
		this.persistenceManager = RoboGuice.getInjector(context).getInstance(DataPersistenceManager.class);
		this.webService = RoboGuice.getInjector(context).getInstance(RestTemplateFactory.class);
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
		return webService.createRestTemplate();
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