package com.octo.android.rest.client.test.persistence;

import java.io.FileNotFoundException;
import java.io.IOException;

import roboguice.RoboGuice;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

import com.octo.android.rest.client.persistence.DataClassPersistenceManager;
import com.octo.android.rest.client.persistence.json.JSonPersistenceManageFactory;
import com.octo.android.rest.client.sample.HelloAndroidActivity;
import com.octo.android.rest.client.sample.model.ClientRequestStatus;

@SmallTest
public class CreditStatusPersistenceManagerTest extends ActivityInstrumentationTestCase2<HelloAndroidActivity> {
	private DataClassPersistenceManager<ClientRequestStatus> dataPersistenceManager;

	public CreditStatusPersistenceManagerTest() {
		super("com.octo.android.rest.client.sample", HelloAndroidActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		JSonPersistenceManageFactory factory = new JSonPersistenceManageFactory(getActivity().getApplication());
		dataPersistenceManager = factory.createDataPersistenceManager(ClientRequestStatus.class);
	}

	public void test_canHandleClientRequestStatus() {
		boolean canHandleClientRequestStatus = dataPersistenceManager.canHandleData(ClientRequestStatus.class);
		assertEquals(true, canHandleClientRequestStatus);
	}

	public void test_saveDataAndReturnData() throws FileNotFoundException, IOException {
		ClientRequestStatus clientRequestStatus = new ClientRequestStatus();
		clientRequestStatus.setDuration("2");
		final String FILE_NAME = "toto";
		ClientRequestStatus clientRequestStatusReturned = dataPersistenceManager.saveDataToCacheAndReturnData(clientRequestStatus,FILE_NAME);
		assertEquals("2", clientRequestStatusReturned.getDuration());
	}

	public void test_loadDataFromCache() throws FileNotFoundException, IOException {
		ClientRequestStatus clientRequestStatus = new ClientRequestStatus();
		clientRequestStatus.setDuration("2");
		final String FILE_NAME = "toto";
		dataPersistenceManager.saveDataToCacheAndReturnData(clientRequestStatus,FILE_NAME);
		ClientRequestStatus clientRequestStatusReturned = dataPersistenceManager.loadDataFromCache(FILE_NAME);
		assertEquals("2", clientRequestStatusReturned.getDuration());
	}
}
