//package com.octo.android.rest.client.contentservice;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.MalformedURLException;
//import java.net.URL;
//
//import android.os.Bundle;
//import android.util.Log;
///**
// * A class dedicated to fetching images from a given url.
// * 
// * @author mwa
// * 
// */
//public class BitmapInputStreamContentService extends BinaryContentService {
//
//	// ============================================================================================
//	// CONSTANT
//	// ============================================================================================
//
//	public static final String BUNDLE_EXTRA_IMAGE_URL = "BUNDLE_EXTRA_IMAGE_URL";
//
//	// ============================================================================================
//	// CONSTRUCTOR
//	// ============================================================================================
//
//	public BitmapInputStreamContentService() {
//		super("BitmapImageContentService");
//	}
//
//	// ============================================================================================
//	// METHODS
//	// ============================================================================================
//
//	@Override
//	protected final InputStream loadDataFromNetwork(Bundle extraBundle) throws WebServiceException {
//		String urlString = extraBundle.getString(BUNDLE_EXTRA_IMAGE_URL);
//		if (urlString != null) {
//			URL url = null;
//			InputStream inputStream = null;
//			try {
//				url = new URL(urlString);
//				inputStream = url.openStream();
//			}
//			catch (MalformedURLException e) {
//				Log.e(getClass().getName(), "Unable to create image URL");
//			}
//			catch (IOException e) {
//				Log.e(getClass().getName(),"Unable to download image");
//			}
//			return inputStream;
//		}
//		return null;
//	}
//
//	@Override
//	protected final String getCacheKey(Bundle extraBundle) {
//		String urlString = extraBundle.getString(BUNDLE_EXTRA_IMAGE_URL);
//		if (urlString != null) {
//			urlString = urlString.replace(":", "_").replace("/", "_");
//		}
//		else {
//			urlString = "image";
//		}
//		return urlString;
//	}
//}
