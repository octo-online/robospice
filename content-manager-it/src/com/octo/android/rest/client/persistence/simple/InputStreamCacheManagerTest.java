package com.octo.android.rest.client.persistence.simple;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

import android.test.ActivityInstrumentationTestCase2;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.octo.android.rest.client.sample.TestActivity;

public class InputStreamCacheManagerTest extends ActivityInstrumentationTestCase2<TestActivity> {

	private static final String TEST_CACHE_KEY = "TEST_CACHE_KEY";

	private InputStreamCacheManager inputStreamCacheManager;

	public InputStreamCacheManagerTest() {
		super("com.octo.android.Rest.client", TestActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		inputStreamCacheManager = new InputStreamCacheManager(getActivity().getApplication());
	}

	public void testSaveDataToCacheAndReturnData() throws Exception {
		inputStreamCacheManager.saveDataToCacheAndReturnData(new ByteArrayInputStream("coucou".getBytes()), TEST_CACHE_KEY);

		File cachedFile = inputStreamCacheManager.getCacheFile(TEST_CACHE_KEY);
		assertTrue(cachedFile.exists());

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ByteStreams.copy(new FileInputStream(cachedFile), bos);
		assertTrue(Arrays.equals("coucou".getBytes(), bos.toByteArray()));
	}

	public void testLoadDataFromCache() throws Exception {
		File cachedFile = inputStreamCacheManager.getCacheFile(TEST_CACHE_KEY);
		Files.write("coucou".getBytes(), cachedFile);

		byte[] actual = Files.toByteArray(cachedFile);
		assertTrue(Arrays.equals("coucou".getBytes(), actual));
	}

	@Override
	protected void tearDown() throws Exception {
		getActivity().finish();
		inputStreamCacheManager.removeAllDataFromCache();
		super.tearDown();
	}

}
