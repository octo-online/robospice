package com.octo.android.rest.client.sample.model;

public class Wind {
    private String dir;
    private String speed;
    private String wind_unit;

    public String getDir() {
        return this.dir;
    }

    public void setDir( String dir ) {
        this.dir = dir;
    }

    public String getSpeed() {
        return this.speed;
    }

    public void setSpeed( String speed ) {
        this.speed = speed;
    }

    public String getWind_unit() {
        return this.wind_unit;
    }

    public void setWind_unit( String wind_unit ) {
        this.wind_unit = wind_unit;
    }
}
