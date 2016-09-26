package com.octo.android.robospice.persistence.retrofit2;

import java.io.File;
import java.io.IOException;

import roboguice.util.temp.Ln;
import android.app.Application;

import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.exception.CacheLoadingException;
import com.octo.android.robospice.persistence.exception.CacheSavingException;
import com.octo.android.robospice.persistence.file.InFileObjectPersister;
import com.octo.android.robospice.persistence.retrofit2.transformers.RetrofitConvertAware;

public abstract class RetrofitObjectPersister2<T> extends InFileObjectPersister<T> {
    private RetrofitConvertAware converter;

    // ============================================================================================
    // CONSTRUCTOR
    // ============================================================================================
    public RetrofitObjectPersister2(Application application, RetrofitConvertAware converter, Class<T> clazz, File cacheFolder) throws CacheCreationException {
        super(application, clazz, cacheFolder);
        this.converter = converter;
    }

    public RetrofitObjectPersister2(Application application, RetrofitConvertAware converter, Class<T> clazz) throws CacheCreationException {
        this(application, converter, clazz, null);
    }

    // ============================================================================================
    // METHODS
    // ============================================================================================
    @Override
    public T saveDataToCacheAndReturnData(final T data, final Object cacheKey) throws CacheSavingException {
        try {
            if (isAsyncSaveEnabled()) {
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        try {
                            saveData(data, cacheKey);
                        } catch (IOException e) {
                            Ln.e(e, "An error occured on saving request " + cacheKey + " data asynchronously");
                        } catch (CacheSavingException e) {
                            Ln.e(e, "An error occured on saving request " + cacheKey + " data asynchronously");
                        }
                    }
                };
                t.start();
            } else {
                saveData(data, cacheKey);
            }
        } catch (CacheSavingException e) {
            throw e;
        } catch (Exception e) {
            throw new CacheSavingException(e);
        }
        return data;
    }

    protected abstract void saveData(T data, Object cacheKey) throws IOException, CacheSavingException;

    @Override
    protected abstract T readCacheDataFromFile(File file) throws CacheLoadingException;

    protected RetrofitConvertAware getConverter() {
        return this.converter;
    }
}
