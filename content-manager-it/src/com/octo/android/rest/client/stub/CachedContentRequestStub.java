package com.octo.android.rest.client.stub;

import com.octo.android.rest.client.request.CachedContentRequest;


// ============================================================================================
// INNER CLASS
// ============================================================================================
public class CachedContentRequestStub< T > extends CachedContentRequest< T > {

    public CachedContentRequestStub( ContentRequestStub< T > contentRequest, String requestCacheKey, long cacheDuration ) {
        super( contentRequest, requestCacheKey, cacheDuration );
    }

    @SuppressWarnings("rawtypes")
    public boolean isLoadDataFromNetworkCalled() {
        return ( (ContentRequestStub) getContentRequest() ).isLoadDataFromNetworkCalled();
    }
}