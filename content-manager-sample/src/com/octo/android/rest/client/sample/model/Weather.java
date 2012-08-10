package com.octo.android.rest.client.sample.model;

import java.util.List;

public class Weather {
    private List curren_weather;
    private List forecast;

    public List getCurren_weather() {
        return this.curren_weather;
    }

    public void setCurren_weather( List curren_weather ) {
        this.curren_weather = curren_weather;
    }

    public List getForecast() {
        return this.forecast;
    }

    public void setForecast( List forecast ) {
        this.forecast = forecast;
    }
}
