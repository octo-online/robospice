package com.octo.android.rest.client;

import roboguice.inject.InjectResource;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.rest.client.contentmanager.ContentManager;
import com.octo.android.rest.client.contentmanager.listener.OnAbstractContentRequestFinishedListener;
import com.octo.android.rest.client.contentservice.AbstractContentService;
import com.octo.android.rest.client.contentservice.CnilLegalMentionsContentService;

import de.akquinet.android.androlog.Log;

public class HelloAndroidActivity extends Activity {

	// ============================================================================================
	// ATTRIBUTES
	// ============================================================================================
	
	@InjectResource(R.id.textview_hello_cnil)
	private TextView cnilTextView;
	private ContentManager mContentManager;

	// ============================================================================================
	// ACITVITY LIFE CYCLE
	// ============================================================================================
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initializes the logging
		Log.init();

		// Log a message (only on dev platform)
		Log.i(this, "onCreate");

		setContentView(R.layout.main);

		int requestId = requestCnilLegalMentions();
		CnilRequestListener cnilRequestListener = new CnilRequestListener();
		mContentManager.addOnRequestFinishedListener( cnilRequestListener);
		cnilRequestListener.setRequestId(requestId);
	}

	// ============================================================================================
	// PUBLIC METHODS
	// ============================================================================================
	
	public int requestCnilLegalMentions() {
		return mContentManager.requestContentWithService(0, CnilLegalMentionsContentService.class, null, true, false);
	}

	// ============================================================================================
	// INNER CLASSES
	// ============================================================================================
	
	private final class CnilRequestListener extends OnAbstractContentRequestFinishedListener {
		public void onRequestFinished(int requestId, int resultCode, Object result) {
			if( requestId == getRequestId() ) {
				if( resultCode == AbstractContentService.RESULT_OK) {
					Toast.makeText(HelloAndroidActivity.this, "success", Toast.LENGTH_SHORT);
				} else {
					Toast.makeText(HelloAndroidActivity.this, "failure", Toast.LENGTH_SHORT);
				}
			}
		}
	}

}

