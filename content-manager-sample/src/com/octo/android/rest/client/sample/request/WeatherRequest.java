package com.octo.android.rest.client.sample.request;

import org.springframework.web.client.RestClientException;

import android.util.Log;

import com.octo.android.rest.client.sample.model.WeatherResult;

public final class WeatherRequest extends com.octo.android.rest.client.request.springandroid.RestContentRequest< WeatherResult > {

    private String baseUrl;

    public WeatherRequest( String zipCode ) {
        super( WeatherResult.class );
        this.baseUrl = String.format( "http://www.myweather2.com/developer/forecast.ashx?uac=AQmS68n6Ku&query=%s&output=json", zipCode );
    }

    @Override
    public WeatherResult loadDataFromNetwork() throws RestClientException {
        Log.d( getClass().getName(), "Call web service " + baseUrl );
        return getRestTemplate().getForObject( baseUrl, WeatherResult.class );
    }

}
