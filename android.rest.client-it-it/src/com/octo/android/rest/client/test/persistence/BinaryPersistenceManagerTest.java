package com.octo.android.rest.client.test.persistence;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import roboguice.RoboGuice;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

import com.google.common.io.ByteStreams;
import com.octo.android.rest.client.persistence.simple.BinaryPersistenceManager;
import com.octo.android.rest.client.sample.HelloAndroidActivity;

@SmallTest
public class BinaryPersistenceManagerTest extends ActivityInstrumentationTestCase2<HelloAndroidActivity> {

	private BinaryPersistenceManager binaryPersistenceManager;

	public BinaryPersistenceManagerTest() {
		super("com.octo.android.rest.client.sample", HelloAndroidActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		binaryPersistenceManager = new BinaryPersistenceManager(getActivity().getApplication());
	}

	public void test_canHandleInputStreams() {
		boolean canHandleStrings = binaryPersistenceManager.canHandleData(InputStream.class);
		assertEquals(true, canHandleStrings);
	}

	public void test_saveDataAndReturnData() throws FileNotFoundException, IOException {
		byte[] bytes = "coucou".getBytes();
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
		final String FILE_NAME = "toto";
		InputStream inputStreamReturned = binaryPersistenceManager.saveDataToCacheAndReturnData(byteArrayInputStream,FILE_NAME);
		bytes = ByteStreams.toByteArray(inputStreamReturned);
		assertEquals("coucou", new String(bytes));
	}

	public void test_loadDataFromCache() throws FileNotFoundException, IOException {
		final String FILE_NAME = "toto";
		byte[] bytes = "coucou".getBytes();
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
		binaryPersistenceManager.saveDataToCacheAndReturnData(byteArrayInputStream,FILE_NAME);
		InputStream inputStreamReturned = binaryPersistenceManager.loadDataFromCache(FILE_NAME);
		bytes = ByteStreams.toByteArray(inputStreamReturned);
		assertEquals("coucou", new String(bytes));
	}
}
