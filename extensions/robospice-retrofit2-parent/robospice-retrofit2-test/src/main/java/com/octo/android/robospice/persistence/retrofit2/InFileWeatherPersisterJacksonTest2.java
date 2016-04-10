package com.octo.android.robospice.persistence.retrofit2;

import android.app.Application;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.retrofit2.transformers.RetrofitJacksonConvertAware;


public class InFileWeatherPersisterJacksonTest2 extends JsonObjectPersisterFactoryTest {

    @Override
    protected RetrofitObjectPersisterFactory2 getRetrofitObjectPersisterFactory(Application application) throws CacheCreationException {
        return new RetrofitObjectPersisterFactory2(application, new RetrofitJacksonConvertAware());

    }

}
