package com.octo.android.rest.client.contentmanager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;

import com.octo.android.rest.client.contentservice.ContentService;
import com.octo.android.rest.client.contentservice.ContentService.ContentServiceBinder;

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
	private Queue<RestRequest<?>> requestQueue = new LinkedList<RestRequest<?>>();
	private Map<RestRequest<?>, Boolean> mapRequestToCacheUsageFlag = new HashMap<RestRequest<?>, Boolean>();

	private Object lockQueue = new Object();
	private Object lockAcquireService = new Object();
	
	Handler handlerResponse = new Handler();

	@Override
	public final synchronized void start() {
		throw new IllegalStateException( "Can't be started without context.");
	}

	public synchronized void start(Context context) {
		this.context = context;
		super.start();
	}

	public void run() {
		bindService( context );

		synchronized (lockAcquireService) {
			while( contentService == null ) {
				try {
					lockAcquireService.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		while( !isStopped ) {
			if( !requestQueue.isEmpty() ) {
				RestRequest<?> restRequest = requestQueue.poll();
				boolean useCache = mapRequestToCacheUsageFlag.get(restRequest);
				mapRequestToCacheUsageFlag.remove(restRequest);
				contentService.addRequest(restRequest, handlerResponse, useCache );
			}

			synchronized (lockQueue) {
				while( requestQueue.isEmpty()) {
					try {
						lockQueue .wait();
					} catch (InterruptedException e) {
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

	private void bindService(Context context ) {
		Intent intentService = new Intent(context, ContentService.class);
		contentServiceConnection = new ContentServiceConnection();
		context.bindService(intentService, contentServiceConnection, Context.BIND_AUTO_CREATE);
	}

	private void  unbindService(Context context) {
		context.unbindService(this.contentServiceConnection);
	}

	public void addRequestToQueue(RestRequest<?> request, boolean useCache) {
		synchronized (lockQueue) {
			this.requestQueue.add( request);
			this.mapRequestToCacheUsageFlag.put(request, useCache);
		}
	}

	public void cancel(RestRequest<?> request) {
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
		for( RestRequest<?> restRequest : requestQueue ) {
			restRequest.cancel();
		}
	}

}
