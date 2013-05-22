package com.octo.android.robospice.persistence;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.io.FileUtils;

import roboguice.util.temp.Ln;
import android.app.Application;

import com.octo.android.robospice.persistence.exception.CacheLoadingException;
import com.octo.android.robospice.persistence.exception.CacheSavingException;

/**
 * An entity responsible for loading/saving data from/to cache. It implements a
 * Chain of Responsibility pattern, delegating loading and saving operations to
 * {@link ObjectPersister} or {@link ObjectPersisterFactory} elements. The chain
 * of responsibility is ordered. This means that the order used to register
 * elements matters. All elements in the chain of responsibility are questioned
 * in order. The first element that can handle a given class for persistence
 * will be used to persist data of this class.
 * @author sni
 */
/*
 * Note to maintainers : concurrency must be taken care of as persister can be
 * created by factories at any time. Thx to Henri Tremblay from EasyMock for
 * peer review and concurrency checks.
 */
public class CacheManager implements ICacheManager {

    private static final String ROBOSPICEDB_SUFFIX = ".robospicedb";

    public static final String DEFAULT_CACHE_FOLDER = "robospice";

    /** The Chain of Responsibility list of all {@link Persister}. */
    private Collection<Persister> listPersister = new ArrayList<Persister>();
    private Map<ObjectPersisterFactory, List<ObjectPersister<?>>> mapFactoryToPersister = new HashMap<ObjectPersisterFactory, List<ObjectPersister<?>>>();

    private Application mApplication;

    public void setApplication(final Application application) {
        mApplication = application;
    }

    /** {@inheritDoc} */
    @Override
    public void addPersister(Persister persister) {
        listPersister.add(persister);
        if (persister instanceof ObjectPersisterFactory) {
            // will lead the list to be copied whenever we add a persister to it
            // but there won't be any overhead while iterating through the list.
            mapFactoryToPersister.put((ObjectPersisterFactory) persister, new CopyOnWriteArrayList<ObjectPersister<?>>());
        } else if (!(persister instanceof ObjectPersister)) {
            throw new RuntimeException(getClass().getSimpleName() + " only supports " + ObjectPersister.class.getSimpleName() + " or "
                + ObjectPersisterFactory.class.getSimpleName() + " instances.");
        }
    }

    /** {@inheritDoc} */
    @Override
    public void removePersister(Persister persister) {
        listPersister.remove(persister);
        if (persister instanceof ObjectPersisterFactory) {
            mapFactoryToPersister.remove(persister);
        }
    }

    /** {@inheritDoc} */
    @Override
    public <T> T loadDataFromCache(Class<T> clazz, Object cacheKey, long maxTimeInCacheBeforeExpiry) throws CacheLoadingException {
        return getObjectPersister(clazz).loadDataFromCache(cacheKey, maxTimeInCacheBeforeExpiry);
    }

    @Override
    @SuppressWarnings("unchecked")
    /** {@inheritDoc}*/
    public <T> T saveDataToCacheAndReturnData(T data, Object cacheKey) throws CacheSavingException {
        // http://stackoverflow.com/questions/4460580/java-generics-why-someobject-getclass-doesnt-return-class-extends-t
        ObjectPersister<T> classCacheManager = getObjectPersister((Class<T>) data.getClass());
        return classCacheManager.saveDataToCacheAndReturnData(data, cacheKey);
    }

    /** {@inheritDoc} */
    @Override
    public boolean removeDataFromCache(Class<?> clazz, Object cacheKey) {
        return getObjectPersister(clazz).removeDataFromCache(cacheKey);
    }

    /** {@inheritDoc} */
    @Override
    public void removeAllDataFromCache(Class<?> clazz) {
        getObjectPersister(clazz).removeAllDataFromCache();
    }

    /** {@inheritDoc} */
    @Override
    public <T> List<Object> getAllCacheKeys(final Class<T> clazz) {
        return getObjectPersister(clazz).getAllCacheKeys();
    }

    /** {@inheritDoc} */
    @Override
    public <T> List<T> loadAllDataFromCache(final Class<T> clazz) throws CacheLoadingException {
        return getObjectPersister(clazz).loadAllDataFromCache();
    }

    /** {@inheritDoc} */
    @Override
    public void removeAllDataFromCache() {
        for (Persister persister : this.listPersister) {
            if (persister instanceof ObjectPersister) {
                ((ObjectPersister<?>) persister).removeAllDataFromCache();
            } else if (persister instanceof ObjectPersisterFactory) {
                ObjectPersisterFactory factory = (ObjectPersisterFactory) persister;
                List<ObjectPersister<?>> listPersisterForFactory = mapFactoryToPersister.get(factory);
                for (ObjectPersister<?> objectPersister : listPersisterForFactory) {
                    objectPersister.removeAllDataFromCache();
                }
            }
        }

        if (mApplication != null) {
            deleteAllRobospiceDefaultCacheFolderContent();
            deleteAllRobospiceDatabaseFiles();
        } else {
            Ln.w("Cache Manager needs the application to be set. Failed to clean cache.");
        }
    }

    private void deleteAllRobospiceDefaultCacheFolderContent() {
        final File defaultCacheRootFolder = new File(mApplication.getCacheDir(), DEFAULT_CACHE_FOLDER);
        try {
            FileUtils.deleteDirectory(defaultCacheRootFolder);
        } catch (IOException e) {
            Ln.e(e, "Unable to delete cache folder: %s", defaultCacheRootFolder.getAbsolutePath());
        }
    }

    private void deleteAllRobospiceDatabaseFiles() {
        // We use any file name in order to resolve the database parent folder.
        final File databaseFolder = mApplication.getDatabasePath(ROBOSPICEDB_SUFFIX).getParentFile();
        final File[] markers = databaseFolder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(ROBOSPICEDB_SUFFIX);
            }
        });
        if (markers != null) {
            for (int i = 0; i < markers.length; i++) {
                final String markerPath = markers[i].getAbsolutePath();
                final String databasePath = markerPath.substring(0, markerPath.lastIndexOf(ROBOSPICEDB_SUFFIX));
                final File database = new File(databasePath);
                if (database.exists() && !database.delete()) {
                    Ln.e("Unable to delete database: %s", database.getAbsolutePath());
                }
                if (!markers[i].delete()) {
                    Ln.e("Unable to delete marker: %s", database.getAbsolutePath());
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> ObjectPersister<T> getObjectPersister(Class<T> clazz) {
        for (Persister persister : this.listPersister) {
            if (persister.canHandleClass(clazz)) {
                if (persister instanceof ObjectPersister) {
                    return (ObjectPersister<T>) persister;
                }

                if (persister instanceof ObjectPersisterFactory) {
                    ObjectPersisterFactory factory = (ObjectPersisterFactory) persister;

                    if (factory.canHandleClass(clazz)) {
                        List<ObjectPersister<?>> listPersisterForFactory = mapFactoryToPersister.get(factory);
                        for (ObjectPersister<?> objectPersister : listPersisterForFactory) {
                            if (objectPersister.canHandleClass(clazz)) {
                                return (ObjectPersister<T>) objectPersister;
                            }
                        }
                        ObjectPersister<T> newPersister = factory.createObjectPersister(clazz);
                        listPersisterForFactory.add(newPersister);
                        return newPersister;
                    }
                }
            }
        }
        throw new RuntimeException("Class " + clazz.getName() + " is not handled by any registered factoryList");
    }
}
