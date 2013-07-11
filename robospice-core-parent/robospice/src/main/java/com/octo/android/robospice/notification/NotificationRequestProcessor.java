package com.octo.android.robospice.notification;

import java.util.Set;
import java.util.concurrent.ExecutorService;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import com.octo.android.robospice.networkstate.NetworkStateChecker;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.CachedSpiceRequest;
import com.octo.android.robospice.request.RequestProcessor;
import com.octo.android.robospice.request.RequestProcessorListener;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.listener.RequestProgress;
import com.octo.android.robospice.request.listener.RequestStatus;

public class NotificationRequestProcessor extends RequestProcessor {

    private NotificationManager nManager;
    private Context context;

    public NotificationRequestProcessor(Context context,
            ExecutorService executorService,
            NetworkStateChecker networkStateChecker, CacheManager cacheManager,
            RequestProcessorListener requestProcessorListener) {

        super(context, cacheManager, executorService, requestProcessorListener,
                networkStateChecker);

        this.context = context;

        nManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected <T> void notifyListenersOfRequestFailure(
            CachedSpiceRequest<T> request, SpiceException e) {

        if (request.getSpiceRequest() instanceof RequestNotification) {
            RequestNotification requestNotification = (RequestNotification) request
                    .getSpiceRequest();

            Notification notification = requestNotification
                    .getNotificationFactory().createNotificationForFailure(
                            context, e);

            nManager.notify(requestNotification.getNotificationId(),
                    notification);
        }

        super.notifyListenersOfRequestFailure(request, e);
    }

    @Override
    protected <T> void notifyListenersOfRequestSuccess(
            CachedSpiceRequest<T> request, T result) {

        if (request.getSpiceRequest() instanceof RequestNotification) {
            RequestNotification requestNotification = (RequestNotification) request
                    .getSpiceRequest();

            Notification notification = requestNotification
                    .getNotificationFactory().createNotificationForSuccess(
                            context, result);

            nManager.notify(requestNotification.getNotificationId(),
                    notification);
        }

        super.notifyListenersOfRequestSuccess(request, result);
    }

    @Override
    protected void notifyListenersOfRequestCancellation(
            CachedSpiceRequest<?> request, Set<RequestListener<?>> listeners) {

        if (request.getSpiceRequest() instanceof RequestNotification) {
            RequestNotification requestNotification = (RequestNotification) request
                    .getSpiceRequest();

            Notification notification = requestNotification
                    .getNotificationFactory()
                    .createNotificationForCancellation(context);

            nManager.notify(requestNotification.getNotificationId(),
                    notification);
        }

        super.notifyListenersOfRequestCancellation(request, listeners);
    }

    @Override
    protected <T> void notifyListenersOfRequestProgress(
            CachedSpiceRequest<?> request, Set<RequestListener<?>> listeners,
            RequestProgress progress) {

        // We don't want a progress noitification AFTER receiving a
        // Success/Failure/Cancellation
        if (request.getSpiceRequest() instanceof RequestNotification
                && progress.getStatus() != RequestStatus.COMPLETE) {

            RequestNotification requestNotification = (RequestNotification) request
                    .getSpiceRequest();

            Notification notification = requestNotification
                    .getNotificationFactory()
                    .createNotificationForProgressUpdate(context, progress);

            nManager.notify(requestNotification.getNotificationId(),
                    notification);
        }

        super.notifyListenersOfRequestProgress(request, listeners, progress);
    }
}
