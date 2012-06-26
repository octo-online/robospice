package com.octo.android.rest.client.contentmanager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.springframework.web.client.RestClientException;
import java.lang.reflect.ParameterizedType;

import android.os.Bundle;

import com.octo.android.rest.client.contentservice.ContentService;
import com.octo.android.rest.client.webservice.WebService;

public abstract class RestRequest<ACTIVITY, RESULT> implements Serializable {
	private static final long serialVersionUID = 1008863412866054970L;
	private Bundle bundle = new Bundle();
	private boolean useCache;
	private boolean isServiceParallelizable;
	private transient ACTIVITY activity;
	protected int mRequestId;

	public RestRequest() {
		
	}
	public RestRequest( ACTIVITY activity,
			boolean useCache,
			boolean isServiceParallelizable) {
		this.activity = activity;
		this.useCache = useCache;
		this.isServiceParallelizable = isServiceParallelizable;
	}

	@SuppressWarnings("unchecked")
	public Class<RESULT> getResultType() {
		return (Class<RESULT>) ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[1];
	}

	public Bundle getBundle() {
		return bundle;
	}

	public void setBundle(Bundle bundle) {
		this.bundle = bundle;
	}

	public boolean isUseCache() {
		return useCache;
	}

	public void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}

	public boolean isServiceParallelizable() {
		return isServiceParallelizable;
	}

	public void setServiceParallelizable(boolean isServiceParallelizable) {
		this.isServiceParallelizable = isServiceParallelizable;
	}
	
	protected ACTIVITY getActivity() {
		return this.activity;
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
		return mRequestId == ContentManager.FINISHED_REQUEST_ID;
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

	public abstract RESULT loadDataFromNetwork( WebService webService, Bundle bundle ) throws RestClientException;

	protected abstract void onRequestFailure( int resultCode);

	protected abstract void onRequestSuccess( RESULT result);

	public abstract String getCacheKey();

	// do nothing when serializing object
	private void writeObject(ObjectOutputStream out) throws IOException {}
	
	// do nothing when deserializing object
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {}

}