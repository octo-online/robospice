package com.octo.android.rest.client.persistence;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

import com.octo.android.rest.client.persistence.simple.StringCacheManager;
import com.octo.android.rest.client.sample.TestActivity;

@SmallTest
public class StringCacheManagerTest extends ActivityInstrumentationTestCase2<TestActivity> {

	private StringCacheManager stringPersistenceManager;

	public StringCacheManagerTest() {
		super("com.octo.android.rest.client.sample", TestActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		stringPersistenceManager = new StringCacheManager(getActivity().getApplication());
	}

	public void test_canHandleStrings() {
		boolean canHandleStrings = stringPersistenceManager.canHandleClass(String.class);
		assertEquals(true, canHandleStrings);
	}

	public void test_saveDataAndReturnData() throws Exception {
		final String FILE_NAME = "toto";
		String stringReturned = stringPersistenceManager.saveDataToCacheAndReturnData("coucou", FILE_NAME);
		assertEquals("coucou", stringReturned);
	}

	public void test_loadDataFromCache() throws Exception {
		final String FILE_NAME = "toto";
		stringPersistenceManager.saveDataToCacheAndReturnData("coucou", FILE_NAME);
		String stringReturned = stringPersistenceManager.loadDataFromCache(FILE_NAME, 0);
		assertEquals("coucou", stringReturned);
	}

}
