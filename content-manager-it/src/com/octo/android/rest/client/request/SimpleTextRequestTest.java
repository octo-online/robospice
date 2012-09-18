package com.octo.android.rest.client.request;

import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.LargeTest;

import com.octo.android.rest.client.request.simple.SimpleTextRequest;

@LargeTest
public class SimpleTextRequestTest extends InstrumentationTestCase {

	private SimpleTextRequest loremIpsumTextRequest;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		loremIpsumTextRequest = new SimpleTextRequest("http://www.loremipsum.de/downloads/original.txt");
	}

	public void test_loadDataFromNetwork() throws Exception {
		String stringReturned = loremIpsumTextRequest.loadDataFromNetwork();
		assertTrue(stringReturned.startsWith("Lorem ipsum"));
	}

}
