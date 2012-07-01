package com.octo.android.rest.client.test;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.content.Intent;
import android.os.Handler;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.octo.android.rest.client.ContentService;
import com.octo.android.rest.client.ContentService.ContentServiceBinder;
import com.octo.android.rest.client.request.simple.AbstractTextRequest;

@SmallTest
public class ContentServiceTest extends ServiceTestCase<ContentService> {

	final Lock lock = new ReentrantLock();
	final Condition cnilRequestFinished  = lock.newCondition(); 

	public ContentServiceTest() {
		super(ContentService.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}


	public void testService() {
		ContentService service = getService();
		assertNotNull(service);
	}

	public void testServiceWithCnilRequest() {
		ContentServiceBinder binder = (ContentServiceBinder) bindService( new Intent(getContext(),ContentService.class) );
		ContentService service = binder.getContentService();
		final CnilRequest cnilRequest = new CnilRequest("http://www.loremipsum.de/downloads/original.txt");
		service.addRequest(cnilRequest, new Handler(), true);
		lock.lock();
		try {
			Date date = new Date();
			date.setMinutes(date.getMinutes()+1);
			cnilRequestFinished.awaitUntil(date);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("Failure during sleep");
		} finally {
			lock.unlock();
		}
		assertTrue(cnilRequest.getResult().startsWith("Lorem ipsum"));
	}

	public final class CnilRequest extends AbstractTextRequest {

		private String result;

		public CnilRequest(String url) {
			super(ContentServiceTest.this.getService(), url);
		}

		@Override
		protected void onRequestFailure(int resultCode) {
			lock.lock();
			cnilRequestFinished.signal();
			lock.unlock();
		}

		@Override
		protected void onRequestSuccess(final String result) {
			lock.lock();
			this.result = result;
			cnilRequestFinished.signal();
			lock.unlock();
		}

		public String getResult() {
			return this.result;
		}
	}


}

