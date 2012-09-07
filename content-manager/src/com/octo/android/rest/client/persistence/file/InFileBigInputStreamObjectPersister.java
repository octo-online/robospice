package com.octo.android.rest.client.persistence.file;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Application;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.octo.android.rest.client.exception.CacheSavingException;

public final class InFileBigInputStreamObjectPersister extends InFileInputStreamObjectPersister {

    public InFileBigInputStreamObjectPersister( Application application ) {
        super( application );
    }

    @Override
    public InputStream saveDataToCacheAndReturnData( InputStream data, Object cacheKey ) throws CacheSavingException {
        // special case for big inputstream object : as it can be read only once and is too big to be locally
        // duplicated,
        // 1) we save it in file
        // 2) we load and return it from the file
        try {
            ByteStreams.copy( data, Files.newOutputStreamSupplier( getCacheFile( cacheKey ) ) );
            return new FileInputStream( getCacheFile( cacheKey ) );
        } catch ( IOException e ) {
            throw new CacheSavingException( e );
        }
    }

    @Override
    public void setAsyncSaveEnabled( boolean isAsyncSaveEnabled ) {
        throw new IllegalArgumentException( "Asynchronous saving operation not supported." );
    }
}
