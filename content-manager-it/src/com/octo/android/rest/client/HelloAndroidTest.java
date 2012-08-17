package com.octo.android.rest.client;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

import com.octo.android.rest.client.sample.HelloAndroidActivity;

@SmallTest
public class HelloAndroidTest extends ActivityInstrumentationTestCase2< HelloAndroidActivity > {

    public HelloAndroidTest() {
        super( "com.octo.android.rest.client.sample", HelloAndroidActivity.class );
    }

    public void testActivity() {
        HelloAndroidActivity activity = getActivity();
        assertNotNull( activity );
    }

    @Override
    protected void tearDown() throws Exception {
        getActivity().finish();
        super.tearDown();
    }
}
