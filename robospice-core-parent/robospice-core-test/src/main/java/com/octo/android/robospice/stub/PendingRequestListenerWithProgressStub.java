package com.octo.android.robospice.stub;

import com.octo.android.robospice.request.listener.PendingRequestListener;
import com.octo.android.robospice.request.CachedSpiceRequest;

public class PendingRequestListenerWithProgressStub<T> extends RequestListenerWithProgressStub<T> implements PendingRequestListener<T> {

    private boolean isRequestNotFound = false;

    private CachedSpiceRequest<?> attachedRequest;


    @Override
    public void onRequestNotFound() {
        isRequestNotFound = true;
    }

    public boolean isRequestNotFound() {
        return isRequestNotFound;
    }

    @Override
    public void onRequestAttached(CachedSpiceRequest<?> request) {
        attachedRequest = request;
    }

    public CachedSpiceRequest<?> getAttachedRequest() {
        return attachedRequest;
    }
}
