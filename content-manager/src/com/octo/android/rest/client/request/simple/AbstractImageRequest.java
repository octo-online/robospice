//package com.octo.android.rest.client.request.simple;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.MalformedURLException;
//import java.net.URL;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.util.Log;
//
//import com.octo.android.rest.client.persistence.DataPersistenceManager;
//import com.octo.android.rest.client.request.CachedContentRequest;
//
//
//public abstract class AbstractImageRequest extends
//CachedContentRequest<InputStream> {
//
//	protected static final String BUNDLE_EXTRA_IMAGE_URL = "BUNDLE_EXTRA_IMAGE_URL";
//	protected String url;
//
//	public AbstractImageRequest(Context context, String url, DataPersistenceManager persistenceManager) {
//		super(context, InputStream.class, persistenceManager);
//		this.url = url;
//	}
//
//	@Override
//	public final InputStream loadDataFromNetwork()
//			throws Exception {
//		try {
//			return new URL(url).openStream();
//		} catch (MalformedURLException e) {
//			Log.e(getClass().getName(), "Unable to create image URL");
//			return null;
//		} catch (IOException e) {
//			Log.e(getClass().getName(), "Unable to download image");
//			return null;
//		}
//	}
//
//	protected final String getUrl() {
//		return this.url;
//	}
//
//	@Override
//	public final String getCacheKey() {
//		return url.replace(":", "").replace("/", "_");
//	}
//
//	@Override
//	protected final void onRequestSuccess(InputStream result) {
//		Bitmap bitmap = BitmapFactory.decodeStream(result);
//		BitmapDrawable drawable = new BitmapDrawable(bitmap);
//		onRequestSuccess( drawable);
//	}
//
//	protected abstract void onRequestSuccess(Drawable result);
//
//}
//
