package com.octo.android.rest.client.test;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

@SmallTest
public class HelloAndroidTest extends ActivityInstrumentationTestCase2<HelloAndroidActivity> {

    public HelloAndroidTest() {
        super("com.octo.android.rest.client.sample", HelloAndroidActivity.class);
    }

    public void testActivity() {
        HelloAndroidActivity activity = getActivity();
        assertNotNull(activity);
    }
}

