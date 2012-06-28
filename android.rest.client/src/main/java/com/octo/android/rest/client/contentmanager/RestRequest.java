package com.octo.android.rest.client.contentmanager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.springframework.web.client.RestClientException;

import roboguice.RoboGuice;

import android.content.Context;
import android.os.Bundle;

import com.google.inject.Inject;
import com.octo.android.rest.client.contentservice.ContentService;
import com.octo.android.rest.client.contentservice.persistence.DataPersistenceManager;
import com.octo.android.rest.client.webservice.WebService;

public abstract class RestRequest<RESULT> {
	private static final int FINISHED_REQUEST_ID = -1;
	protected int mRequestId;
	protected Class<RESULT> resultType;
	private boolean isCanceled = false;
	
	@Inject
	private DataPersistenceManager persistenceManager;

	public RestRequest( Context context, Class<RESULT> clazz) {
		this.persistenceManager = RoboGuice.getInjector(context).getInstance(DataPersistenceManager.class);
		this.resultType = clazz;
	}

	public Class<RESULT> getResultType() {
		return resultType;
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

	public final void onRequestFinished(int requestId, int resultCode, RESULT result) {
		if( requestId == getRequestId() ) {
			if( resultCode == ContentService.RESULT_OK && result != null) {
				onRequestSuccess(result );
			} else {
				onRequestFailure(resultCode );
			}
		}
	}
	
	public RESULT loadDataFromCache( String cacheFileName) throws FileNotFoundException, IOException {
		return persistenceManager.getDataClassPersistenceManager(getResultType() ).loadDataFromCache(cacheFileName);
	}
	
	public RESULT saveDataToCacheAndReturnData(RESULT data, String cacheFileName) throws FileNotFoundException, IOException {
		return persistenceManager.getDataClassPersistenceManager(getResultType() ).saveDataToCacheAndReturnData(data, cacheFileName);
	}

	public abstract RESULT loadDataFromNetwork( WebService webService, Bundle bundle ) throws RestClientException;

	protected abstract void onRequestFailure( int resultCode);

	protected abstract void onRequestSuccess( RESULT result);

	public abstract String getCacheKey();

	public void cancel() {
		this.isCanceled = true;
	}

	public boolean isCanceled() {
		return this.isCanceled;
	}

}