package com.octo.android.rest.client.persistence.simple;

import java.io.File;
import java.io.FileFilter;

import android.app.Application;

import com.octo.android.rest.client.persistence.ClassCacheManager;

public abstract class FileBasedClassCacheManager< DATA > extends ClassCacheManager< DATA > {

    private static final String CACHE_PREFIX_END = "_";

    public FileBasedClassCacheManager( Application application ) {
        super( application );
    }

    @Override
    public void removeDataFromCache( Object cacheKey ) {
        getCacheFile( cacheKey ).delete();
    }

    @Override
    public void removeAllDataFromCache() {
        File cacheFolder = getCacheFolder();
        File[] cacheFileList = cacheFolder.listFiles( new FileFilter() {

            public boolean accept( File pathname ) {
                String path = pathname.getAbsolutePath();
                String fileName = path.substring( path.lastIndexOf( File.separator ) + 1 );
                return fileName.startsWith( getCachePrefix() );
            }
        } );

        for ( File cacheFile : cacheFileList ) {
            cacheFile.delete();
        }
    }

    protected String getCachePrefix() {
        return getClass().getSimpleName() + CACHE_PREFIX_END;
    }

    protected File getCacheFile( Object cacheKey ) {
        return new File( getCacheFolder(), getCachePrefix() + cacheKey.toString() );
    }

    private File getCacheFolder() {
        return getApplication().getCacheDir();
    }

}