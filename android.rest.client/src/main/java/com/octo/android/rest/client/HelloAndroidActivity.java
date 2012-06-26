package com.octo.android.rest.client;

import org.springframework.web.client.RestClientException;

import roboguice.activity.RoboActivity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.octo.android.rest.client.contentmanager.ContentManager;
import com.octo.android.rest.client.contentmanager.AbstractImageRequest;
import com.octo.android.rest.client.contentmanager.AbstractTextRequest;
import com.octo.android.rest.client.contentmanager.RestRequest;
import com.octo.android.rest.client.model.ClientRequestStatus;
import com.octo.android.rest.client.utils.EnvironmentConfigService;
import com.octo.android.rest.client.webservice.WebService;
import com.octo.android.rest.client.webservice.WebService.Urls;

public class HelloAndroidActivity extends RoboActivity {

	// ============================================================================================
	// ATTRIBUTES
	// ============================================================================================

	private TextView mCnilTextView;
	private TextView mCreditStatusTextView;
	TextView mImageTextView;

	@Inject
	private ContentManager mContentManager;

	@Inject
	EnvironmentConfigService environmentConfigService;

	private CnilRequest cnilRequest;
	private CreditStatusRequest creditStatusRequest;
	private ImageRequest imageRequest;

	// ============================================================================================
	// ACITVITY LIFE CYCLE
	// ============================================================================================

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		mCnilTextView = (TextView) findViewById(R.id.textview_hello_cnil);
		mCreditStatusTextView = (TextView) findViewById(R.id.textview_hello_credit_status);
		mImageTextView = (TextView) findViewById(R.id.textview_hello_image);

		// Initializes the logging
		// Log a message (only on dev platform)
		Log.i(getClass().getName(), "onCreate");

		cnilRequest = new CnilRequest(this, environmentConfigService.getWebServiceUrl() + Urls.CNIL_LEGAL_MENTIONS);
		creditStatusRequest = new CreditStatusRequest(this, "12345678",
				"19/12/1976");
		imageRequest = new ImageRequest(this, "https://developers.google.com/images/developers-logo.png");

		//requestCnilLegalMentions();
		//requestCreditStatus();
		requestImage();
	}

	// ============================================================================================
	// PUBLIC METHODS
	// ============================================================================================

	public void requestCnilLegalMentions() {
		mContentManager.requestContentWithService(cnilRequest);
	}

	public void requestCreditStatus() {
		mContentManager.requestContentWithService(creditStatusRequest);
	}

	public void requestImage() {
		mContentManager.requestContentWithService(imageRequest);
	}

	@Override
	protected void onPause() {
		mContentManager.removeOnRequestFinishedListener(cnilRequest);
		super.onPause();
	}

	// ============================================================================================
	// OUTER CLASSES
	// ============================================================================================

	private static final class CnilRequest extends
	AbstractTextRequest<HelloAndroidActivity> {

		private static final long serialVersionUID = -1578679537677496271L;

		public CnilRequest(HelloAndroidActivity activity, String url) {
			super(activity, url);
		}

		@Override
		protected void onRequestFailure(int resultCode) {
			Toast.makeText(getActivity(), "failure", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onRequestSuccess(String result) {
			Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).show();
			String originalText = getActivity().mCnilTextView.getText()
					.toString();
			getActivity().mCnilTextView.setText(originalText + result);
		}
	}

	private static final class CreditStatusRequest extends
	RestRequest<HelloAndroidActivity, ClientRequestStatus> {

		private static final long serialVersionUID = -3578679537677496761L;
		private final static String BUNDLE_EXTRA_REQUEST_ID = "BUNDLE_EXTRA_REQUEST_ID";
		private final static String BUNDLE_EXTRA_BIRTH_DATE = "BUNDLE_EXTRA_BIRTH_DATE";

		private String requestId;
		private String birthDate;

		public CreditStatusRequest(HelloAndroidActivity activity,
				String requestId, String birthDate) {
			super(activity, false, false);
			this.requestId = requestId;
			this.birthDate = birthDate;
			getBundle().putString(BUNDLE_EXTRA_REQUEST_ID, requestId);
			getBundle().putString(BUNDLE_EXTRA_BIRTH_DATE, birthDate);
		}

		// can't use activity here or any non serializable field
		// will be invoked in remote service
		@Override
		public ClientRequestStatus loadDataFromNetwork(WebService webService,
				Bundle bundle) throws RestClientException {
			String requestId = bundle.getString(BUNDLE_EXTRA_REQUEST_ID);
			String birthDate = bundle.getString(BUNDLE_EXTRA_BIRTH_DATE);
			String url = webService.getBaseUrl()
					+ String.format(WebService.Urls.REQUEST_STATUS, requestId,
							birthDate.replaceAll("/", ""), "FRANDROIDBK");
			Log.d(getClass().getName(), "Call web service " + url);
			return webService.getRestTemplate().getForObject(url,
					ClientRequestStatus.class);
		}

		// can't use activity here or any non serializable field
		// will be invoked in remote service
		@Override
		public String getCacheKey() {
			return "credtiRequestStatus" + requestId + "-"
					+ birthDate.hashCode();
		}

		@Override
		protected void onRequestFailure(int resultCode) {
			Toast.makeText(getActivity(), "failure", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onRequestSuccess(ClientRequestStatus result) {
			Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).show();
			String originalText = getActivity().mCreditStatusTextView.getText()
					.toString();
			getActivity().mCreditStatusTextView.setText(originalText
					+ result.getStatus().toString());
		}
	}

	private static final class ImageRequest extends
	AbstractImageRequest<HelloAndroidActivity> {

		private static final long serialVersionUID = 7177091635547038422L;

		public ImageRequest(HelloAndroidActivity activity, String url) {
			super(activity, url);
		}
		
		@Override
		protected void onRequestFailure(int resultCode) {
			Toast.makeText(getActivity(), "failure", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onRequestSuccess(Drawable result) {
			getActivity().mImageTextView.setBackgroundDrawable(result);
			getActivity().mImageTextView.setText("");
		}
	}
}

