package com.octo.android.rest.client.sample;

import java.io.InputStream;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.rest.client.exception.ContentManagerException;
import com.octo.android.rest.client.persistence.DurationInMillis;
import com.octo.android.rest.client.request.RequestListener;
import com.octo.android.rest.client.request.simple.SimpleImageRequest;
import com.octo.android.rest.client.request.simple.SimpleTextRequest;
import com.octo.android.rest.client.sample.model.WeatherResult;
import com.octo.android.rest.client.sample.request.WeatherRequest;

@ContentView(R.layout.main)
public class HelloAndroidActivity extends RoboContentActivity {

    // ============================================================================================
    // ATTRIBUTES
    // ============================================================================================

    @InjectView(R.id.textview_hello_cnil)
    private TextView mLoremTextView;
    @InjectView(R.id.textview_hello_credit_status)
    private TextView mCurrentWeatherTextView;
    @InjectView(R.id.textview_hello_image)
    private TextView mImageTextView;

    SimpleTextRequest loremRequest;
    SimpleImageRequest imageRequest;
    WeatherRequest weatherRequest;

    // ============================================================================================
    // ACITVITY LIFE CYCLE
    // ============================================================================================

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        // Initializes the logging
        // Log a message (only on dev platform)
        Log.i( getClass().getName(), "onCreate" );

        loremRequest = new SimpleTextRequest( "http://www.loremipsum.de/downloads/original.txt" );
        weatherRequest = new WeatherRequest( "75000" );
        imageRequest = new SimpleImageRequest( "http://earthobservatory.nasa.gov/blogs/elegantfigures/files/2011/10/globe_west_2048.jpg" );
        // imageRequest = new SimpleImageRequest("http://cdn1.iconfinder.com/data/icons/softicons/PNG/Programming.png");
    }

    @Override
    protected void onResume() {
        super.onResume();
        execute( loremRequest, "lorem.txt", DurationInMillis.ONE_DAY, new LoremRequestListener() );
        execute( weatherRequest, "75000.weather", DurationInMillis.ONE_DAY, new WeatherRequestListener() );
        execute( imageRequest, "logo", DurationInMillis.ONE_DAY, new ImageRequestListener() );
    }

    // ============================================================================================
    // INNER CLASSES
    // ============================================================================================

    public final class LoremRequestListener implements RequestListener< String > {

        @Override
        public void onRequestFailure( ContentManagerException contentManagerException ) {
            Toast.makeText( HelloAndroidActivity.this, "failure", Toast.LENGTH_SHORT ).show();
        }

        @Override
        public void onRequestSuccess( final String result ) {
            Toast.makeText( HelloAndroidActivity.this, "success", Toast.LENGTH_SHORT ).show();
            String originalText = mLoremTextView.getText().toString();
            mLoremTextView.setText( originalText + result );
        }
    }

    public final class WeatherRequestListener implements RequestListener< WeatherResult > {

        @Override
        public void onRequestFailure( ContentManagerException contentManagerException ) {
            Toast.makeText( HelloAndroidActivity.this, "failure", Toast.LENGTH_SHORT ).show();
        }

        @Override
        public void onRequestSuccess( final WeatherResult result ) {
            Toast.makeText( HelloAndroidActivity.this, "success", Toast.LENGTH_SHORT ).show();
            String originalText = mCurrentWeatherTextView.getText().toString();
            mCurrentWeatherTextView.setText( originalText + result.toString() );
        }
    }

    public final class ImageRequestListener implements RequestListener< InputStream > {

        @Override
        public void onRequestFailure( ContentManagerException contentManagerException ) {
            Toast.makeText( HelloAndroidActivity.this, "failure", Toast.LENGTH_SHORT ).show();
        }

        @Override
        public void onRequestSuccess( final InputStream result ) {
            Bitmap bitmap = BitmapFactory.decodeStream( result );
            BitmapDrawable drawable = new BitmapDrawable( bitmap );
            Toast.makeText( HelloAndroidActivity.this, "success", Toast.LENGTH_SHORT ).show();
            mImageTextView.setBackgroundDrawable( drawable );
            mImageTextView.setText( "" );
        }
    }

}
