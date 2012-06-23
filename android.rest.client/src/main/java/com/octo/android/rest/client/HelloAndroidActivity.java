package com.octo.android.rest.client;

import roboguice.activity.RoboActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.octo.android.rest.client.contentmanager.ContentManager;
import com.octo.android.rest.client.contentmanager.listener.OnAbstractContentRequestFinishedListener;
import com.octo.android.rest.client.contentservice.AbstractContentService;
import com.octo.android.rest.client.contentservice.CnilLegalMentionsContentService;

public class HelloAndroidActivity extends RoboActivity {

	// ============================================================================================
	// ATTRIBUTES
	// ============================================================================================
	
	private TextView mCnilTextView;
	
	@Inject
	private ContentManager mContentManager;

	// ============================================================================================
	// ACITVITY LIFE CYCLE
	// ============================================================================================
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		mCnilTextView = (TextView) findViewById(R.id.textview_hello_cnil);
		// Initializes the logging
		// Log a message (only on dev platform)
		Log.i(getClass().getName(), "onCreate");

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
					mCnilTextView.setText((String) result );
				} else {
					Toast.makeText(HelloAndroidActivity.this, "failure", Toast.LENGTH_SHORT);
				}
			}
		}
	}

}

