package com.octo.android.rest.client.request;

public class CachedContentRequest< RESULT > extends ContentRequest< RESULT > {

    private String requestCacheKey;
    private long cacheDuration;
    private ContentRequest< RESULT > contentRequest;

    public CachedContentRequest( ContentRequest< RESULT > contentRequest, String requestCacheKey, long cacheDuration ) {
        super( contentRequest.getResultType() );
        this.requestCacheKey = requestCacheKey;
        this.cacheDuration = cacheDuration;
    }

    @Override
    public RESULT loadDataFromNetwork() throws Exception {
        return contentRequest.loadDataFromNetwork();
    }

    @Override
    public Class< RESULT > getResultType() {
        return contentRequest.getResultType();
    }

    @Override
    public void cancel() {
        contentRequest.cancel();
    }

    @Override
    public boolean isCanceled() {
        return contentRequest.isCanceled();
    }

    public String getRequestCacheKey() {
        return requestCacheKey;
    }

    public long getCacheDuration() {
        return cacheDuration;
    }

}