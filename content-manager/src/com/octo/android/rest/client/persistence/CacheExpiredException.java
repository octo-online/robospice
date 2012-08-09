package com.octo.android.rest.client.persistence;

public class CacheExpiredException extends Exception {

	private static final long serialVersionUID = 1795922527662609583L;

	public CacheExpiredException() {
		super();
	}

	public CacheExpiredException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public CacheExpiredException(String detailMessage) {
		super(detailMessage);
	}

	public CacheExpiredException(Throwable throwable) {
		super(throwable);
	}
}
