package com.octo.android.rest.client;

import android.app.Application;
import android.content.Intent;
import android.os.IBinder;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.octo.android.rest.client.persistence.CacheManager;
import com.octo.android.rest.client.sample.service.ContentServiceUnderTest;

//Thanks to http://stackoverflow.com/questions/2300029/servicetestcaset-getservice
@SmallTest
public class ContentServiceTest extends ServiceTestCase< ContentServiceUnderTest > {

    public ContentServiceTest() {
        super( ContentServiceUnderTest.class );
    }

    public void testServiceNotNull() {
        Intent startIntent = new Intent();
        startIntent.setClass( getContext(), ContentServiceUnderTest.class );
        startService( startIntent );
        assertNotNull( getService() );
    }

    public void testBindable() {
        Intent startIntent = new Intent();
        startIntent.setClass( getContext(), ContentServiceUnderTest.class );
        IBinder service = bindService( startIntent );
        assertNotNull( service );
    }

    public void testCacheManagerCall() {
        CacheManagerStub cacheManager = new CacheManagerStub();
        ContentServiceUnderTest2 contentServiceUnderTest2 = new ContentServiceUnderTest2( cacheManager );
        contentServiceUnderTest2.removeAllDataFromCache();

        assertTrue( cacheManager.isRemoveAllDataFromCacheInvoked );
    }

    public class ContentServiceUnderTest2 extends ContentService {

        private CacheManager cacheManager = null;

        private ContentServiceUnderTest2( CacheManager cacheManager ) {
            super();
            this.cacheManager = cacheManager;
        }

        @Override
        public CacheManager createCacheManager( Application arg0 ) {
            return cacheManager;
        }

    }

    private class CacheManagerStub extends CacheManager {

        public boolean isRemoveAllDataFromCacheInvoked = false;

        @Override
        public void removeAllDataFromCache() {
            super.removeAllDataFromCache();
            isRemoveAllDataFromCacheInvoked = true;
        }
    }

}
