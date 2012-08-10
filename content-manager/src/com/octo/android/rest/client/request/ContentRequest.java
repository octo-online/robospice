package com.octo.android.rest.client.request;

public abstract class ContentRequest< RESULT > {

    private Class< RESULT > resultType;
    private boolean isCanceled = false;

    public ContentRequest( Class< RESULT > clazz ) {
        this.resultType = clazz;
    }

    public abstract RESULT loadDataFromNetwork() throws Exception;

    public Class< RESULT > getResultType() {
        return resultType;
    }

    public void cancel() {
        this.isCanceled = true;
    }

    public boolean isCanceled() {
        return this.isCanceled;
    }

}