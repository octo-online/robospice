package com.octo.android.rest.client.sample.service;

import android.app.Application;

import com.octo.android.rest.client.ContentService;
import com.octo.android.rest.client.persistence.CacheManager;

// ============================================================================================
// CLASS UNDER TEST
// ============================================================================================
public class ContentServiceUnderTest extends ContentService {

    @Override
    public CacheManager createCacheManager( Application arg0 ) {
        return new CacheManager();
    }

}