package com.octo.android.robospice.request;

public interface RequestRunner
{
    void executeRequest(CachedSpiceRequest<?> request);

    boolean isFailOnCacheError();

    void setFailOnCacheError(boolean failOnCacheError);

    void shouldStop();
}
