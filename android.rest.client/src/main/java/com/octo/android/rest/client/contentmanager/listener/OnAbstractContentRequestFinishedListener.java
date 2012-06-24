package com.octo.android.rest.client.contentmanager.listener;

import com.octo.android.rest.client.contentmanager.AbstractContentManager;

/**
 * Super class whose instances are responsible for receiving the result of the request to get all saving products. Every OnContentRequestFinishedListener can manage a single request.
 * 
 * @author stephanenicolas
 * 
 */
public abstract class OnAbstractContentRequestFinishedListener<ACTIVITY, RESULT> implements ContentRequestFinishedListener<RESULT> {

	// ============================================================================================
	// ATTRIBUTES
	// ============================================================================================

	protected int mRequestId;

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
}
