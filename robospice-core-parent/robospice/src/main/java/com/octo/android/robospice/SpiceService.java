package com.octo.android.robospice;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import roboguice.util.temp.Ln;
import android.app.Application;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.octo.android.robospice.networkstate.DefaultNetworkStateChecker;
import com.octo.android.robospice.networkstate.NetworkStateChecker;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.exception.CacheLoadingException;
import com.octo.android.robospice.persistence.exception.CacheSavingException;
import com.octo.android.robospice.priority.PriorityThreadPoolExecutor;
import com.octo.android.robospice.request.CachedSpiceRequest;
import com.octo.android.robospice.request.RequestProcessor;
import com.octo.android.robospice.request.RequestProcessorListener;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.listener.RequestStatus;
import com.octo.android.robospice.request.listener.SpiceServiceServiceListener;
import com.octo.android.robospice.request.observer.ObserverManager;
import com.octo.android.robospice.request.observer.ObserversNotSupportedException;
import com.octo.android.robospice.request.observer.RequestObserverFactory;
import com.octo.android.robospice.request.reporter.DefaultRequestProgressReporter;
import com.octo.android.robospice.request.reporter.RequestProgressReporter;
import com.octo.android.robospice.request.reporter.RequestProgressReporterWithObserverSupport;
import com.octo.android.robospice.request.tracker.RequestTracker;
import com.octo.android.robospice.request.tracker.RequestTrackerService;

/**
 * This is an abstract class used to manage the cache and provide web service
 * result to an activity. <br/>
 * Extends this class to provide a service able to load content from web service
 * or cache (if available and enabled). You will have to implement
 * {@link #createCacheManager(Application)} to configure the
 * {@link CacheManager} used by all requests to persist their results in the
 * cache (and load them from cache if possible).
 * @author jva
 * @author mwa
 * @author sni
 */
public abstract class SpiceService extends Service {

    // ----------------------------------
    // CONSTANTS
    // ----------------------------------
    private static final int NOTIFICATION_ID = 42;

    protected static final int DEFAULT_THREAD_COUNT = 1;
    protected static final int DEFAULT_THREAD_PRIORITY = Thread.MIN_PRIORITY;

    private static final boolean DEFAULT_FAIL_ON_CACHE_ERROR = false;

    // ----------------------------------
    // ATTRIBUTES
    // ----------------------------------
    private SpiceServiceBinder mSpiceServiceBinder;

    /** Responsible for processing requests. */
    private RequestProcessor requestProcessor;

    private int currentPendingRequestCount = 0;

    private boolean isBound;

    private Notification notification;

    /** Responsible for persisting data. */
    private CacheManager cacheManager;

    private ObserverManager observerManager;
    private RequestProgressReporter progressReporter;

    private final SelfStopperRequestProcessorListener requestProcessorListener = new SelfStopperRequestProcessorListener();

    private RequestTracker requestTracker;

    // ----------------------------------
    // CONSTRUCTOR
    // ----------------------------------

