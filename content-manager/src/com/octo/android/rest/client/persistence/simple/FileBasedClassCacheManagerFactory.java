package com.octo.android.rest.client.persistence.simple;

import java.io.File;
import java.io.FileFilter;

import android.app.Application;

import com.octo.android.rest.client.persistence.ClassCacheManagerFactory;

public abstract class FileBasedClassCacheManagerFactory extends ClassCacheManagerFactory {

	public FileBasedClassCacheManagerFactory(Application application) {
		super(application);
	}

	@Override
	public abstract <DATA> FileBasedClassCacheManager<DATA> createDataPersistenceManager(Class<DATA> clazz);

	public boolean removeDataFromCache(Class<?> clazz, Object cacheKey) {
		return createDataPersistenceManager(clazz).removeDataFromCache(cacheKey);
	}

	public void removeAllDataFromCache(Class<?> clazz) {
		createDataPersistenceManager(clazz).removeAllDataFromCache();
	}

	public void removeAllDataFromCache() {
		File cacheFolder = getCacheFolder();
		File[] cacheFileList = cacheFolder.listFiles(new FileFilter() {

			public boolean accept(File pathname) {
				String path = pathname.getAbsolutePath();
				String fileName = path.substring(path.lastIndexOf(File.separator) + 1);
				return fileName.startsWith(getCachePrefix());
			}
		});

		for (File cacheFile : cacheFileList) {
			cacheFile.delete();
		}
	}

	protected String getCachePrefix() {
		return getClass().getSimpleName() + FileBasedClassCacheManager.CACHE_PREFIX_END;
	}

	private File getCacheFolder() {
		return getApplication().getCacheDir();
	}
}
