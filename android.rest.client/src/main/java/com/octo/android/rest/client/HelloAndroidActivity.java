package com.octo.android.rest.client;

import roboguice.activity.RoboActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.octo.android.rest.client.contentmanager.ContentManager;
import com.octo.android.rest.client.contentmanager.RestRequest;
import com.octo.android.rest.client.contentmanager.listener.OnAbstractContentRequestFinishedListener;
import com.octo.android.rest.client.contentservice.AbstractContentService;
import com.octo.android.rest.client.custom.cnil.CnilLegalMentionsContentService;

public class HelloAndroidActivity extends RoboActivity {

	// ============================================================================================
	// ATTRIBUTES
	// ============================================================================================

	private TextView mCnilTextView;

	@Inject
	private ContentManager mContentManager;

	private final CnilRequest cnilRequest = new CnilRequest();

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

		requestCnilLegalMentions();
	}

	// ============================================================================================
	// PUBLIC METHODS
	// ============================================================================================

	public void requestCnilLegalMentions() {
		mContentManager.requestContentWithService(cnilRequest);
	}
	
	@Override
	protected void onPause() {
		mContentManager.removeOnRequestFinishedListener(cnilRequest);
		super.onPause();
	}

	// ============================================================================================
	// INNER CLASSES
	// ============================================================================================

	private final class CnilRequest extends RestRequest<String> {

		public CnilRequest() {
			super(0, CnilLegalMentionsContentService.class, null, true, false);
		}

		protected  void onRequestFailure(int resultCode) {
			Toast.makeText(HelloAndroidActivity.this, "failure", Toast.LENGTH_SHORT);
		}

		protected void onRequestSuccess(String result) {
			Toast.makeText(HelloAndroidActivity.this, "success", Toast.LENGTH_SHORT);
			mCnilTextView.setText( result );
		}
	}
}