    /**
     * Default constructor.
     */
    public SpiceService() {
        mSpiceServiceBinder = new SpiceServiceBinder(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            cacheManager = createCacheManager(getApplication());
        } catch (CacheCreationException e) {
            Ln.e(e);
            stopSelf();
            return;
        }
        if (cacheManager == null) {
            Ln.e(new CacheCreationException("createCacheManager() can't create a null cacheManager"));
            stopSelf();
            return;
        }

        progressReporter = createRequestProgressReporter();

        if (progressReporter instanceof RequestProgressReporterWithObserverSupport)
        {
            observerManager = createObserverManager();

            ((RequestProgressReporterWithObserverSupport) progressReporter).setObserverManager(observerManager);
        }

        final ExecutorService executorService = getExecutorService();
        final NetworkStateChecker networkStateChecker = getNetworkStateChecker();

        requestProcessor = createRequestProcessor(executorService, networkStateChecker);
        requestProcessor.setFailOnCacheError(DEFAULT_FAIL_ON_CACHE_ERROR);

        notification = createDefaultNotification();
        startForeground(notification);

        Ln.d("SpiceService instance created.");
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    /**
     * Factory method to create an entity responsible for processing requests
     * send to the SpiceService. The default implementation of this method will
     * return a {@link RequestProcessor}. Override this method if you want to
     * inject a custom request processor. This feature has been implemented
     * following a request from Christopher Jenkins.
     * @param executorService
     *            a service executor that can be used to multi-thread request
     *            processing.
     * @param networkStateChecker
     *            an entity that will check network state.
     * @return a {@link RequestProcessor} that will be used to process requests.
     */
    protected RequestProcessor createRequestProcessor(ExecutorService executorService, NetworkStateChecker networkStateChecker) {
        return new RequestProcessor(getApplicationContext(), cacheManager, executorService, requestProcessorListener,
            networkStateChecker, progressReporter);
    }

    /**
     * Method to create a Request Progress Reporter object which is responsible
     * for informing the listeners of the current state of each request.
     * You can use this method to modify the existing behavior
     * @return {@link RequestProgressReporter}
     */
    protected RequestProgressReporter createRequestProgressReporter() {
        return new DefaultRequestProgressReporter();
    }

    /**
     * Factory method to create an entity responsible to check for network
     * state. The default implementation of this method will return a
     * {@link DefaultNetworkStateChecker}. Override this method if you want to
     * inject a custom network state for testing or to adapt to connectivity
     * changes on the Android. This method is also useful to create non-network
     * related requests. In that case create a {@link NetworkStateChecker} that
     * always return true. This feature has been implemented following a request
     * from Pierre Durand.
     * @return a {@link NetworkStateChecker} that will be used to determine if
     *         network state allows requests executions.
     */
    protected NetworkStateChecker getNetworkStateChecker() {
        return new DefaultNetworkStateChecker();
    }

    /**
     * Factory method to create an {@link ExecutorService} that will be used to
     * execute requests. The default implementation of this method will create a
     * single threaded or multi-threaded {@link ExecutorService} depending on
     * the number of threads returned by {@link #getThreadCount()} and will
     * assign them the priority returned by {@link #getThreadPriority()}.
     * <p/>
     * If you override this method in your service, you can supply a custom
     * {@link ExecutorService}.
     * <p/>
     * This feature has been implemented following a request from Riccardo
     * Ciovati.
     * @return the {@link ExecutorService} to be used to execute requests .
     */
    protected ExecutorService getExecutorService() {
        final int threadCount = getThreadCount();
        final int threadPriority = getThreadPriority();
        if (threadCount <= 0) {
            throw new IllegalArgumentException("Thread count must be >= 1");
        } else {
            return PriorityThreadPoolExecutor.getPriorityExecutor(threadCount, threadPriority);
        }
    }

    /**
     * Creates the Observer Manager. This method is only called if the RequestReporter implements {@linkRequestReporterWithObserverSupport)
     * @return ({@link ObserverManager)
     */
    protected ObserverManager createObserverManager() {
        return new ObserverManager();
    }

    /**
     * This method can be overrided in order to create a foreground
     * SpiceService. By default, it will create a notification that can't be
     * used to set a spiceService to foreground. It can work on some versions of
     * Android but it should be overriden for more safety.
     * @return a notification used to tell user that the SpiceService is still
     *         running and processing requests.
     */
    public Notification createDefaultNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(0);
        builder.setTicker(null);
        builder.setWhen(System.currentTimeMillis());
        final Notification note = builder.getNotification();
        note.flags |= Notification.FLAG_NO_CLEAR;
        return note;
    }

    @Override
    public void onDestroy() {
        Ln.d("SpiceService instance destroyed.");
        if (observerManager != null) {
            observerManager.stop();
        }
        super.onDestroy();
    }

    // ----------------------------------
    // DELEGATE METHODS (delegation is used to ease tests)
    // ----------------------------------

    public abstract CacheManager createCacheManager(Application application) throws CacheCreationException;

    /**
     * Override this method to increase the number of threads used to process
     * requests. This method will have no effect if you override
     * {@link #getExecutorService()}.
     * @return the number of threads used to process requests. Defaults to
     *         {@link #DEFAULT_THREAD_COUNT}.
     */
    public int getThreadCount() {
        return DEFAULT_THREAD_COUNT;
    }

    /**
     * Override this method to change the priority of threads requests. This
     * method will have no effect if you override {@link #getExecutorService()}.
     * @return the number of threads used to process requests.Defaults to
     *         {@link #DEFAULT_THREAD_PRIORITY}.
     */
    public int getThreadPriority() {
        return DEFAULT_THREAD_PRIORITY;
    }

    /**
     * Registers an Observer Factory so that it will be informed of new requests and can observe them if necessary
     * @param observerFactory
     * @throws ObserversNotSupportedException
     */
    public void registerObserver(final RequestObserverFactory observerFactory) throws ObserversNotSupportedException {
        if (!(progressReporter instanceof RequestProgressReporterWithObserverSupport)) {
            throw new ObserversNotSupportedException();
        } else {
            ((RequestProgressReporterWithObserverSupport) progressReporter).registerObserver(observerFactory);
        }
    }
    
    /**
     * Enables Request Tracking so that getActiveRequests() can be called.
     * This will only work if you have used a request progress reporter which suppports (@link RequestProgressReporterWithObserverSupport)
     * @throws ObserversNotSupportedException
     */
    public void enableRequestTracking() throws ObserversNotSupportedException {
        if (requestTracker == null) {
            registerObserver(RequestTrackerService.getRequestTrackerFactory());

            // exception may have been thrown if observers not supported
            requestTracker = RequestTrackerService.getRequestTracker();
        }
    }

    /**
     * 
     * @return active requests if request tracking is enabled
     * @throws IllegalStateException otherwise
     */
    public Map<CachedSpiceRequest<?>, RequestStatus> getActiveRequests() {
        if (requestTracker == null) {
            throw new IllegalStateException("Request Tracker not enabled, please call enableRequestTracking");
        }
        return requestTracker.getActiveRequests();
    }

