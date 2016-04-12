package com.octo.android.robospice.persistence.retrofit2.transformers;

import com.fasterxml.jackson.databind.ObjectMapper;

public class RetrofitJacksonConvertAware extends RetrofitConvertAware {

    private final ObjectMapper mapper;

    public RetrofitJacksonConvertAware() {
        this(new ObjectMapper());
    }

    public RetrofitJacksonConvertAware(ObjectMapper mapper) {
        if (mapper != null) {
            this.mapper = mapper;
        } else {
            this.mapper = new ObjectMapper();
        }
    }

    @Override
    public String convertToString(Object object, Class<?> clzz) throws Exception {
        return mapper.writeValueAsString(object);
    }

    @Override
    public Object convertFromString(String string, Class<?> clzz) throws Exception {
        return mapper.readValue(string, clzz);
    }

}
