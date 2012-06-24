//package com.octo.android.rest.client.contentmanager;
//
//import java.io.FilterInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//
///**
// * Able to load common content such as bitmaps.
// * 
// * @author stephanenicolas
// * 
// */
//public class CommonContentManager extends AbstractContentManager {
//
//	// ============================================================================================
//	// CONSTANT
//	// ============================================================================================
//	protected static final int SERVICE_TYPE_IMAGE_BITMAP = 100;
//
//	// ============================================================================================
//	// CONSTRUCTOR
//	// ============================================================================================
//	public CommonContentManager(Context context) {
//		super(context);
//	}
//
//	// ============================================================================================
//	// METHODS
//	// ============================================================================================
//	@Override
//	protected Object doProcessResultIfNeeded(Object result) {
//		// special case for images, convert inputstream to bitmap :
//		Bitmap bitmap = BitmapFactory.decodeStream(new FlushedInputStream((InputStream) result));
//		result = bitmap;
//		return result;
//	}
//
//	/**
//	 * Image Decoder Bug correction FilterInputStream. Permit to decode JPG image with BitmapFactory.decodeStream() method, what fails in Android 1.6.
//	 * 
//	 * @author octo_mwa
//	 * 
//	 */
//	static class FlushedInputStream extends FilterInputStream {
//		public FlushedInputStream(InputStream inputStream) {
//			super(inputStream);
//		}
//
//		@Override
//		public long skip(long n) throws IOException {
//			long totalBytesSkipped = 0L;
//			while (totalBytesSkipped < n) {
//				long bytesSkipped = in.skip(n - totalBytesSkipped);
//				if (bytesSkipped == 0L) {
//					int bytes = read();
//					if (bytes < 0) {
//						break; // we reached EOF
//					}
//					else {
//						bytesSkipped = 1; // we read one byte
//					}
//				}
//				totalBytesSkipped += bytesSkipped;
//			}
//			return totalBytesSkipped;
//		}
//	}
//
//}
