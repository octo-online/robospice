package com.octo.android.rest.client.persistence.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import android.app.Application;
import android.util.Log;

import com.google.common.base.Strings;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.octo.android.rest.client.persistence.CacheExpiredException;
import com.octo.android.rest.client.persistence.ClassCacheManager;

public final class JSonPersistenceManager< T > extends ClassCacheManager< T > {

    // ============================================================================================
    // ATTRIBUTES
    // ============================================================================================

    private final ObjectMapper mJsonMapper;

    private Class< T > clazz;

    // ============================================================================================
    // CONSTRUCTOR
    // ============================================================================================
    public JSonPersistenceManager( Application application, Class< T > clazz ) {
        super( application );
        this.clazz = clazz;
        this.mJsonMapper = new ObjectMapper();
    }

    // ============================================================================================
    // METHODS
    // ============================================================================================

    @Override
    public final T loadDataFromCache( Object cacheKey, long maxTimeInCacheBeforeExpiry ) throws JsonParseException, JsonMappingException, IOException,
            CacheExpiredException {
        T result = null;
        String resultJson = null;

        File file = getCacheFile( cacheKey );
        if ( file.exists() ) {
            long timeInCache = System.currentTimeMillis() - file.lastModified();
            if ( maxTimeInCacheBeforeExpiry == 0 || timeInCache <= maxTimeInCacheBeforeExpiry ) {
                resultJson = CharStreams.toString( Files.newReader( file, Charset.forName( "UTF-8" ) ) );
                if ( resultJson != null ) {
                    // finally transform json in object
                    if ( !Strings.isNullOrEmpty( resultJson ) ) {
                        result = mJsonMapper.readValue( resultJson, clazz );
                    } else {
                        Log.e( getClass().getName(), "Unable to restore cache content : cache file is empty" );
                    }
                } else {
                    Log.e( getClass().getName(), "Unable to restore cache content" );
                }
                return result;
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
    public T saveDataToCacheAndReturnData( T data, Object cacheKey ) throws FileNotFoundException, IOException {
        String resultJson = null;

        // transform the content in json to store it in the cache
        resultJson = mJsonMapper.writeValueAsString( data );

        // finally store the json in the cache
        if ( !Strings.isNullOrEmpty( resultJson ) ) {
            Files.write( resultJson, getCacheFile( cacheKey ), Charset.forName( "UTF-8" ) );
        } else {
            Log.e( getClass().getName(), "Unable to save web service result into the cache" );
        }
        return data;
    }

    @Override
    public boolean canHandleClass( Class< ? > clazz ) {
        return true;
    }

}
