package com.octo.android.rest.client.stub;

public final class ContentRequestSucceedingStub< T > extends ContentRequestStub< T > {
    private T returnedData;

    public ContentRequestSucceedingStub( Class< T > clazz, T returnedData ) {
        super( clazz );
        this.returnedData = returnedData;
    }

    @Override
    public T loadDataFromNetwork() throws Exception {
        isLoadDataFromNetworkCalled = true;
        return returnedData;
    }
}