    public void addRequest(final CachedSpiceRequest<?> request, final Set<RequestListener<?>> listRequestListener) {
        currentPendingRequestCount++;
        requestProcessor.addRequest(request, listRequestListener);
    }

    public boolean removeDataFromCache(final Class<?> clazz, final Object cacheKey) {
        return requestProcessor.removeDataFromCache(clazz, cacheKey);
    }

    public void removeAllDataFromCache(final Class<?> clazz) {
        requestProcessor.removeAllDataFromCache(clazz);
    }

    public <T> List<Object> getAllCacheKeys(final Class<T> clazz) {
        return cacheManager.getAllCacheKeys(clazz);
    }

    public <T> List<T> loadAllDataFromCache(final Class<T> clazz) throws CacheLoadingException, CacheCreationException {
        return cacheManager.loadAllDataFromCache(clazz);
    }

    public <T> T getDataFromCache(final Class<T> clazz, final Object cacheKey) throws CacheLoadingException, CacheCreationException {
        return cacheManager.loadDataFromCache(clazz, cacheKey, DurationInMillis.ALWAYS_RETURNED);
    }

    public <T> T putDataInCache(final Object cacheKey, T data) throws CacheSavingException, CacheCreationException {
        return cacheManager.saveDataToCacheAndReturnData(data, cacheKey);
    }

    public boolean isDataInCache(Class<?> clazz, Object cacheKey, long cacheExpiryDuration) throws CacheCreationException {
        return cacheManager.isDataInCache(clazz, cacheKey, cacheExpiryDuration);
    }

    public Date getDateOfDataInCache(Class<?> clazz, Object cacheKey) throws CacheLoadingException, CacheCreationException {
        return cacheManager.getDateOfDataInCache(clazz, cacheKey);
    }

    public void removeAllDataFromCache() {
        requestProcessor.removeAllDataFromCache();
    }

    public boolean isFailOnCacheError() {
        return requestProcessor.isFailOnCacheError();
    }

    public void setFailOnCacheError(final boolean failOnCacheError) {
        requestProcessor.setFailOnCacheError(failOnCacheError);
    }

    public void dontNotifyRequestListenersForRequest(final CachedSpiceRequest<?> request, final Collection<RequestListener<?>> listRequestListener) {
        requestProcessor.dontNotifyRequestListenersForRequest(request, listRequestListener);
    }

    // ----------------------------------
    // SERVICE METHODS
    // ----------------------------------

    @Override
    public IBinder onBind(final Intent intent) {
        isBound = true;
        return mSpiceServiceBinder;
    }

    @Override
    public boolean onUnbind(final Intent intent) {
        final boolean result = super.onUnbind(intent);
        isBound = false;
        stopIfNotBoundAndHasNoPendingRequests();
        return result;
    }

    private final class SelfStopperRequestProcessorListener implements RequestProcessorListener {
        @Override
        public void allRequestComplete() {
            currentPendingRequestCount = 0;
            stopIfNotBoundAndHasNoPendingRequests();
        }
    }

    public static class SpiceServiceBinder extends Binder {
        private final SpiceService spiceService;

        public SpiceServiceBinder(final SpiceService spiceService) {
            this.spiceService = spiceService;
        }

        public SpiceService getSpiceService() {
            return spiceService;
        }
    }

    public void dumpState() {
        Ln.v(requestProcessor.toString());
    }

    public void addSpiceServiceListener(final SpiceServiceServiceListener spiceServiceServiceListener) {
        requestProcessor.addSpiceServiceListener(spiceServiceServiceListener);
    }

    public void removeSpiceServiceListener(final SpiceServiceServiceListener spiceServiceServiceListener) {
        requestProcessor.removeSpiceServiceListener(spiceServiceServiceListener);
    }

    private void stopIfNotBoundAndHasNoPendingRequests() {
        Ln.v("Pending requests : " + currentPendingRequestCount);
        if (currentPendingRequestCount == 0 && !isBound) {
            stopSelf();
        }
    }

    // There is a bug in ServiceTestCase : a call to setForeground will fail
    // http://code.google.com/p/android/issues/detail?id=12122
    private void startForeground(final Notification notification) {
        try {
            final Method setForegroundMethod = Service.class.getMethod("startForeground", int.class, Notification.class);
            setForegroundMethod.invoke(this, NOTIFICATION_ID, notification);
        } catch (final SecurityException e) {
            Ln.e(e, "Unable to start a service in foreground");
        } catch (final NoSuchMethodException e) {
            Ln.e(e, "Unable to start a service in foreground");
        } catch (final IllegalArgumentException e) {
            Ln.e(e, "Unable to start a service in foreground");
        } catch (final IllegalAccessException e) {
            Ln.e(e, "Unable to start a service in foreground");
        } catch (final InvocationTargetException e) {
            Ln.e(e, "Unable to start a service in foreground");
        }
    }
}
