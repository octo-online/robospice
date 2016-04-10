package com.octo.android.robospice.retrofit2.test;

import com.octo.android.robospice.retrofit2.RetrofitGsonSpiceService2;
import java.io.File;

public class TestRetrofitGsonSpiceService2 extends RetrofitGsonSpiceService2 {

    @Override
    protected String getServerUrl() {
        return "http://non-blank.random.server/";
    }

    @Override
    public File getCacheFolder() {
        return new File("/");
    }

}
