package com.octo.android.rest.client.contentmanager;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import com.octo.android.rest.client.contentmanager.listener.ContentRequestFinishedListener;
import com.octo.android.rest.client.contentservice.AbstractContentService;
import com.octo.android.rest.client.utils.ContentRequestBundleUtils;

/**
 * Class used to manage content received from web service. <br/>
 * <ul>
 * <li>Start a {@link Service} to request the web service</li>
 * <li>Manage the communication between the Service and the Activity or Fragment : maintains a list of requests and a list of result receiver (listener)</li>
 * </ul>
 * 
 * @author jva
 * 
 */
public class AbstractContentManager {

	// ============================================================================================
	// CONSTANTS
	// ============================================================================================

	public static final int FINISHED_REQUEST_ID = -1;

	protected static final String INTENT_EXTRA_SERVICE_TYPE = "INTENT_EXTRA_SERVICE_TYPE";
	public static final String INTENT_EXTRA_REQUEST_ID = "INTENT_EXTRA_REQUEST_ID";
	public static final String INTENT_EXTRA_RECEIVER = "INTENT_EXTRA_RECEIVER";

	private static final String LOG_CAT_TAG = "AbstractContentManager";

	// ============================================================================================
	// ATTRIBUTES
	// ============================================================================================

	/** Random generator for generating request ids. */
	protected static Random sRandom = new Random();

	/** List of requests (fragments or activities content requests). */
	protected SparseArray<Intent> mRequestSparseArray;

	/**
	 * List of listeners (fragments or activities, or their inner classes that listen for a request result) using CopyOnWriteArrayList to avoid ConcurrentModificationException.
	 */
	protected CopyOnWriteArrayList<ContentRequestFinishedListener> mListenersList;

	protected Context mContext;
	protected Handler mHandler = new Handler();
	protected ServiceResultReceiver mServiceResultReceiver = new ServiceResultReceiver(mHandler);

	// ============================================================================================
	// CONSTRUCTOR
	// ============================================================================================

	public AbstractContentManager(Context context) {
		mContext = context;
		mRequestSparseArray = new SparseArray<Intent>();
		mListenersList = new CopyOnWriteArrayList<ContentRequestFinishedListener>();
	}

	// ============================================================================================
	// METHODS
	// ============================================================================================

	public boolean isRequestInProgress(final int requestId) {
		return (mRequestSparseArray.indexOfKey(requestId) >= 0);
	}

	/**
	 * Handle the service result :
	 * <ul>
	 * <li>retrieve the request id,</li>
	 * <li>remove it from the list of request (because the request is finished)</li>
	 * <li>call the listeners to tell them the request is finished</li>
	 * </ul>
	 * 
	 * @param resultCode
	 *            result code of the service
	 * @param resultBundle
	 *            result content of the service
	 */
	protected void handleServiceResult(int resultCode, Bundle resultBundle) {
		// Get the request Id
		int requestId = resultBundle.getInt(INTENT_EXTRA_REQUEST_ID);
		final int serviceType = resultBundle.getInt(INTENT_EXTRA_SERVICE_TYPE);

		mRequestSparseArray.remove(requestId);

		// Call the available listeners
		synchronized (mListenersList) {
			Object result = null;

			if (!mListenersList.isEmpty() && resultCode == AbstractContentService.RESULT_OK) {
				// get object if result is ok :
				try {
					result = ContentRequestBundleUtils.getResultFromBundle(resultBundle);
					if (result != null) {
						result = doProcessResultIfNeeded(serviceType, result);
					}
				}
				catch (Exception e) {
					Log.e(LOG_CAT_TAG, "Unable to get result object from bundle before listener call",e );
				}
			}

			// notify listeners, whatever result we have.
			for (ContentRequestFinishedListener listener : mListenersList) {
				if (listener != null) {
					listener.onRequestFinished(requestId, resultCode, result);
				}
			}
		}
	}

	/**
	 * Hook method to be overridden by subclasses if needed. Process the result obtained from the service and convert the result into somthing else if needed.
	 * 
	 * @param serviceType
	 *            type of service.
	 * @param result
	 *            the result from the request.
	 * @return a new processed result. This default implementation simply return the param result.
	 */
	protected Object doProcessResultIfNeeded(int serviceType, Object result) {
		return result;
	}

