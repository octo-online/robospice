package com.octo.android.rest.client.request;

import com.octo.android.rest.client.ContentService;

public abstract class ContentRequest<RESULT> {

	private boolean isCanceled = false;
	private Class<RESULT> resultType;

	public ContentRequest(Class<RESULT> clazz) {
		super();
		this.resultType = clazz;
	}

	public Class<RESULT> getResultType() {
		return resultType;
	}

	public final void onRequestFinished(final int resultCode, final RESULT result) {
		if( resultCode == ContentService.RESULT_OK && result != null) {
			onRequestSuccess(result );
		} else {
			onRequestFailure(resultCode );
		}
	}

	public abstract RESULT loadData() throws Exception;

	protected abstract void onRequestFailure(int resultCode);

	protected abstract void onRequestSuccess(RESULT result);

	public void cancel() {
		this.isCanceled = true;
	}

	public boolean isCanceled() {
		return this.isCanceled;
	}

}