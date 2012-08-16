package com.octo.android.rest.client;

import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.octo.android.rest.client.ContentService.ContentServiceBinder;
import com.octo.android.rest.client.persistence.DurationInMillis;
import com.octo.android.rest.client.request.CachedContentRequest;
import com.octo.android.rest.client.request.ContentRequest;
import com.octo.android.rest.client.request.RequestListener;

/**
 * Class used to manage content received from web service. <br/>
 * <ul>
 * <li>Start a {@link Service} to request the web service</li>
 * <li>Manage the communication between the Service and the Activity or Fragment : maintains a list of requests and a list of result receiver (listener)</li>
 * </ul>
 * 
 * @author jva & sni
 * 
 */
public class ContentManager extends Thread {

	private ContentService contentService;
	private ContentServiceConnection contentServiceConnection = new ContentServiceConnection();
	private Context context;

	private boolean isStopped;
	private Queue<CachedContentRequest<?>> requestQueue = new LinkedList<CachedContentRequest<?>>();
	private Map<CachedContentRequest<?>, Set<RequestListener<?>>> mapRequestToRequestListener = Collections.synchronizedMap(new IdentityHashMap<CachedContentRequest<?>, Set<RequestListener<?>>>());

	private ExecutorService executorService = Executors.newSingleThreadExecutor();

	// TODO use blocking queue
	private Object lockQueue = new Object();
	// TODO use semaphore
	private Object lockAcquireService = new Object();

	// ============================================================================================
	// THREAD BEHAVIOR
	// ============================================================================================

	@Override
	public final synchronized void start() {
		throw new IllegalStateException("Can't be started without context.");
	}

	public synchronized void start(Context context) {
		this.context = context;
		super.start();
	}