	/**
	 * Retrieve a content for a specific request (serviceType)
	 * 
	 * @param serviceType
	 *            The request to execute
	 * @param serviceClass
	 *            The service class to launch (must extends {@link AbstractContentService})
	 * @param optionalBundle
	 *            params to add in the intent bundle for the service
	 * @param useCache
	 *            use local file cache system
	 * @param isServiceParallelizable
	 *            can service be used multiple times to parallelize content request (e.g. : image downloading)
	 * @return an int value containing the requestId, will be used to retrieve the request in the list of request
	 */
	protected int requestContentWithService(int serviceType, Class<? extends AbstractContentService<?>> serviceClass, Bundle optionalBundle, boolean useCache, boolean isServiceParallelizable) {

		checkContentServiceIsDeclaredInManifest(serviceClass);

		if (!isServiceParallelizable) {
			// search in the list if the intent is already there
			for (int requestIndex = 0; requestIndex < mRequestSparseArray.size(); requestIndex++) {
				final Intent savedIntent = mRequestSparseArray.valueAt(requestIndex);

				if (savedIntent.getIntExtra(INTENT_EXTRA_SERVICE_TYPE, -1) == serviceType) {
					return mRequestSparseArray.keyAt(requestIndex);
				}

			}
		}

		// set cache :
		if (optionalBundle == null) {
			optionalBundle = new Bundle();
		}
		optionalBundle.putBoolean(AbstractContentService.BUNDLE_EXTRA_CACHE_ENABLED, useCache);

		// if the request is not already pending, create a new request and launch the Service
		final int requestId = sRandom.nextInt(Integer.MAX_VALUE);

		Intent intent = new Intent(mContext, serviceClass);
		intent.putExtra(INTENT_EXTRA_SERVICE_TYPE, serviceType);
		intent.putExtra(INTENT_EXTRA_RECEIVER, mServiceResultReceiver);
		intent.putExtra(INTENT_EXTRA_REQUEST_ID, requestId);
		if (optionalBundle != null) {
			intent.putExtras(optionalBundle);
		}
		mContext.startService(intent);
		mRequestSparseArray.append(requestId, intent);

		return requestId;
	}

	private void checkContentServiceIsDeclaredInManifest(Class<? extends AbstractContentService<?>> serviceClass) {
		Intent i = new Intent();
		i.setClass(mContext, serviceClass);
		List<ResolveInfo> listResolveInfo = mContext.getPackageManager().queryIntentServices(i, 0);
		if (listResolveInfo == null || listResolveInfo.isEmpty()) {
			Log.e(LOG_CAT_TAG, "The service class " + serviceClass.getName() + " is not declared in manifest !");
			throw new RuntimeException("The service class " + serviceClass.getName() + " is not declared in manifest !");
		}
	}

	/**
	 * Add a {@link ContentRequestFinishedListener} to this {@link AbstractContentManager}. Clients may use it in order to listen to events fired when a request is finished.
	 * <p>
	 * <b>Warning !! </b> If it's an {@link Activity} or {@link Fragment} that is used as a Listener, it must be detached when {@link Activity#onPause} or {@link Fragment#onPause()} is called.
	 * </p>
	 * 
	 * @param listener
	 *            The listener to add
	 */
	public void addOnRequestFinishedListener(final ContentRequestFinishedListener listener) {
		synchronized (mListenersList) {
			if (!mListenersList.contains(listener)) {
				mListenersList.add(listener);
			}
		}
	}

	/**
	 * Remove a {@link OnRequestFinishedListener} to this {@link AbstractContentManager}.
	 * 
	 * @param listener
	 *            The listener to remove
	 */
	public void removeOnRequestFinishedListener(final ContentRequestFinishedListener listener) {
		synchronized (mListenersList) {
			mListenersList.remove(listener);
		}
	}

	// ============================================================================================
	// INNER CLASSES
	// ============================================================================================

	/**
	 * The ResultReceiver that will receive the result from the Service
	 * 
	 * author mwa
	 * 
	 */
	protected class ServiceResultReceiver extends ResultReceiver {
		ServiceResultReceiver(final Handler handler) {
			super(handler);
		}

		@Override
		public void onReceiveResult(final int resultCode, final Bundle resultData) {
			handleServiceResult(resultCode, resultData);
		}
	}

}
