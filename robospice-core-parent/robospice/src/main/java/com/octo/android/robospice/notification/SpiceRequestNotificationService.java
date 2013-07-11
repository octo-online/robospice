package com.octo.android.robospice.notification;

import java.util.concurrent.ExecutorService;

import com.octo.android.robospice.SpiceService;
import com.octo.android.robospice.networkstate.NetworkStateChecker;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.request.RequestProcessor;
import com.octo.android.robospice.request.RequestProcessorListener;

public abstract class SpiceRequestNotificationService extends SpiceService {

    @Override
    protected RequestProcessor createRequestProcessor(
            ExecutorService executorService,
            NetworkStateChecker networkStateChecker, CacheManager cacheManager,
            RequestProcessorListener requestProcessorListener) {

        return new NotificationRequestProcessor(getApplicationContext(),
                executorService, networkStateChecker, cacheManager,
                requestProcessorListener);
    }
}
