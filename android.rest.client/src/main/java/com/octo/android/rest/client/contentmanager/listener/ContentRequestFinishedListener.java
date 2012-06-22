package com.octo.android.rest.client.contentmanager.listener;

/**
 * Interface to implement to be notified when a request is finished
 * 
 * @author mwa
 * 
 */
public interface ContentRequestFinishedListener {

	/**
	 * Event fired when a request is finished
	 * 
	 * @param requestId
	 *            The request Id (to see if this is the right request)
	 * @param resultCode
	 *            The result code (0 if there was no error)
	 * @param result
	 *            The result of the service execution
	 */
	public void onRequestFinished(int requestId, int resultCode, Object result);
}