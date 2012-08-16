package com.octo.android.rest.client.request;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;

import com.octo.android.rest.client.request.simple.SimpleTextRequest;
import com.octo.android.rest.client.sample.HelloAndroidActivity;

@LargeTest
public class SimpleTextRequestTest extends ActivityInstrumentationTestCase2< HelloAndroidActivity > {

    private SimpleTextRequest cnilRequest;

    public SimpleTextRequestTest() {
        super( "com.octo.android.rest.client.sample", HelloAndroidActivity.class );
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        cnilRequest = new SimpleTextRequest( "http://www.loremipsum.de/downloads/original.txt" );
    }

    public void test_loadDataFromNetwork() throws Exception {
        String stringReturned = cnilRequest.loadDataFromNetwork();
        assertTrue( stringReturned.startsWith( "Lorem ipsum" ) );
    }

}
