package com.octo.android.rest.client.request;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.easymock.EasyMock;

import android.os.Looper;
import android.test.InstrumentationTestCase;

import com.octo.android.rest.client.exception.CacheLoadingException;
import com.octo.android.rest.client.exception.CacheSavingException;
import com.octo.android.rest.client.exception.ContentManagerException;
import com.octo.android.rest.client.persistence.DurationInMillis;
import com.octo.android.rest.client.persistence.ICacheManager;

public class RequestProcessorTest extends InstrumentationTestCase {

    private final static Class< String > TEST_CLASS = String.class;
    private final static String TEST_CACHE_KEY = "12345";
    private final static long TEST_DURATION = DurationInMillis.ONE_SECOND;
    private final static String TEST_RETURNED_DATA = "coucou";

    private ICacheManager mockCacheManager;
    private RequestProcessor requestProcessorUnderTest;

    private ReentrantLock lock;
    private Condition requestFinishedCondition;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mockCacheManager = EasyMock.createMock( ICacheManager.class );
        requestProcessorUnderTest = new RequestProcessor( getInstrumentation().getTargetContext(), mockCacheManager );
        lock = new ReentrantLock();
        requestFinishedCondition = lock.newCondition();
    }

    // ============================================================================================
    // TESTING WITH FAIL ON ERROR = false
    // ============================================================================================

    public void testAddRequest_when_something_is_found_in_cache() throws CacheLoadingException, CacheSavingException, InterruptedException {
        // given
        CachedContentRequestStub< String > stubRequest = createSuccessfulRequest( TEST_CLASS, TEST_CACHE_KEY, TEST_DURATION, TEST_RETURNED_DATA );

        RequestListenerStub< String > mockRequestListener = new RequestListenerStub< String >();
        Set< RequestListener< ? > > requestListenerSet = new HashSet< RequestListener< ? > >();
        requestListenerSet.add( mockRequestListener );

        EasyMock.expect( mockCacheManager.loadDataFromCache( EasyMock.eq( TEST_CLASS ), EasyMock.eq( TEST_CACHE_KEY ), EasyMock.eq( TEST_DURATION ) ) )
                .andReturn( TEST_RETURNED_DATA );
        EasyMock.replay( mockCacheManager );

        // when
        requestProcessorUnderTest.addRequest( stubRequest, requestListenerSet );

        lock.lock();
        try {
            requestFinishedCondition.await( 500, TimeUnit.MILLISECONDS );
        } finally {
            lock.unlock();
        }

        // then
        EasyMock.verify( mockCacheManager );
        assertFalse( stubRequest.isLoadDataFromNetworkCalled() );
        assertTrue( mockRequestListener.isExecutedInUIThread() );
        assertTrue( mockRequestListener.isSuccessful() );
    }

    public void testAddRequest_when_nothing_is_found_in_cache_and_request_succeeds() throws CacheLoadingException, CacheSavingException, InterruptedException {
        // given
        CachedContentRequestStub< String > stubRequest = createSuccessfulRequest( TEST_CLASS, TEST_CACHE_KEY, TEST_DURATION, TEST_RETURNED_DATA );

        RequestListenerStub< String > mockRequestListener = new RequestListenerStub< String >();
        Set< RequestListener< ? > > requestListenerSet = new HashSet< RequestListener< ? > >();
        requestListenerSet.add( mockRequestListener );

        EasyMock.expect( mockCacheManager.loadDataFromCache( EasyMock.eq( TEST_CLASS ), EasyMock.eq( TEST_CACHE_KEY ), EasyMock.eq( TEST_DURATION ) ) )
                .andReturn( null );
        EasyMock.expect( mockCacheManager.saveDataToCacheAndReturnData( EasyMock.eq( TEST_RETURNED_DATA ), EasyMock.eq( TEST_CACHE_KEY ) ) ).andReturn(
                TEST_RETURNED_DATA );
        EasyMock.replay( mockCacheManager );

        // when
        requestProcessorUnderTest.addRequest( stubRequest, requestListenerSet );

        lock.lock();
        try {
            requestFinishedCondition.await( 500, TimeUnit.MILLISECONDS );
        } finally {
            lock.unlock();
        }

        // then
        EasyMock.verify( mockCacheManager );
        assertTrue( stubRequest.isLoadDataFromNetworkCalled() );
        assertTrue( mockRequestListener.isExecutedInUIThread() );
        assertTrue( mockRequestListener.isSuccessful() );
    }

    public void testAddRequest_when_nothing_is_found_in_cache_and_request_fails() throws CacheLoadingException, CacheSavingException, InterruptedException {
        // given
        CachedContentRequestStub< String > stubRequest = createRequest( TEST_CLASS, TEST_CACHE_KEY, TEST_DURATION );

        RequestListenerStub< String > mockRequestListener = new RequestListenerStub< String >();
        Set< RequestListener< ? > > requestListenerSet = new HashSet< RequestListener< ? > >();
        requestListenerSet.add( mockRequestListener );

        EasyMock.expect( mockCacheManager.loadDataFromCache( EasyMock.eq( TEST_CLASS ), EasyMock.eq( TEST_CACHE_KEY ), EasyMock.eq( TEST_DURATION ) ) )
                .andReturn( null );
        EasyMock.replay( mockCacheManager );

        // when
        requestProcessorUnderTest.addRequest( stubRequest, requestListenerSet );

        lock.lock();
        try {
            requestFinishedCondition.await( 500, TimeUnit.MILLISECONDS );
        } finally {
            lock.unlock();
        }

        // then
        EasyMock.verify( mockCacheManager );
        assertTrue( stubRequest.isLoadDataFromNetworkCalled() );
        assertTrue( mockRequestListener.isExecutedInUIThread() );
        assertFalse( mockRequestListener.isSuccessful() );
    }

    public void testAddRequest_when_saving_to_cache_throws_exception() throws CacheLoadingException, CacheSavingException, InterruptedException {
        // given
        CachedContentRequestStub< String > stubRequest = createSuccessfulRequest( TEST_CLASS, TEST_CACHE_KEY, TEST_DURATION, TEST_RETURNED_DATA );

        RequestListenerStub< String > mockRequestListener = new RequestListenerStub< String >();
        Set< RequestListener< ? > > requestListenerSet = new HashSet< RequestListener< ? > >();
        requestListenerSet.add( mockRequestListener );

        EasyMock.expect( mockCacheManager.loadDataFromCache( EasyMock.eq( TEST_CLASS ), EasyMock.eq( TEST_CACHE_KEY ), EasyMock.eq( TEST_DURATION ) ) )
                .andReturn( null );
        EasyMock.expect( mockCacheManager.saveDataToCacheAndReturnData( EasyMock.eq( TEST_RETURNED_DATA ), EasyMock.eq( TEST_CACHE_KEY ) ) ).andThrow(
                new CacheSavingException( "" ) );
        EasyMock.replay( mockCacheManager );

        // when
        requestProcessorUnderTest.addRequest( stubRequest, requestListenerSet );

        lock.lock();
        try {
            requestFinishedCondition.await( 500, TimeUnit.MILLISECONDS );
        } finally {
            lock.unlock();
        }

        // then
        EasyMock.verify( mockCacheManager );
        assertTrue( stubRequest.isLoadDataFromNetworkCalled() );
        assertTrue( mockRequestListener.isExecutedInUIThread() );
        assertTrue( mockRequestListener.isSuccessful() );
    }

    // ============================================================================================
    // TESTING WITH FAIL ON ERROR = true
    // ============================================================================================

    public void testAddRequest_fail_on_error_true_when_nothing_is_found_in_cache() throws CacheLoadingException, CacheSavingException, InterruptedException {
        // given
        CachedContentRequestStub< String > stubRequest = createSuccessfulRequest( TEST_CLASS, TEST_CACHE_KEY, TEST_DURATION, TEST_RETURNED_DATA );

        RequestListenerStub< String > mockRequestListener = new RequestListenerStub< String >();
        Set< RequestListener< ? > > requestListenerSet = new HashSet< RequestListener< ? > >();
        requestListenerSet.add( mockRequestListener );

        EasyMock.expect( mockCacheManager.loadDataFromCache( EasyMock.eq( TEST_CLASS ), EasyMock.eq( TEST_CACHE_KEY ), EasyMock.eq( TEST_DURATION ) ) )
                .andReturn( null );
        EasyMock.expect( mockCacheManager.saveDataToCacheAndReturnData( EasyMock.eq( TEST_RETURNED_DATA ), EasyMock.eq( TEST_CACHE_KEY ) ) ).andReturn(
                TEST_RETURNED_DATA );
        EasyMock.replay( mockCacheManager );

        // when
        requestProcessorUnderTest.setFailOnCacheError( true );
        requestProcessorUnderTest.addRequest( stubRequest, requestListenerSet );

        lock.lock();
        try {
            requestFinishedCondition.await( 500, TimeUnit.MILLISECONDS );
        } finally {
            lock.unlock();
        }

        // then
        EasyMock.verify( mockCacheManager );
        assertTrue( stubRequest.isLoadDataFromNetworkCalled() );
        assertTrue( mockRequestListener.isExecutedInUIThread() );
        assertTrue( mockRequestListener.isSuccessful() );
    }

    public void testAddRequest_when_fail_on_error_true_loading_from_cache_throws_exception() throws CacheLoadingException, CacheSavingException,
            InterruptedException {
        // given
        CachedContentRequestStub< String > stubRequest = createSuccessfulRequest( TEST_CLASS, TEST_CACHE_KEY, TEST_DURATION, TEST_RETURNED_DATA );

        RequestListenerStub< String > mockRequestListener = new RequestListenerStub< String >();
        Set< RequestListener< ? > > requestListenerSet = new HashSet< RequestListener< ? > >();
        requestListenerSet.add( mockRequestListener );

        EasyMock.expect( mockCacheManager.loadDataFromCache( EasyMock.eq( TEST_CLASS ), EasyMock.eq( TEST_CACHE_KEY ), EasyMock.eq( TEST_DURATION ) ) )
                .andThrow( new CacheLoadingException( "" ) );
        EasyMock.replay( mockCacheManager );

        // when
        requestProcessorUnderTest.setFailOnCacheError( true );
        requestProcessorUnderTest.addRequest( stubRequest, requestListenerSet );

        lock.lock();
        try {
            requestFinishedCondition.await( 500, TimeUnit.MILLISECONDS );
        } finally {
            lock.unlock();
        }

        // then
        EasyMock.verify( mockCacheManager );
        assertFalse( stubRequest.isLoadDataFromNetworkCalled() );
        assertFalse( mockRequestListener.isSuccessful() );
    }

    public void testAddRequest_when_fail_on_error_true_saving_to_cache_throws_exception() throws CacheLoadingException, CacheSavingException,
            InterruptedException {
        // given
        CachedContentRequestStub< String > stubRequest = createSuccessfulRequest( TEST_CLASS, TEST_CACHE_KEY, TEST_DURATION, TEST_RETURNED_DATA );

        RequestListenerStub< String > mockRequestListener = new RequestListenerStub< String >();
        Set< RequestListener< ? > > requestListenerSet = new HashSet< RequestListener< ? > >();
        requestListenerSet.add( mockRequestListener );

        EasyMock.expect( mockCacheManager.loadDataFromCache( EasyMock.eq( TEST_CLASS ), EasyMock.eq( TEST_CACHE_KEY ), EasyMock.eq( TEST_DURATION ) ) )
                .andReturn( null );
        EasyMock.expect( mockCacheManager.saveDataToCacheAndReturnData( EasyMock.eq( TEST_RETURNED_DATA ), EasyMock.eq( TEST_CACHE_KEY ) ) ).andThrow(
                new CacheSavingException( "" ) );
        EasyMock.replay( mockCacheManager );

        // when
        requestProcessorUnderTest.setFailOnCacheError( true );
        requestProcessorUnderTest.addRequest( stubRequest, requestListenerSet );

        lock.lock();
        try {
            requestFinishedCondition.await( 500, TimeUnit.MILLISECONDS );
        } finally {
            lock.unlock();
        }

        // then
        EasyMock.verify( mockCacheManager );
        assertTrue( stubRequest.isLoadDataFromNetworkCalled() );
        assertTrue( mockRequestListener.isExecutedInUIThread() );
        assertFalse( mockRequestListener.isSuccessful() );
    }

    // ============================================================================================
    // TESTING CACHE MANAGER DEPENDENCY
    // ============================================================================================

    public void testRemoveAllDataFromCache() {
        // given
        mockCacheManager.removeAllDataFromCache();
        EasyMock.replay( mockCacheManager );

        // when
        requestProcessorUnderTest.removeAllDataFromCache();

        // then
        EasyMock.verify( mockCacheManager );
    }

    public void testRemoveAllDataFromCache_for_given_class() {
        // given
        final Class< ? > TEST_CLASS = String.class;
        mockCacheManager.removeAllDataFromCache( TEST_CLASS );
        EasyMock.replay( mockCacheManager );

        // when
        requestProcessorUnderTest.removeAllDataFromCache( TEST_CLASS );

        // then
        EasyMock.verify( mockCacheManager );
    }

    public void testRemoveAllDataFromCache_for_given_class_and_cachekey() {
        // given
        final Class< ? > TEST_CLASS = String.class;
        final String TEST_CACHE_KEY = "12345";
        EasyMock.expect( mockCacheManager.removeDataFromCache( TEST_CLASS, TEST_CACHE_KEY ) ).andReturn( true );
        EasyMock.replay( mockCacheManager );

        // when
        requestProcessorUnderTest.removeDataFromCache( TEST_CLASS, TEST_CACHE_KEY );

        // then
        EasyMock.verify( mockCacheManager );
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    // ============================================================================================
    // PRIVATE METHODS
    // ============================================================================================

    private < T > CachedContentRequestStub< T > createSuccessfulRequest( Class< T > clazz, String cacheKey, long maxTimeInCache, T returnedData ) {
        ContentRequestStub< T > stubContentRequest = new ContentRequestSucceedingStub< T >( clazz, returnedData );
        return new CachedContentRequestStub< T >( stubContentRequest, cacheKey, maxTimeInCache );
    }

    private < T > CachedContentRequestStub< T > createRequest( Class< T > clazz, String cacheKey, long maxTimeInCache ) {
        ContentRequestStub< T > stubContentRequest = new ContentRequestFailingStub< T >( clazz );
        return new CachedContentRequestStub< T >( stubContentRequest, cacheKey, maxTimeInCache );
    }

    // ============================================================================================
    // INNER CLASS
    // ============================================================================================
    private class CachedContentRequestStub< T > extends CachedContentRequest< T > {

        public CachedContentRequestStub( ContentRequestStub< T > contentRequest, String requestCacheKey, long cacheDuration ) {
            super( contentRequest, requestCacheKey, cacheDuration );
        }

        @SuppressWarnings("rawtypes")
        public boolean isLoadDataFromNetworkCalled() {
            return ( (ContentRequestStub) getContentRequest() ).isLoadDataFromNetworkCalled();
        }
    }

    private abstract class ContentRequestStub< T > extends ContentRequest< T > {
        protected boolean isLoadDataFromNetworkCalled = false;

        public ContentRequestStub( Class< T > clazz ) {
            super( clazz );
        }

        public boolean isLoadDataFromNetworkCalled() {
            return isLoadDataFromNetworkCalled;
        }
    }

    private final class ContentRequestSucceedingStub< T > extends ContentRequestStub< T > {
        private T returnedData;

        private ContentRequestSucceedingStub( Class< T > clazz, T returnedData ) {
            super( clazz );
            this.returnedData = returnedData;
        }

        @Override
        public T loadDataFromNetwork() throws Exception {
            isLoadDataFromNetworkCalled = true;
            return returnedData;
        }
    }

    private final class ContentRequestFailingStub< T > extends ContentRequestStub< T > {

        private ContentRequestFailingStub( Class< T > clazz ) {
            super( clazz );
        }

        @Override
        public T loadDataFromNetwork() throws Exception {
            isLoadDataFromNetworkCalled = true;
            throw new Exception();
        }

    }

    private class RequestListenerStub< T > implements RequestListener< T > {

        private Boolean isSuccessful = null;
        private boolean isExecutedInUIThread = false;

        @Override
        public void onRequestFailure( ContentManagerException arg0 ) {
            lock.lock();
            try {
                checkIsExectuedInUIThread();
                isSuccessful = false;
                requestFinishedCondition.signal();
            } finally {
                lock.unlock();
            }
        }

        @Override
        public void onRequestSuccess( T arg0 ) {
            lock.lock();
            try {
                checkIsExectuedInUIThread();
                isSuccessful = true;
                requestFinishedCondition.signal();
            } finally {
                lock.unlock();
            }
        }

        protected void checkIsExectuedInUIThread() {
            if ( Looper.myLooper() != null && Looper.myLooper() == Looper.getMainLooper() ) {
                isExecutedInUIThread = true;
            }
        }

        public Boolean isSuccessful() {
            return isSuccessful;
        }

        public boolean isExecutedInUIThread() {
            return isExecutedInUIThread;
        }
    }
}
