package com.octo.android.rest.client.contentmanager;

import android.os.Bundle;
import android.widget.Toast;

import com.octo.android.rest.client.HelloAndroidActivity;
import com.octo.android.rest.client.contentmanager.listener.OnAbstractContentRequestFinishedListener;
import com.octo.android.rest.client.contentservice.AbstractContentService;

public abstract class RestRequest<RESULT> extends OnAbstractContentRequestFinishedListener<RESULT> {
	private int serviceType;
	private Class<? extends AbstractContentService<?>> serviceClass;
	private Bundle optionalBundle;
	private boolean useCache;
	private boolean isServiceParallelizable;

	public RestRequest(int serviceType,
			Class<? extends AbstractContentService<?>> serviceClass,
					Bundle optionalBundle, boolean useCache,
					boolean isServiceParallelizable) {
		this.serviceType = serviceType;
		this.serviceClass = serviceClass;
		this.optionalBundle = optionalBundle;
		this.useCache = useCache;
		this.isServiceParallelizable = isServiceParallelizable;
	}

	public int getServiceType() {
		return serviceType;
	}

	public void setServiceType(int serviceType) {
		this.serviceType = serviceType;
	}

	public Class<? extends AbstractContentService<?>> getServiceClass() {
		return serviceClass;
	}

	public void setServiceClass(
			Class<? extends AbstractContentService<?>> serviceClass) {
		this.serviceClass = serviceClass;
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

	public final void onRequestFinished(int requestId, int resultCode, RESULT result) {
		if( requestId == getRequestId() ) {
			if( resultCode == AbstractContentService.RESULT_OK && result != null) {
				onRequestSuccess( result );
			} else {
				onRequestFailure( resultCode );
			}
		}
	}

	protected abstract void onRequestFailure(int resultCode);

	protected abstract void onRequestSuccess(RESULT result);

	
}