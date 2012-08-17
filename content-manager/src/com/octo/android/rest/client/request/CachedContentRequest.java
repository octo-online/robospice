package com.octo.android.rest.client.request;

import java.lang.reflect.ParameterizedType;

import android.annotation.TargetApi;
import android.os.AsyncTask;

public class CachedContentRequest< RESULT > extends ContentRequest< RESULT > {

    private String requestCacheKey;
    private long cacheDuration;
    private ContentRequest< RESULT > contentRequest;

    public CachedContentRequest( ContentRequest< RESULT > contentRequest, String requestCacheKey, long cacheDuration ) {
        super( contentRequest.getResultType() );
        this.requestCacheKey = requestCacheKey;
        this.cacheDuration = cacheDuration;
        this.contentRequest = contentRequest;
    }

    @TargetApi(3)
    @SuppressWarnings("unchecked")
    public < Params, Progress > CachedContentRequest( AsyncTask< Params, Progress, RESULT > asyncTask, String requestCacheKey, long cacheDuration,
            Params... params ) {
        super( (Class< RESULT >) ( (ParameterizedType) asyncTask.getClass().getGenericSuperclass() ).getActualTypeArguments()[ 2 ].getClass() );
        this.requestCacheKey = requestCacheKey;
        this.cacheDuration = cacheDuration;
        this.contentRequest = new AsyncTaskWrapper< Params, Progress >( asyncTask, params );
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
    public boolean isCancelled() {
        return contentRequest.isCancelled();
    }

    public String getRequestCacheKey() {
        return requestCacheKey;
    }

    public long getCacheDuration() {
        return cacheDuration;
    }

    public ContentRequest< RESULT > getContentRequest() {
        return contentRequest;
    }

    @Override
    public String toString() {
        return "CachedContentRequest [requestCacheKey=" + requestCacheKey + ", cacheDuration=" + cacheDuration + ", contentRequest=" + contentRequest + "]";
    }

    @TargetApi(3)
    private class AsyncTaskWrapper< Params, Progress > extends ContentRequest< RESULT > {

        private AsyncTask< Params, Progress, RESULT > asyncTask;
        private Params[] params;

        @SuppressWarnings("unchecked")
        public AsyncTaskWrapper( AsyncTask< Params, Progress, RESULT > asyncTask, Params... params ) {
            super( (Class< RESULT >) ( (ParameterizedType) asyncTask.getClass().getGenericSuperclass() ).getActualTypeArguments()[ 2 ].getClass() );
            this.asyncTask = asyncTask;
            this.params = params;
        }

        @Override
        public RESULT loadDataFromNetwork() throws Exception {
            asyncTask.execute( params );
            return asyncTask.get();
        }

        @Override
        public boolean isCancelled() {
            return asyncTask.isCancelled();
        }

    }

}