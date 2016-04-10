package com.octo.android.robospice.retrofit2;

import java.io.File;

import retrofit2.Converter.Factory;
import android.app.Application;

import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.retrofit2.RetrofitObjectPersisterFactory2;
import com.octo.android.robospice.persistence.retrofit2.transformers.RetrofitJacksonConvertAware;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * A pre-set, easy to use, retrofit service. It will use retrofit for network
 * requests and both networking and caching will use Jackson. To use it, make your
 * service to extend this service.
 */
public abstract class RetrofitJacksonSpiceService2 extends RetrofitSpiceService2 {

    @Override
    public CacheManager createCacheManager(Application application) throws CacheCreationException {
        CacheManager cacheManager = new CacheManager();
        cacheManager.addPersister(new RetrofitObjectPersisterFactory2(getApplication(), getRetrofitToCacheConverter(), getCacheFolder()));
        return cacheManager;
    }

    @Override
    protected final Factory createConverterFactory() {
        return JacksonConverterFactory.create();
    }

    public File getCacheFolder() {
        return null;
    }

    @Override
    protected RetrofitJacksonConvertAware createRetrofitToCacheConverter() {
        return new RetrofitJacksonConvertAware();
    }
}
