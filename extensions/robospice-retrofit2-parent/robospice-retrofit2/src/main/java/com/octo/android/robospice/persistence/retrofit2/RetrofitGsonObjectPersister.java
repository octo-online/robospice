/*
 * Copyright 2016 tony.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.octo.android.robospice.persistence.retrofit2;

import android.app.Application;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.exception.CacheLoadingException;
import com.octo.android.robospice.persistence.exception.CacheSavingException;
import com.octo.android.robospice.persistence.retrofit2.transformers.RetrofitConvertAware;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import roboguice.util.temp.Ln;


public class RetrofitGsonObjectPersister<T> extends RetrofitObjectPersister<T> {
    private static final int READ_BUFFER_SIZE = 512;
    private final RetrofitConvertAware converter;

    public RetrofitGsonObjectPersister(Application application, RetrofitConvertAware converter, Class<T> clazz, File cacheFolder) throws CacheCreationException {
        super(application, converter, clazz, cacheFolder);
        this.converter = converter;
    }

    public RetrofitGsonObjectPersister(Application application, RetrofitConvertAware converter, Class<T> clazz) throws CacheCreationException {
        super(application, converter, clazz);
        this.converter = converter;
    }

    @Override
    protected void saveData(T data, Object cacheKey) throws IOException, CacheSavingException {
        FileOutputStream outStream = null;
        try {
            Class<T> handledClass = getHandledClass();
            String str = converter.convertToString(data, handledClass);
            File file = getCacheFile(cacheKey);
            outStream = new FileOutputStream(file);
            outStream.write(str.getBytes(Charset.forName("UTF-8")));
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new CacheSavingException(e);
        } finally {
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    Ln.d(e);
                }
            }
        }
    }

    @Override
    protected T readCacheDataFromFile(File file) throws CacheLoadingException {
        if (!file.exists()) {
            Ln.w("file \"" + file.getAbsolutePath() + "\" does not exist");
            return null;
        }

        StringBuilder sb = new StringBuilder();
        byte[] buffer = new byte[READ_BUFFER_SIZE];
        FileInputStream inReader = null;
        try {
            Charset charset = Charset.forName("UTF-8");
            inReader = new FileInputStream(file);
            int byteCount;
            while ((byteCount = inReader.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, byteCount, charset));
            }
            Class<T> handledClass = getHandledClass();
            return (T) converter.convertFromString(sb.toString(), handledClass);
        } catch (Exception e) {
            throw new CacheLoadingException(e);
        } finally {
            if (inReader != null) {
                try {
                    inReader.close();
                } catch (IOException e) {
                    Ln.d(e);
                }
            }
        }
    }
}
