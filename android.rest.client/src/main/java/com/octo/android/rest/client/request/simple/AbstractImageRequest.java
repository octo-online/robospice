package com.octo.android.rest.client.request.simple;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.web.client.RestClientException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;


public abstract class AbstractImageRequest extends
CachedRestRequest<InputStream> {

	protected static final String BUNDLE_EXTRA_IMAGE_URL = "BUNDLE_EXTRA_IMAGE_URL";
	protected String url;

	public AbstractImageRequest(Context context, String url) {
		super(context, InputStream.class);
		this.url = url;	}

	@Override
	public final InputStream loadDataFromNetwork()
			throws RestClientException {
		try {
			return new URL(url).openStream();
		} catch (MalformedURLException e) {
			Log.e(getClass().getName(), "Unable to create image URL");
			return null;
		} catch (IOException e) {
			Log.e(getClass().getName(), "Unable to download image");
			return null;
		}
	}

	protected final String getUrl() {
		return this.url;
	}

	@Override
	public final String getCacheKey() {
		return url.replace(":", "").replace("/", "_");
	}

	@Override
	protected final void onRequestSuccess(InputStream result) {
		Bitmap bitmap = BitmapFactory.decodeStream(result);
		BitmapDrawable drawable = new BitmapDrawable(bitmap);
		onRequestSuccess( drawable);
	}

	protected abstract void onRequestSuccess(Drawable result);

}

/**
 * Image Decoder Bug correction FilterInputStream. Permit to decode JPG image with BitmapFactory.decodeStream() method, what fails in Android 1.6.
 * 
 * @author octo_mwa
 * 
 */
class FlushedInputStream extends FilterInputStream {
	public FlushedInputStream(InputStream inputStream) {
		super(inputStream);
	}

	@Override
	public long skip(long n) throws IOException {
		long totalBytesSkipped = 0L;
		while (totalBytesSkipped < n) {
			long bytesSkipped = in.skip(n - totalBytesSkipped);
			if (bytesSkipped == 0L) {
				int bytes = read();
				if (bytes < 0) {
					break; // we reached EOF
				}
				else {
					bytesSkipped = 1; // we read one byte
				}
			}
			totalBytesSkipped += bytesSkipped;
		}
		return totalBytesSkipped;
	}
}
