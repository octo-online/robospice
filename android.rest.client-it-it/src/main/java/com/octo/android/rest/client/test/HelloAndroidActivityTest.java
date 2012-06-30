package com.octo.android.rest.client.test;

import android.test.ActivityInstrumentationTestCase2;
import com.octo.android.rest.client.sample.HelloAndroidActivity;

public class HelloAndroidActivityTest extends ActivityInstrumentationTestCase2<HelloAndroidActivity> {

    public HelloAndroidActivityTest() {
        super("com.octo.android.rest.client.sample", HelloAndroidActivity.class);
    }

    public void testActivity() {
        HelloAndroidActivity activity = getActivity();
        assertNotNull(activity);
    }
}

