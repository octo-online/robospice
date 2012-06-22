package com.octo.android.rest.client.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Date;

import org.apache.commons.io.IOUtils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

/**
 * Util class to work with files (external storage or application storage)
 * 
 * @author jva
 * 
 */
public class CacheFileUtils {

	// ============================================================================================
	// CONSTANTS
	// ============================================================================================

	private static final String FILE_CACHE_ENCODING = "UTF-8";
	private static final String LOGCAT_TAG = "CacheFileUtils";

	// ============================================================================================
	// CONSTRUCTORS
	// ============================================================================================

	/**
	 * Prevent instanciation of utility class.
	 */
	private CacheFileUtils() {
	}

	// ============================================================================================
	// METHODS
	// ============================================================================================

	/**
	 * Verify if we can write on external storage (sd card)
	 * 
	 * @return
	 */
	public static String checkExternalStorage() {

		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;

		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		}
		else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		}
		else {
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}

		if (!mExternalStorageAvailable || !mExternalStorageWriteable) {
			return "Unable to write to sdcard";
		}
		return null;
	}

	/**
	 * Open file in cache and determine its last modification date
	 * 
	 * @param context
	 * @param filename
	 * @return Date
	 */
	public static Date getModifiedDateForFile(Context context, String filename) {

		File file = new File(context.getCacheDir(), filename);
		if (file.exists()) {
			return new Date(file.lastModified());
		}
		return null;
	}

	/**
	 * Read some content from local application file
	 * 
	 * @param context
	 * @param filename
	 * @return read inputStream
	 */
	public static InputStream readInputStreamFromFile(Context context, String filename) {
		FileInputStream stream = null;
		try {
			Log.e(LOGCAT_TAG, "Reading file " + filename);
			File file = new File(context.getCacheDir(), filename);
			stream = new FileInputStream(file);
		}
		catch (FileNotFoundException fnfe) {
			Log.e(LOGCAT_TAG,filename + " not found", fnfe);
		}
		catch (Exception e) {
			Log.e(LOGCAT_TAG,e.getMessage(),e);
		}
		return stream;
	}

	/**
	 * Write some content in local application file
	 * 
	 * @param context
	 * @param filename
	 * @return read inputStream
	 */
	public static void writeInputStreamToFile(Context context, String filename, InputStream dataInputStream) {
		Log.d(LOGCAT_TAG,"Writing file " + filename);

		File file = new File(context.getCacheDir(), filename);
		OutputStream outputStream;
		try {
			outputStream = new FileOutputStream(file);
			IOUtils.copy(dataInputStream, outputStream);
		}
		catch (FileNotFoundException e) {
			Log.e(LOGCAT_TAG,filename + " not found",e);
		}
		catch (IOException e) {
			Log.e(LOGCAT_TAG,e.getMessage(),e);
		}
	}

	/**
	 * Read inputstream from file and convert it to String
	 * 
	 * @param context
	 * @param filename
	 * @return String
	 */
	public static String readStringContentFromFile(Context context, String filename) {
		InputStream inputStream = CacheFileUtils.readInputStreamFromFile(context, filename);
		StringWriter writer = new StringWriter();
		String result = null;

		if (inputStream != null) {
			try {
				IOUtils.copy(inputStream, writer, FILE_CACHE_ENCODING);
				result = writer.toString();
			}
			catch (IOException e) {
				Log.e(LOGCAT_TAG,e.getMessage(),e);
			}
		}

		return result;
	}

	/**
	 * Save some content to an application file
	 * 
	 * @param context
	 * @param data
	 * @param filename
	 * @throws IOException
	 */
	public static void saveStringToFile(Context context, String data, String filename) throws IOException {
		InputStream inputStream = IOUtils.toInputStream(data);
		CacheFileUtils.writeInputStreamToFile(context, filename, inputStream);
	}

	public static void clearCache(Context context) {
		Log.e(LOGCAT_TAG,"Clear application cache");
		File cacheDir = context.getCacheDir();
		deleteDirRecursively(cacheDir);
	}

	private static void deleteDirRecursively(File cacheDir) {
		if (cacheDir != null && cacheDir.isDirectory()) {
			File[] files = cacheDir.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					deleteDirRecursively(file);
				}
				Log.d(LOGCAT_TAG,"Delete file " + file.getName());
				file.delete();
			}
		}
	}
}
