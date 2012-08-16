package com.octo.android.rest.client.persistence.simple;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.Application;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.octo.android.rest.client.persistence.CacheExpiredException;
import com.octo.android.rest.client.persistence.ClassCacheManager;

public final class BinaryPersistenceManager extends ClassCacheManager< InputStream > {

    public BinaryPersistenceManager( Application application ) {
        super( application );
    }

    @Override
    public InputStream loadDataFromCache( Object cacheKey, long maxTimeInCacheBeforeExpiry ) throws FileNotFoundException, CacheExpiredException {
        File file = getCacheFile( cacheKey );
        if ( file.exists() ) {
            long timeInCache = System.currentTimeMillis() - file.lastModified();
            if ( maxTimeInCacheBeforeExpiry == 0 || timeInCache <= maxTimeInCacheBeforeExpiry ) {
                return new FileInputStream( file );
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
    public InputStream saveDataToCacheAndReturnData( InputStream data, Object cacheKey ) throws FileNotFoundException, IOException {
        // special case for inputstream object : as it can be read only once,
        // 0) we extract the content of the input stream as a byte[]
        // 1) we save it in file asynchronously
        // 2) the result will be a new InputStream on the byte[]
        byte[] byteArray = ByteStreams.toByteArray( data );
        ByteStreams.write( byteArray, Files.newOutputStreamSupplier( getCacheFile( cacheKey ) ) );
        return new ByteArrayInputStream( byteArray );
    }

    public boolean canHandleClass( Class< ? > clazz ) {
        try {
            clazz.asSubclass( InputStream.class );
            return true;
        } catch ( ClassCastException ex ) {
            return false;
        }
    }
}
