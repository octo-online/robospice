package com.octo.android.rest.client.test.persistence;

import java.io.FileNotFoundException;
import java.io.IOException;

import roboguice.RoboGuice;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

import com.octo.android.rest.client.persistence.simple.StringPersistenceManager;
import com.octo.android.rest.client.sample.HelloAndroidActivity;

@SmallTest
public class StringPersistenceManagerTest extends ActivityInstrumentationTestCase2<HelloAndroidActivity> {

	private StringPersistenceManager stringPersistenceManager;

	public StringPersistenceManagerTest() {
		super("com.octo.android.rest.client.sample", HelloAndroidActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		stringPersistenceManager = new StringPersistenceManager(getActivity().getApplication());
	}

	public void test_canHandleStrings() {
		boolean canHandleStrings = stringPersistenceManager.canHandleData(String.class);
		assertEquals(true, canHandleStrings);
	}

	public void test_saveDataAndReturnData() throws FileNotFoundException, IOException {
		final String FILE_NAME = "toto";
		String stringReturned = stringPersistenceManager.saveDataToCacheAndReturnData("coucou",FILE_NAME);
		assertEquals("coucou", stringReturned);
	}

	public void test_loadDataFromCache() throws FileNotFoundException, IOException {
		final String FILE_NAME = "toto";
		stringPersistenceManager.saveDataToCacheAndReturnData("coucou",FILE_NAME);
		String stringReturned = stringPersistenceManager.loadDataFromCache(FILE_NAME);
		assertEquals("coucou", stringReturned);
	}


}
