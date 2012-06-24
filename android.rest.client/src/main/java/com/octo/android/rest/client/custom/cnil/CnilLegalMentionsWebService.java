package com.octo.android.rest.client.custom.cnil;


///**
// * Web service in charge of retrieving credit disclaimer
// * 
// * @author sni
// * 
// */
//public class CnilLegalMentionsWebService extends WebService<String> {
//
//	private static final long serialVersionUID = 1L;
//
//	public CnilLegalMentionsWebService( String baseUrl ) {
//		super( baseUrl);
//	}
//
//	public String loadDataFromNetwork() throws WebServiceException {
//		String url = getBaseUrl() + Urls.CNIL_LEGAL_MENTIONS;
//
//		try {
//			Log.d(getClass().getName(),"Call web service " + url);
//			return getRestTemplate().getForObject(url, String.class);
//		}
//		catch (Exception e) {
//			throw new WebServiceException(e);
//		}
//	}
//}
