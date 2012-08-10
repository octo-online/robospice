package com.octo.android.rest.client.request.simple;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

import com.octo.android.rest.client.request.ContentRequest;

public class AbstractImageRequest extends ContentRequest< InputStream > {

    protected String url;

    public AbstractImageRequest( String url ) {
        super( InputStream.class );
        this.url = url;
    }

    @Override
    public final InputStream loadDataFromNetwork() throws Exception {
        try {
            return new URL( url ).openStream();
        } catch ( MalformedURLException e ) {
            Log.e( getClass().getName(), "Unable to create image URL" );
            return null;
        } catch ( IOException e ) {
            Log.e( getClass().getName(), "Unable to download image" );
            return null;
        }
    }

    protected final String getUrl() {
        return this.url;
    }

}
