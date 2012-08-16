package com.octo.android.rest.client.persistence;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

import com.octo.android.rest.client.persistence.json.JSonPersistenceManageFactory;
import com.octo.android.rest.client.sample.TestActivity;
import com.octo.android.rest.client.sample.model.Curren_weather;
import com.octo.android.rest.client.sample.model.Weather;
import com.octo.android.rest.client.sample.model.WeatherResult;

@SmallTest
public class WeatherPersistenceManagerTest extends ActivityInstrumentationTestCase2< TestActivity > {
    private ClassCacheManager< WeatherResult > dataPersistenceManager;

    public WeatherPersistenceManagerTest() {
        super( "com.octo.android.rest.client.sample", TestActivity.class );
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        JSonPersistenceManageFactory factory = new JSonPersistenceManageFactory( getActivity().getApplication() );
        dataPersistenceManager = factory.createClassCacheManager( WeatherResult.class );
    }

    public void test_canHandleClientRequestStatus() {
        boolean canHandleClientWeatherResult = dataPersistenceManager.canHandleClass( WeatherResult.class );
        assertEquals( true, canHandleClientWeatherResult );
    }

    public void test_saveDataAndReturnData() throws FileNotFoundException, IOException {
        // GIVEN
        WeatherResult weatherRequestStatus = buildWeather();

        // WHEN
        WeatherResult weatherReturned = dataPersistenceManager.saveDataToCacheAndReturnData( weatherRequestStatus, "weather.json" );

        // THEN
        assertEquals( "28", weatherReturned.getWeather().getCurren_weather().get( 0 ).getTemp() );
    }

    public void test_loadDataFromCache() throws FileNotFoundException, IOException, CacheExpiredException {
        // GIVEN
        WeatherResult weatherRequestStatus = buildWeather();
        final String FILE_NAME = "toto";
        dataPersistenceManager.saveDataToCacheAndReturnData( weatherRequestStatus, FILE_NAME );

        // WHEN
        WeatherResult weatherReturned = dataPersistenceManager.loadDataFromCache( FILE_NAME, 0 );

        // THEN
        assertEquals( "28", weatherReturned.getWeather().getCurren_weather().get( 0 ).getTemp() );
    }

    private WeatherResult buildWeather() {
        WeatherResult weatherRequestStatus = new WeatherResult();
        Weather weather = new Weather();
        List< Curren_weather > currents = new ArrayList< Curren_weather >();
        Curren_weather current_weather = new Curren_weather();
        current_weather.setTemp( "28" );
        current_weather.setTemp_unit( "C" );
        currents.add( current_weather );
        weather.setCurren_weather( currents );
        weatherRequestStatus.setWeather( weather );
        return weatherRequestStatus;
    }
}
