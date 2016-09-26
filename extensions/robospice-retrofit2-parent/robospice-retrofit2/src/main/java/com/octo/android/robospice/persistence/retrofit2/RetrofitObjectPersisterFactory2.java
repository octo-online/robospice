package com.octo.android.robospice.persistence.retrofit2;

import java.io.File;

import android.app.Application;

import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.file.InFileObjectPersister;
import com.octo.android.robospice.persistence.file.InFileObjectPersisterFactory;
import com.octo.android.robospice.persistence.retrofit2.transformers.RetrofitConvertAware;
import java.util.List;

public class RetrofitObjectPersisterFactory2 extends InFileObjectPersisterFactory {

    // ----------------------------------
    // ATTRIBUTES
    // ----------------------------------
    private final RetrofitConvertAware converter;

    // ----------------------------------
    // CONSTRUCTORS
    // ----------------------------------
    public RetrofitObjectPersisterFactory2(Application application, RetrofitConvertAware converter, File cacheFolder) throws CacheCreationException {
        this(application, converter, null, cacheFolder);
    }

    public RetrofitObjectPersisterFactory2(Application application, RetrofitConvertAware converter, List<Class<?>> listHandledClasses, File cacheFolder) throws CacheCreationException {
        super(application, listHandledClasses, cacheFolder);
        this.converter = converter;
    }

    public RetrofitObjectPersisterFactory2(Application application, RetrofitConvertAware converter) throws CacheCreationException {
        this(application, converter, null, null);
    }

    public RetrofitObjectPersisterFactory2(Application application, RetrofitConvertAware converter,
            List<Class<?>> listHandledClasses) throws CacheCreationException {
        this(application, converter, listHandledClasses, null);
    }

    // ----------------------------------
    // API
    // ----------------------------------
    @Override
    public <DATA> InFileObjectPersister<DATA> createInFileObjectPersister(Class<DATA> clazz, File cacheFolder) throws CacheCreationException {
        return new RetrofitGsonObjectPersister2<DATA>(getApplication(), converter, clazz, cacheFolder);
    }

}
