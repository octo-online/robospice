package com.octo.android.robospice.retrofit2.test;

import com.octo.android.robospice.retrofit2.RetrofitJacksonSpiceService2;
import java.io.File;

public class TestRetrofitJacksonSpiceService2 extends RetrofitJacksonSpiceService2 {

    @Override
    protected String getServerUrl() {
        return "http://non-blank.random.server/";
    }

    @Override
    public File getCacheFolder() {
        return new File("/");
    }

}