	@Override
	public void run() {
		bindService(context);

		waitForServiceToBeBound();

		while (!isStopped) {
			synchronized (lockQueue) {
				if (!requestQueue.isEmpty()) {
					CachedContentRequest<?> restRequest = requestQueue.poll();
					Set<RequestListener<?>> listRequestListener = mapRequestToRequestListener.get(restRequest);
					mapRequestToRequestListener.remove(restRequest);
					contentService.addRequest(restRequest, listRequestListener);
				}

				while (requestQueue.isEmpty()) {
					try {
						lockQueue.wait();
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

		unbindService(context);
	}

	// ============================================================================================
	// PUBLIC EXPOSED METHODS
	// ============================================================================================

	/**
	 * Call this in {@link Activity#onDestroy} to stop the {@link ContentManager} and all request
	 */
	public void shouldStop() {
		this.isStopped = true;
		unbindService(context);
	}

	/**
	 * Execute a request, put the result in cache with key <i>requestCacheKey</i> during <i>cacheDuration</i> millisecond and register listeners to notify when request is finished.
	 * 
	 * @param request
	 *            the request to execute
	 * @param requestCacheKey
	 *            the key used to store and retrieve the result of the request in the cache
	 * @param cacheDuration
	 *            the time in millisecond to keep cache alive (see {@link DurationInMillis})
	 * @param requestListener
	 *            the listener to notify when the request will finish
	 */
	public <T> void execute(ContentRequest<T> request, String requestCacheKey, long cacheDuration, RequestListener<T> requestListener) {
		synchronized (lockQueue) {
			CachedContentRequest<T> cachedContentRequest = new CachedContentRequest<T>(request, requestCacheKey, cacheDuration);
			// add listener to listeners list for this request
			Set<RequestListener<?>> listeners = mapRequestToRequestListener.get(request);
			if (listeners == null) {
				listeners = new HashSet<RequestListener<?>>();
				this.mapRequestToRequestListener.put(cachedContentRequest, listeners);
			}
			listeners.add(requestListener);

			this.requestQueue.add(cachedContentRequest);
			lockQueue.notifyAll();
		}
	}

	/**
	 * Execute a request, put the result in cache and register listeners to notify when request is finished.
	 * 
	 * @param request
	 *            the request to execute. {@link CachedContentRequest} is a wrapper of {@link ContentRequest} that contains cache key and cache duration
	 * @param requestListener
	 *            the listener to notify when the request will finish
	 */
	public <T> void execute(CachedContentRequest<T> cachedContentRequest, RequestListener<T> requestListener) {
		synchronized (lockQueue) {
			// add listener to listeners list for this request
			Set<RequestListener<?>> listeners = mapRequestToRequestListener.get(cachedContentRequest);
			if (listeners == null) {
				listeners = new HashSet<RequestListener<?>>();
			}
			else if (!listeners.contains(requestListener)) {
				listeners.add(requestListener);
			}
			this.mapRequestToRequestListener.put(cachedContentRequest, listeners);
			this.requestQueue.add(cachedContentRequest);
			lockQueue.notifyAll();
		}
	}

	/**
	 * Cancel a specific request
	 * 
	 * @param request
	 *            the request to cancel
	 */
	public void cancel(ContentRequest<?> request) {
		request.cancel();
	}

	/**
	 * Cancel all requests
	 */
	public void cancelAllRequests() {
		synchronized (lockQueue) {
			for (ContentRequest<?> restRequest : requestQueue) {
				restRequest.cancel();
			}
		}
	}

	/**
	 * Remove some specific content from cache
	 * 
	 * @param clazz
	 *            the Type of data you want to remove from cache
	 * @param cacheKey
	 *            the key of the object in cache
	 * @return true if the data has been deleted from cache
	 */
	public <T> void removeDataFromCache(final Class<T> clazz, final Object cacheKey) {
		executorService.execute(new Runnable() {

			public void run() {
				waitForServiceToBeBound();
				contentService.removeDataFromCache(clazz, cacheKey);
			}
		});
	}

	public void removeAllDataFromCache() {
		executorService.execute(new Runnable() {

			public void run() {
				waitForServiceToBeBound();
				contentService.removeAllDataFromCache();
			}
		});
	}

	/**
	 * Configure the behavior in case of error during reading/writing cache. <br/>
	 * Specify wether an error on reading/writing cache must fail the process.
	 * 
	 * @param failOnCacheError
	 *            true if an error must fail the process
	 */
	public void setFailOnCacheError(final boolean failOnCacheError) {
		executorService.execute(new Runnable() {

			public void run() {
				waitForServiceToBeBound();
				contentService.setFailOnCacheError(failOnCacheError);
			}
		});
	}

	private void waitForServiceToBeBound() {
		synchronized (lockAcquireService) {
			while (contentService == null) {
				try {
					lockAcquireService.wait();
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Disable request listeners notifications for a specific request.<br/>
	 * All listeners associated to this request won't be called when request will finish.<br/>
	 * Should be called in {@link Activity#onPause}
	 * 
	 * @param request
	 *            Request on which you want to disable listeners
	 */
	public void dontNotifyRequestListenersForRequest(ContentRequest<?> request) {
		for (CachedContentRequest<?> cachedContentRequest : mapRequestToRequestListener.keySet()) {
			if (cachedContentRequest.getContentRequest().equals(request)) {
				mapRequestToRequestListener.remove(cachedContentRequest);
				break;
			}
		}
	}

	/**
	 * Disable request listeners notifications for all requests. <br/>
	 * Should be called in {@link Activity#onPause}
	 */
	public void dontNotifyAnyRequestListeners() {
		mapRequestToRequestListener.clear();
	}

	// ============================================================================================
	// INNER CLASS
	// ============================================================================================
	public class ContentServiceConnection implements ServiceConnection {

		public void onServiceConnected(ComponentName name, IBinder service) {
			contentService = ((ContentServiceBinder) service).getContentService();
			synchronized (lockAcquireService) {
				lockAcquireService.notifyAll();
			}
		}

		public void onServiceDisconnected(ComponentName name) {
			contentService = null;
		}
	}

	// ============================================================================================
	// PRIVATE
	// ============================================================================================
	private void bindService(Context context) {
		Intent intentService = new Intent();
		intentService.setAction("com.octo.android.rest.client.ContentService");
		contentServiceConnection = new ContentServiceConnection();
		context.bindService(intentService, contentServiceConnection, Context.BIND_AUTO_CREATE);
	}

	private void unbindService(Context context) {
		if (contentService != null) {
			context.unbindService(this.contentServiceConnection);
		}
	}
}