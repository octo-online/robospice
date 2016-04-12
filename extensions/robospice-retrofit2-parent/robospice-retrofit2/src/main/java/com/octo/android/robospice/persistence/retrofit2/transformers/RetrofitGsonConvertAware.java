package com.octo.android.robospice.persistence.retrofit2.transformers;

import com.google.gson.Gson;

public class RetrofitGsonConvertAware extends RetrofitConvertAware {

    private final Gson gson;

    public RetrofitGsonConvertAware() {
        this(new Gson());
    }

    public RetrofitGsonConvertAware(Gson gson) {
        if (gson != null) {
            this.gson = gson;
        } else {
            this.gson = new Gson();
        }
    }

    @Override
    public String convertToString(Object object, Class<?> clzz) throws Exception {
        return this.gson.toJson(object);
    }

    @Override
    public Object convertFromString(String string, Class<?> clzz) throws Exception {
        if (string == null || string.length() == 0) {
            return null;
        }
        return this.gson.fromJson(string, clzz);
    }

}
