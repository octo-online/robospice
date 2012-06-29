package com.octo.android.rest.client;

import org.springframework.web.client.RestClientException;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.octo.android.rest.client.contentmanager.AbstractImageRequest;
import com.octo.android.rest.client.contentmanager.AbstractTextRequest;
import com.octo.android.rest.client.contentmanager.RestRequest;
import com.octo.android.rest.client.model.ClientRequestStatus;
import com.octo.android.rest.client.utils.EnvironmentConfigService;
import com.octo.android.rest.client.webservice.UrlConstants;

@ContentView(R.layout.main)
public class HelloAndroidActivity extends RestClientActivity {

	// ============================================================================================
	// ATTRIBUTES
	// ============================================================================================


	@InjectView(R.id.textview_hello_cnil)
	private TextView mCnilTextView;
	@InjectView(R.id.textview_hello_credit_status)
	private TextView mCreditStatusTextView;
	@InjectView(R.id.textview_hello_image)
	private TextView mImageTextView;

	@Inject
	EnvironmentConfigService environmentConfigService;

	CnilRequest cnilRequest;
	ImageRequest imageRequest;
	CreditStatusRequest creditStatusRequest;

	// ============================================================================================
	// ACITVITY LIFE CYCLE
	// ============================================================================================

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initializes the logging
		// Log a message (only on dev platform)
		Log.i(getClass().getName(), "onCreate");

		String baseUrl = environmentConfigService.getWebServiceUrl() ;
		cnilRequest = new CnilRequest( baseUrl + UrlConstants.CNIL_LEGAL_MENTIONS);
		creditStatusRequest = new CreditStatusRequest(baseUrl, "12345678", "19/12/1976");
		imageRequest = new ImageRequest("http://media.xda-developers.com/images/icons/breadcrumbs-home.png");

	}

	@Override
	protected void onResume() {
		super.onResume();
		addRequestToQueue(cnilRequest);
		addRequestToQueue(creditStatusRequest);
		addRequestToQueue(imageRequest);
	}


	// ============================================================================================
	// INNER CLASSES
	// ============================================================================================

	private final class CnilRequest extends
	AbstractTextRequest {

		public CnilRequest(String url) {
			super(HelloAndroidActivity.this, url);
		}

		@Override
		protected void onRequestFailure(int resultCode) {
			Toast.makeText(HelloAndroidActivity.this, "failure", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onRequestSuccess(final String result) {
			Toast.makeText(HelloAndroidActivity.this, "success", Toast.LENGTH_SHORT).show();
			String originalText = mCnilTextView.getText()
					.toString();
			mCnilTextView.setText(originalText + result);		
		}
	}

	private final class CreditStatusRequest extends RestRequest< ClientRequestStatus> {

		private String requestId;
		private String birthDate;
		private String baseUrl;

		public CreditStatusRequest(String url,
				String requestId, String birthDate) {
			super(HelloAndroidActivity.this, ClientRequestStatus.class);
			this.baseUrl = url;
			this.requestId = requestId;
			this.birthDate = birthDate;
		}

		// can't use activity here or any non serializable field
		// will be invoked in remote service
		@Override
		public ClientRequestStatus loadDataFromNetwork() throws RestClientException {
			String url = this.baseUrl
					+ String.format(UrlConstants.REQUEST_STATUS, requestId,
							birthDate.replaceAll("/", ""), "FRANDROIDBK");
			Log.d(getClass().getName(), "Call web service " + url);
			return getRestTemplate().getForObject(url,
					ClientRequestStatus.class);
		}

		// can't use activity here or any non serializable field
		// will be invoked in remote service
		@Override
		public String getCacheKey() {
			return "credtiRequestStatus-" + requestId + "-"
					+ birthDate.hashCode();
		}

		@Override
		protected void onRequestFailure(int resultCode) {
			Toast.makeText(HelloAndroidActivity.this, "failure", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onRequestSuccess(final ClientRequestStatus result) {
			Toast.makeText(HelloAndroidActivity.this, "success", Toast.LENGTH_SHORT).show();
			String originalText = mCreditStatusTextView.getText()
					.toString();
			mCreditStatusTextView.setText(originalText
					+ result.getStatusDescription().toString());
		}
	}

	private final class ImageRequest extends AbstractImageRequest {

		public ImageRequest(String url) {
			super(HelloAndroidActivity.this, url);
		}

		@Override
		protected void onRequestFailure(int resultCode) {
			Toast.makeText(HelloAndroidActivity.this, "failure", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onRequestSuccess(final Drawable result) {
			Toast.makeText(HelloAndroidActivity.this, "success", Toast.LENGTH_SHORT).show();
			mImageTextView.setBackgroundDrawable(result);
			mImageTextView.setText("");
		}
	}

}

