package com.octo.android.rest.client.stub;

public final class ContentRequestFailingStub< T > extends ContentRequestStub< T > {

    public ContentRequestFailingStub( Class< T > clazz ) {
        super( clazz );
    }

    @Override
    public T loadDataFromNetwork() throws Exception {
        isLoadDataFromNetworkCalled = true;
        throw new Exception();
    }

}