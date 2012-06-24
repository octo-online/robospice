package com.octo.android.rest.client.contentmanager;

import java.io.Serializable;

import org.springframework.web.client.RestClientException;

import android.os.Bundle;

import com.octo.android.rest.client.contentservice.AbstractContentService;
import com.octo.android.rest.client.webservice.WebService;

public abstract class RestRequest<ACTIVITY, RESULT> implements Serializable {
	private static final long serialVersionUID = 1008863412866054970L;
	private Bundle optionalBundle;
	private boolean useCache;
	private boolean isServiceParallelizable;
	private transient ACTIVITY activity;
	protected int mRequestId;

	public RestRequest( ACTIVITY activity,
			Bundle optionalBundle, boolean useCache,
			boolean isServiceParallelizable) {
		this.activity = activity;
		this.optionalBundle = optionalBundle;
		this.useCache = useCache;
		this.isServiceParallelizable = isServiceParallelizable;
	}

	@SuppressWarnings("unchecked")
	public Class<RESULT> getResultType() {
		return (Class<RESULT>) getClass().getTypeParameters().getClass();
	}

	public Bundle getOptionalBundle() {
		return optionalBundle;
	}

	public void setOptionalBundle(Bundle optionalBundle) {
		this.optionalBundle = optionalBundle;
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
		return mRequestId == AbstractContentManager.FINISHED_REQUEST_ID;
	}

	public final void onRequestFinished(int requestId, int resultCode, RESULT result) {
		if( requestId == getRequestId() ) {
			if( resultCode == AbstractContentService.RESULT_OK && result != null) {
				onRequestSuccess(result );
			} else {
				onRequestFailure(resultCode );
			}
		}
	}

	public abstract RESULT loadDataFromNetwork( WebService webService) throws RestClientException;

	protected abstract void onRequestFailure( int resultCode);

	protected abstract void onRequestSuccess( RESULT result);

	public abstract String getCacheKey();

}