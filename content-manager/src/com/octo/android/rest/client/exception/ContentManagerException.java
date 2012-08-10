package com.octo.android.rest.client.exception;

public class ContentManagerException extends Exception {

	private static final long serialVersionUID = 4494147890739338461L;

	public ContentManagerException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public ContentManagerException(Throwable throwable) {
		super(throwable);
	}

}
