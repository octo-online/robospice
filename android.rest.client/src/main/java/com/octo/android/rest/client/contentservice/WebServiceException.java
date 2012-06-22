package com.octo.android.rest.client.contentservice;

public class WebServiceException extends Exception {

	private static final long serialVersionUID = -3359284952037895591L;

	public WebServiceException() {
	}

	public WebServiceException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public WebServiceException(Throwable throwable) {
		super(throwable);
	}

	public WebServiceException(String message) {
		super(message);
	}
}
