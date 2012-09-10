package com.octo.android.rest.client;

import com.octo.android.rest.client.persistence.CacheManager;

/**
 * Allows to inject dependencies inside the {@link ContentService}.
 * 
 * Developpers have to implement this interface within their custom application class.
 * 
 * @author sni
 * 
 */
public interface ContentConfiguration {
    public abstract CacheManager getCacheManager();
}
