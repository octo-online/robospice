package com.octo.android.rest.client.request;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;

import com.octo.android.rest.client.request.simple.SimpleTextRequest;
import com.octo.android.rest.client.sample.TestActivity;

@LargeTest
public class SimpleTextRequestTest extends ActivityInstrumentationTestCase2< TestActivity > {

    private SimpleTextRequest loremIpsumTextRequest;

    public SimpleTextRequestTest() {
        super( "com.octo.android.rest.client.sample", TestActivity.class );
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        loremIpsumTextRequest = new SimpleTextRequest( "http://www.loremipsum.de/downloads/original.txt" );
    }

    public void test_loadDataFromNetwork() throws Exception {
        String stringReturned = loremIpsumTextRequest.loadDataFromNetwork();
        assertTrue( stringReturned.startsWith( "Lorem ipsum" ) );
    }

}
