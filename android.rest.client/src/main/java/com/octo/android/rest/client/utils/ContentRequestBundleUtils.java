package com.octo.android.rest.client.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.apache.commons.io.IOUtils;

import android.os.Bundle;
import com.octo.android.rest.client.contentservice.JsonContentService;

public class ContentRequestBundleUtils {

	// ============================================================================================
	// CONSTANTS
	// ============================================================================================

	public static final String BUNDLE_RESULT = "bundle_result_key";
	public static final String BUNDLE_RESULT_CODE = "bundle_result_code_key";
	private static final String BUNDLE_IS_INPUTSTREAM = "bundle_is_inputstream";

	// ============================================================================================
	// CONSTRUCTORS
	// ============================================================================================

	/**
	 * Prevent instantiation of utility class.
	 */
	private ContentRequestBundleUtils() {
	}

	// ============================================================================================
	// METHODS
	// ============================================================================================

	public static Object getResultFromBundle(Bundle bundle) throws Exception {
		byte[] bytes = bundle.getByteArray(BUNDLE_RESULT);
		boolean isInputStream = bundle.getBoolean(BUNDLE_IS_INPUTSTREAM);
		return ContentRequestBundleUtils.convertBytesArrayIntoObject(bytes, isInputStream);
	}

	public static int getResultCodeFromBundle(Bundle bundle) {
		return bundle.getInt(BUNDLE_RESULT_CODE, JsonContentService.RESULT_ERROR);
	}

	public static void setResultObjectInBundle(Bundle bundle, Object object) throws IOException {
		byte[] bytes = ContentRequestBundleUtils.convertObjectIntoBytesArray(object);
		bundle.putByteArray(BUNDLE_RESULT, bytes);
		if (object instanceof InputStream) {
			bundle.putBoolean(BUNDLE_IS_INPUTSTREAM, true);
		}
	}

	public static void setResultCodeInBundle(Bundle bundle, int code) {
		bundle.putInt(BUNDLE_RESULT_CODE, code);
	}

	public static void setResultCodeAndObjectInBundle(Bundle bundle, int code, Object object) throws IOException {
		ContentRequestBundleUtils.setResultCodeInBundle(bundle, code);
		ContentRequestBundleUtils.setResultObjectInBundle(bundle, object);
	}

	private static byte[] convertObjectIntoBytesArray(Object object) throws IOException {
		byte[] bytes = null;

		if (object instanceof InputStream) {
			bytes = IOUtils.toByteArray((InputStream) object);
		}
		else if (object instanceof Serializable) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = new ObjectOutputStream(bos);
			out.writeObject(object);
			bytes = bos.toByteArray();
			// TODO : add finally to close out and bos
			out.close();
			bos.close();
		}
		else {
			throw new IOException("Unable to convert object into byte array because not serializable");
		}

		return bytes;
	}

	private static Object convertBytesArrayIntoObject(byte[] bytes, boolean isInputStream) throws Exception {

		// TODO : add finally to close bis and in
		Object object = null;
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);

		if (isInputStream) {
			return bis;
		}
		else {
			ObjectInput in = new ObjectInputStream(bis);
			object = in.readObject();

			bis.close();
			in.close();
		}

		return object;
	}
}
