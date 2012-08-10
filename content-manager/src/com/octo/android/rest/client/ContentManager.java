package com.octo.android.rest.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.octo.android.rest.client.ContentService.ContentServiceBinder;
import com.octo.android.rest.client.request.ContentRequest;
import com.octo.android.rest.client.request.RequestListener;

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
public class ContentManager extends Thread {

	private ContentService contentService;
	private ContentServiceConnection contentServiceConnection = new ContentServiceConnection();
	private Context context;

	private boolean isStopped;
	private Queue<ContentRequest<?>> requestQueue = new LinkedList<ContentRequest<?>>();
	private Map<ContentRequest<?>, Boolean> mapRequestToCacheUsageFlag = new HashMap<ContentRequest<?>, Boolean>();
	private Map<ContentRequest<?>, List<RequestListener<?>>> mapRequestToRequestListener = Collections.synchronizedMap(new IdentityHashMap<ContentRequest<?>, List<RequestListener<?>>>());

	private Object lockQueue = new Object();
	private Object lockAcquireService = new Object();

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

		while (!isStopped) {
			synchronized (lockQueue) {
				if (!requestQueue.isEmpty()) {
					ContentRequest<?> restRequest = requestQueue.poll();
					boolean useCache = mapRequestToCacheUsageFlag.get(restRequest);
					mapRequestToCacheUsageFlag.remove(restRequest);
					contentService.addRequest(restRequest, mapRequestToRequestListener.get(restRequest), useCache);
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

	public void shouldStop() {
		this.isStopped = true;
		unbindService(context);
	}

	private void bindService(Context context) {
		Intent intentService = new Intent(context, ContentService.class);
		contentServiceConnection = new ContentServiceConnection();
		context.bindService(intentService, contentServiceConnection, Context.BIND_AUTO_CREATE);
	}

	private void unbindService(Context context) {
		if (contentService != null) {
			context.unbindService(this.contentServiceConnection);
		}
	}

	public void addRequestToQueue(ContentRequest<?> request, RequestListener<?> listener, boolean useCache) {
		synchronized (lockQueue) {
			this.mapRequestToCacheUsageFlag.put(request, useCache);
			this.requestQueue.add(request);

			// add listener to listeners list for this request
			List<RequestListener<?>> listeners = mapRequestToRequestListener.get(request);
			if (listeners == null) {
				listeners = new ArrayList<RequestListener<?>>();
			}
			else if (!listeners.contains(listener)) {
				listeners.add(listener);
			}
			this.mapRequestToRequestListener.put(request, listeners);

			lockQueue.notifyAll();
		}
	}

	public void cancel(ContentRequest<?> request) {
		request.cancel();
	}

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

	public void cancelAllRequests() {
		for (ContentRequest<?> restRequest : requestQueue) {
			restRequest.cancel();
		}
	}

}
