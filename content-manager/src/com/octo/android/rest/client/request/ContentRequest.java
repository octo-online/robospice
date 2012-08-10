package com.octo.android.rest.client.request;


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

	public abstract RESULT loadData() throws Exception;

	public void cancel() {
		this.isCanceled = true;
	}

	public boolean isCanceled() {
		return this.isCanceled;
	}

}