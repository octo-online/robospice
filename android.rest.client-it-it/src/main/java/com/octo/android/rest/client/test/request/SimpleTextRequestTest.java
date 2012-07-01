package com.octo.android.rest.client.test.request;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.SmallTest;

import com.octo.android.rest.client.sample.HelloAndroidActivity;

public class SimpleTextRequestTest extends ActivityInstrumentationTestCase2<HelloAndroidActivity> {

	private HelloAndroidActivity.CnilRequest cnilRequest;

	public SimpleTextRequestTest() {
		super("com.octo.android.rest.client.sample", HelloAndroidActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cnilRequest = getActivity().cnilRequest;
	}

	@SmallTest
	public void test_saveDataAndReturnData() throws FileNotFoundException, IOException {
		final String FILE_NAME = "toto";
		String stringReturned = cnilRequest.saveDataToCacheAndReturnData("coucou",FILE_NAME);
		assertEquals("coucou", stringReturned);
	}

	@SmallTest
	public void test_loadDataFromCache() throws FileNotFoundException, IOException {
		final String FILE_NAME = "toto";
		cnilRequest.saveDataToCacheAndReturnData("coucou",FILE_NAME);
		String stringReturned = cnilRequest.loadDataFromCache(FILE_NAME);
		assertEquals("coucou", stringReturned);
	}
	
	@LargeTest
	public void test_loadDataFromNetwork() throws FileNotFoundException, IOException {
		String stringReturned = cnilRequest.loadDataFromNetwork();
		assertTrue(stringReturned.startsWith("Lorem ipsum"));
	}


}
