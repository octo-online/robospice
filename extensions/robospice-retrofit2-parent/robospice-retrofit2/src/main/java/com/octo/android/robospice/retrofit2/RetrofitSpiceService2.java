package com.octo.android.robospice.retrofit2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Converter.Factory;
import retrofit2.Retrofit;

import com.octo.android.robospice.SpiceService;
import com.octo.android.robospice.persistence.retrofit2.transformers.RetrofitConvertAware;
import com.octo.android.robospice.request.CachedSpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.retrofit2.RetrofitSpiceRequest2;

public abstract class RetrofitSpiceService2 extends SpiceService {

    private final Map<Class<?>, Object> retrofitInterfaceToServiceMap = new HashMap<Class<?>, Object>();
    private Retrofit.Builder builder;
    private Retrofit restAdapter;
    private final List<Class<?>> retrofitInterfaceList = new ArrayList<Class<?>>();
    private Factory converterFactory;
    private RetrofitConvertAware retrofitToCacheConverter;

    @Override
    public void onCreate() {
        super.onCreate();
        builder = createRestAdapterBuilder();
        restAdapter = builder.build();
    }

    protected abstract String getServerUrl();

    protected Retrofit.Builder createRestAdapterBuilder() {
        return new Retrofit.Builder().baseUrl(getServerUrl()).addConverterFactory(getConverterFactory());
    }

    protected abstract Factory createConverterFactory();

    protected final Factory getConverterFactory() {
        if (converterFactory == null) {
            converterFactory = createConverterFactory();
        }
        return converterFactory;
    }

    /**
     * Creates a converter which converts a data received by an underlying
     * Retrofit into format suitable to be written to a cache file
     *
     * @return {@link RetrofitConvertAware} object
     */
    protected abstract RetrofitConvertAware createRetrofitToCacheConverter();

    protected final RetrofitConvertAware getRetrofitToCacheConverter() {
        if (this.retrofitToCacheConverter == null) {
            this.retrofitToCacheConverter = createRetrofitToCacheConverter();
        }
        return this.retrofitToCacheConverter;
    }

    @SuppressWarnings("unchecked")
    protected <T> T getRetrofitService(Class<T> serviceClass) {
        T service = (T) retrofitInterfaceToServiceMap.get(serviceClass);
        if (service == null) {
            service = restAdapter.create(serviceClass);
            retrofitInterfaceToServiceMap.put(serviceClass, service);
        }
        return service;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void addRequest(CachedSpiceRequest<?> request, Set<RequestListener<?>> listRequestListener) {
        if (request.getSpiceRequest() instanceof RetrofitSpiceRequest2) {
            RetrofitSpiceRequest2 retrofitSpiceRequest = (RetrofitSpiceRequest2) request.getSpiceRequest();
            retrofitSpiceRequest.setService(getRetrofitService(retrofitSpiceRequest.getRetrofitedInterfaceClass()));
        }
        super.addRequest(request, listRequestListener);
    }

    public final List<Class<?>> getRetrofitInterfaceList() {
        return retrofitInterfaceList;
    }

    protected void addRetrofitInterface(Class<?> serviceClass) {
        retrofitInterfaceList.add(serviceClass);
    }

}
