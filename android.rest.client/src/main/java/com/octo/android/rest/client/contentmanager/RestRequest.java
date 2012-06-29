package com.octo.android.rest.client.contentmanager;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import roboguice.RoboGuice;
import android.app.Activity;
import android.content.Context;

import com.google.inject.Inject;
import com.octo.android.rest.client.contentservice.ContentService;
import com.octo.android.rest.client.contentservice.persistence.DataPersistenceManager;
import com.octo.android.rest.client.webservice.WebService;

public abstract class RestRequest<RESULT> {
	private static final int FINISHED_REQUEST_ID = -1;
	protected int mRequestId;
	protected Class<RESULT> resultType;
	private boolean isCanceled = false;
	private Context context;
	
	@Inject
	private DataPersistenceManager persistenceManager;
	@Inject
	private WebService webService;
	
	public RestRequest( Context context, Class<RESULT> clazz) {
		this.context = context;
		this.persistenceManager = RoboGuice.getInjector(context).getInstance(DataPersistenceManager.class);
		this.webService = RoboGuice.getInjector(context).getInstance(WebService.class);
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

	public final void onRequestFinished(final int requestId, final int resultCode, final RESULT result) {
		if( context instanceof Activity) {
			((Activity)context).runOnUiThread( new Runnable() {
				public void run() {
					processResponse(requestId, resultCode, result);
				}
			});
		} else {
			processResponse(requestId, resultCode, result);
		}
	}

	private void processResponse(int requestId, int resultCode, RESULT result) {
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
	
	protected RestTemplate getRestTemplate() {
		return webService.getRestTemplate();
	}

	public abstract RESULT loadDataFromNetwork() throws RestClientException;

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