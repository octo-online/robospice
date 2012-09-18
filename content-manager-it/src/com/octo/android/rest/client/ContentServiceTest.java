package com.octo.android.rest.client;

import android.content.Intent;
import android.os.IBinder;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.octo.android.rest.client.sample.SampleContentService;

//Thanks to http://stackoverflow.com/questions/2300029/servicetestcaset-getservice
@SmallTest
public class ContentServiceTest extends ServiceTestCase<SampleContentService> {

	public ContentServiceTest() {
		super(SampleContentService.class);
	}

	public void testServiceNotNull() {
		Intent startIntent = new Intent();
		startIntent.setClass(getContext(), ContentService.class);
		startService(startIntent);
		assertNotNull(getService());
	}

	public void testBindable() {
		Intent startIntent = new Intent();
		startIntent.setClass(getContext(), ContentService.class);
		IBinder service = bindService(startIntent);
		assertNotNull(service);
	}

}
