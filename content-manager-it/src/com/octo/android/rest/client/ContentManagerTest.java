package com.octo.android.rest.client;

import android.test.InstrumentationTestCase;

import com.octo.android.rest.client.persistence.DurationInMillis;
import com.octo.android.rest.client.request.CachedContentRequest;
import com.octo.android.rest.client.request.ContentRequest;
import com.octo.android.rest.client.stub.ContentRequestFailingStub;
import com.octo.android.rest.client.stub.ContentRequestStub;
import com.octo.android.rest.client.stub.ContentRequestSucceedingStub;
import com.octo.android.rest.client.stub.RequestListenerStub;

public class ContentManagerTest extends InstrumentationTestCase {

    private final static Class< String > TEST_CLASS = String.class;
    private final static String TEST_CACHE_KEY = "12345";
    private final static long TEST_DURATION = DurationInMillis.ONE_SECOND;
    private final static String TEST_RETURNED_DATA = "coucou";
    private static final long REQUEST_COMPLETION_TIME_OUT = 500;
    private static final long CONTENT_MANAGER_WAIT_TIMEOUT = 500;

    private ContentManager contentManager;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        contentManager = new ContentManager( SpringAndroidContentService.class );
    }

    @Override
    protected void tearDown() throws Exception {
        if ( contentManager != null && contentManager.isStarted() ) {
            contentManager.shouldStopAndJoin( CONTENT_MANAGER_WAIT_TIMEOUT );
            contentManager = null;
        }
        super.tearDown();
    }

    public void test_start_shouldNotBeCalledWithoutContext() throws Exception {
        // given

        // when
        try {
            contentManager.start();
            // then
            fail();
        } catch ( Exception ex ) {
            // then
            assertTrue( true );
        }
    }

    public void test_executeContentRequest_shouldFailIfNotStarted() {
        // given

        // when
        try {
            contentManager.execute( new CachedContentRequest< String >( (ContentRequest< String >) null, null, DurationInMillis.ALWAYS ), null );
            // then
            fail();
        } catch ( Exception ex ) {
            // then
            assertTrue( true );
        }
    }

    public void test_executeContentRequest_shouldFailIfStartedFromContextWithNoService() {
        // given

        // when
        try {
            contentManager.start( getInstrumentation().getContext() );
            // then
            fail();
        } catch ( Exception ex ) {
            // then
            assertTrue( true );
        }
    }

    public void test_executeContentRequest_shouldFailIfStopped() throws InterruptedException {
        // given
        contentManager.start( getInstrumentation().getTargetContext() );
        contentManager.shouldStopAndJoin( CONTENT_MANAGER_WAIT_TIMEOUT );

        // when
        try {
            contentManager.execute( new CachedContentRequest< String >( (ContentRequest< String >) null, null, DurationInMillis.ALWAYS ), null );
            // then
            fail();
        } catch ( Exception ex ) {
            // then
            assertTrue( true );
        }
    }

    /*
     * public void test_executeContentRequest_based_on_asynctask() throws InterruptedException { // when
     * contentManager.start( getInstrumentation().getContext() ); AsyncTaskStub< Void, Void, String > asyncTaskStub =
     * new AsyncTaskStub< Void, Void, String >(); RequestListenerStub< String > requestListenerStub = new
     * RequestListenerStub< String >();
     * 
     * // when contentManager.execute( asyncTaskStub, TEST_CACHE_KEY, TEST_DURATION, requestListenerStub );
     * requestListenerStub.await( REQUEST_COMPLETION_TIME_OUT );
     * 
     * // test assertTrue( asyncTaskStub.isLoadDataFromNetworkCalled() ); assertTrue(
     * requestListenerStub.isExecutedInUIThread() ); assertTrue( requestListenerStub.isSuccessful() ); }
     */

    public void test_executeContentRequest_when_request_succeeds() throws InterruptedException {
        // when
        contentManager.start( getInstrumentation().getTargetContext() );
        ContentRequestStub< String > contentRequestStub = new ContentRequestSucceedingStub< String >( TEST_CLASS, TEST_RETURNED_DATA );
        RequestListenerStub< String > requestListenerStub = new RequestListenerStub< String >();

        // when
        contentManager.execute( contentRequestStub, TEST_CACHE_KEY, TEST_DURATION, requestListenerStub );
        requestListenerStub.await( REQUEST_COMPLETION_TIME_OUT );

        // test
        assertTrue( contentRequestStub.isLoadDataFromNetworkCalled() );
        assertTrue( requestListenerStub.isExecutedInUIThread() );
        assertTrue( requestListenerStub.isSuccessful() );
    }

    public void test_executeContentRequest_when_request_fails() throws InterruptedException {
        // when
        contentManager.start( getInstrumentation().getTargetContext() );
        ContentRequestStub< String > contentRequestStub = new ContentRequestFailingStub< String >( TEST_CLASS );
        RequestListenerStub< String > requestListenerStub = new RequestListenerStub< String >();

        // when
        contentManager.execute( contentRequestStub, TEST_CACHE_KEY, TEST_DURATION, requestListenerStub );
        requestListenerStub.await( REQUEST_COMPLETION_TIME_OUT );

        // test
        assertTrue( contentRequestStub.isLoadDataFromNetworkCalled() );
        assertTrue( requestListenerStub.isExecutedInUIThread() );
        assertFalse( requestListenerStub.isSuccessful() );
    }

    public void testCancel() {
        // given
        ContentRequestStub< String > contentRequestStub = new ContentRequestSucceedingStub< String >( String.class, TEST_RETURNED_DATA );
        contentManager.start( getInstrumentation().getTargetContext() );
        // when
        contentManager.cancel( contentRequestStub );

        // test
        assertTrue( contentRequestStub.isCancelled() );
    }

    public void testCancelAllRequests() {
        // given
        contentManager.start( getInstrumentation().getTargetContext() );
        ContentRequestStub< String > contentRequestStub = new ContentRequestFailingStub< String >( TEST_CLASS );
        ContentRequestStub< String > contentRequestStub2 = new ContentRequestFailingStub< String >( TEST_CLASS );
        RequestListenerStub< String > requestListenerStub = new RequestListenerStub< String >();
        RequestListenerStub< String > requestListenerStub2 = new RequestListenerStub< String >();

        // when
        contentManager.execute( contentRequestStub, TEST_CACHE_KEY, TEST_DURATION, requestListenerStub );
        contentManager.execute( contentRequestStub2, TEST_CACHE_KEY, TEST_DURATION, requestListenerStub2 );
        contentManager.cancelAllRequests();

        // test
        assertTrue( contentRequestStub.isCancelled() );
        assertTrue( contentRequestStub2.isCancelled() );
    }

    public void test_dontNotifyRequestListenersForRequest() throws InterruptedException {
        // given
        contentManager.start( getInstrumentation().getTargetContext() );
        ContentRequestStub< String > contentRequestStub = new ContentRequestFailingStub< String >( TEST_CLASS );
        ContentRequestStub< String > contentRequestStub2 = new ContentRequestFailingStub< String >( TEST_CLASS );
        RequestListenerStub< String > requestListenerStub = new RequestListenerStub< String >();
        RequestListenerStub< String > requestListenerStub2 = new RequestListenerStub< String >();

        // when
        contentManager.execute( contentRequestStub, TEST_CACHE_KEY, TEST_DURATION, requestListenerStub );
        // contentManager.dontNotifyRequestListenersForRequest( contentRequestStub );
        contentManager.dontNotifyAnyRequestListeners();
        contentManager.execute( contentRequestStub2, TEST_CACHE_KEY, TEST_DURATION, requestListenerStub2 );

        contentRequestStub.await( REQUEST_COMPLETION_TIME_OUT );
        contentRequestStub2.await( REQUEST_COMPLETION_TIME_OUT );

        // test
        assertTrue( contentRequestStub.isLoadDataFromNetworkCalled() );
        assertTrue( contentRequestStub2.isLoadDataFromNetworkCalled() );
        assertNull( requestListenerStub.isSuccessful() );
        assertFalse( requestListenerStub2.isSuccessful() );
    }

    public void test_dontNotifyAnyRequestListeners() throws InterruptedException {
        // given
        contentManager.start( getInstrumentation().getTargetContext() );
        ContentRequestStub< String > contentRequestStub = new ContentRequestFailingStub< String >( TEST_CLASS );
        ContentRequestStub< String > contentRequestStub2 = new ContentRequestFailingStub< String >( TEST_CLASS );
        RequestListenerStub< String > requestListenerStub = new RequestListenerStub< String >();
        RequestListenerStub< String > requestListenerStub2 = new RequestListenerStub< String >();

        // when
        contentManager.execute( contentRequestStub, TEST_CACHE_KEY, TEST_DURATION, requestListenerStub );
        contentManager.execute( contentRequestStub2, TEST_CACHE_KEY, TEST_DURATION, requestListenerStub2 );
        contentManager.dontNotifyAnyRequestListeners();

        contentRequestStub.await( REQUEST_COMPLETION_TIME_OUT );
        contentRequestStub2.await( REQUEST_COMPLETION_TIME_OUT );

        // test
        assertTrue( contentRequestStub.isLoadDataFromNetworkCalled() );
        assertTrue( contentRequestStub2.isLoadDataFromNetworkCalled() );
        assertNull( requestListenerStub.isSuccessful() );
        assertNull( requestListenerStub2.isSuccessful() );
    }

}
