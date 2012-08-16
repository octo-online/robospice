package com.octo.android.rest.client.persistence.simple;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

import android.app.Application;
import android.util.Log;

import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.octo.android.rest.client.persistence.CacheExpiredException;
import com.octo.android.rest.client.persistence.DataClassPersistenceManager;

public final class StringPersistenceManager extends DataClassPersistenceManager< String > {
    private final static String LOG_CAT = "StringPersistenceManager";

    public StringPersistenceManager( Application application ) {
        super( application );
    }

    @Override
    public String loadDataFromCache( Object cacheKey, long maxTimeInCacheBeforeExpiry ) throws FileNotFoundException, IOException, CacheExpiredException {
        Log.v( LOG_CAT, "Loading String for cacheKey = " + cacheKey );
        File file = getCacheFile( cacheKey );
        if ( file.exists() ) {
            long timeInCache = System.currentTimeMillis() - file.lastModified();
            if ( maxTimeInCacheBeforeExpiry == 0 || timeInCache <= maxTimeInCacheBeforeExpiry ) {
                return CharStreams.toString( Files.newReader( file, Charset.forName( "UTF-8" ) ) );
            } else {
                throw new CacheExpiredException( "Cache content is expired since " + ( maxTimeInCacheBeforeExpiry - timeInCache ) );
            }
        }
        throw new FileNotFoundException( "File was not found in cache: " + file.getAbsolutePath() );

    }

    private File getCacheFile( Object cacheKey ) {
        return new File( getApplication().getCacheDir(), cacheKey.toString() );
    }

    @Override
    public String saveDataToCacheAndReturnData( String data, Object cacheKey ) throws FileNotFoundException, IOException {
        Log.v( LOG_CAT, "Saving String " + data + " into cacheKey = " + cacheKey );
        Files.write( data, getCacheFile( cacheKey ), Charset.forName( "UTF-8" ) );
        return data;
    }

    public boolean canHandleClass( Class< ? > clazz ) {
        return clazz.equals( String.class );
    }

}
