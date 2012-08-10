package com.octo.android.rest.client.sample.model;

import java.util.List;

public class Curren_weather {
    private String humidity;
    private String pressure;
    private String temp;
    private String temp_unit;
    private String weather_code;
    private String weather_text;
    private List wind;

    public String getHumidity() {
        return this.humidity;
    }

    public void setHumidity( String humidity ) {
        this.humidity = humidity;
    }

    public String getPressure() {
        return this.pressure;
    }

    public void setPressure( String pressure ) {
        this.pressure = pressure;
    }

    public String getTemp() {
        return this.temp;
    }

    public void setTemp( String temp ) {
        this.temp = temp;
    }

    public String getTemp_unit() {
        return this.temp_unit;
    }

    public void setTemp_unit( String temp_unit ) {
        this.temp_unit = temp_unit;
    }

    public String getWeather_code() {
        return this.weather_code;
    }

    public void setWeather_code( String weather_code ) {
        this.weather_code = weather_code;
    }

    public String getWeather_text() {
        return this.weather_text;
    }

    public void setWeather_text( String weather_text ) {
        this.weather_text = weather_text;
    }

    public List getWind() {
        return this.wind;
    }

    public void setWind( List wind ) {
        this.wind = wind;
    }
}
