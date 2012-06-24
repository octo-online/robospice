package com.octo.android.rest.client;

import org.springframework.web.client.RestClientException;

import roboguice.activity.RoboActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.octo.android.rest.client.contentmanager.AbstractContentManager;
import com.octo.android.rest.client.contentmanager.RestRequest;
import com.octo.android.rest.client.utils.EnvironmentConfigService;
import com.octo.android.rest.client.webservice.WebService;
import com.octo.android.rest.client.webservice.WebService.Urls;

public class HelloAndroidActivity extends RoboActivity {

	// ============================================================================================
	// ATTRIBUTES
	// ============================================================================================

	private TextView mCnilTextView;

	@Inject
	private AbstractContentManager mContentManager;

	@Inject
	EnvironmentConfigService environmentConfigService;
	
	private CnilRequest cnilRequest;
	
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

		cnilRequest = new CnilRequest(this);

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

	private static final class CnilRequest extends RestRequest<HelloAndroidActivity, String> {

		private static final long serialVersionUID = -1578679537677496271L;

		public CnilRequest( HelloAndroidActivity activity) {
			super( activity, null, true, false);
		}
		
		//can't use activity here or any non serializable field
		//will be invoked in remote service
		@Override
		public String loadDataFromNetwork(WebService webService) throws RestClientException {
				String url = webService.getBaseUrl() + Urls.CNIL_LEGAL_MENTIONS;
				Log.d(getClass().getName(),"Call web service " + url);
				return webService.getRestTemplate().getForObject(url, String.class);
		}
		
		//can't use activity here or any non serializable field
		//will be invoked in remote service
		@Override
		public String getCacheKey() {
			return "cnil";
		}
		
		@Override
		protected void onRequestFailure( int resultCode) {
			Toast.makeText(getActivity(), "failure", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onRequestSuccess( String result) {
			Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).show();
			getActivity().mCnilTextView.setText( result );
		}
	}
}

