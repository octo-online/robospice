package com.octo.android.rest.client.persistence.simple;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;

import android.app.Application;
import android.util.Log;

import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.octo.android.rest.client.exception.CacheLoadingException;
import com.octo.android.rest.client.exception.CacheSavingException;

public final class StringCacheManager extends FileBasedClassCacheManager< String > {
    private final static String LOG_CAT = StringCacheManager.class.getSimpleName();

    public StringCacheManager( Application application ) {
        super( application );
    }

    @Override
    public boolean canHandleClass( Class< ? > clazz ) {
        return clazz.equals( String.class );
    }

    @Override
    public String loadDataFromCache( Object cacheKey, long maxTimeInCacheBeforeExpiry ) throws CacheLoadingException {
        Log.v( LOG_CAT, "Loading String for cacheKey = " + cacheKey );
        File file = getCacheFile( cacheKey );
        if ( file.exists() ) {
            long timeInCache = System.currentTimeMillis() - file.lastModified();
            if ( maxTimeInCacheBeforeExpiry == 0 || timeInCache <= maxTimeInCacheBeforeExpiry ) {
                try {
                    return CharStreams.toString( Files.newReader( file, Charset.forName( "UTF-8" ) ) );
                } catch ( FileNotFoundException e ) {
                    // Should not occur (we test before if file exists)
                    // Do not throw, file is not cached
                    Log.w( LOG_CAT, "file " + file.getAbsolutePath() + " does not exists", e );
                    return null;
                } catch ( Exception e ) {
                    throw new CacheLoadingException( e );
                }
            }
        }
        Log.v( LOG_CAT, "file " + file.getAbsolutePath() + " does not exists" );
        return null;
    }

    @Override
    public String saveDataToCacheAndReturnData( String data, Object cacheKey ) throws CacheSavingException {
        Log.v( LOG_CAT, "Saving String " + data + " into cacheKey = " + cacheKey );
        try {
            Files.write( data, getCacheFile( cacheKey ), Charset.forName( "UTF-8" ) );
        } catch ( Exception e ) {
            throw new CacheSavingException( e );
        }
        return data;
    }

}
