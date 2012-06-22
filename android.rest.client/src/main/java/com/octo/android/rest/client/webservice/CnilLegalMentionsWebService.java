package com.octo.android.rest.client.webservice;

import android.content.Context;
import android.util.Log;

import com.octo.android.rest.client.contentservice.WebServiceException;

/**
 * Web service in charge of retrieving credit disclaimer
 * 
 * @author sni
 * 
 */
public class CnilLegalMentionsWebService extends WebService {

	public CnilLegalMentionsWebService(Context context) {
		super(context);
	}

	public String getCnilLegalMentionsText() throws WebServiceException {
		String url = getBaseUrl() + Urls.CNIL_LEGAL_MENTIONS;

		String cnilLegalMentions = null;
		try {
			Log.d(getClass().getName(),"Call web service " + url);
			cnilLegalMentions = getRestTemplate().getForObject(url, String.class);
		}
		catch (Exception e) {
			throw new WebServiceException(e);
		}

		return cnilLegalMentions;
	}
}
