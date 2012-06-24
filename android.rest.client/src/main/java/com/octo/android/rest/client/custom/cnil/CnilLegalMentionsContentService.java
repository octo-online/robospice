//package com.octo.android.rest.client.custom.cnil;
//
//import android.os.Bundle;
//
//import com.octo.android.rest.client.contentservice.SimpleTextContentService;
//import com.octo.android.rest.client.contentservice.WebServiceException;
//
///**
// * Content service used to get cnil legal mentions text
// * 
// * @author sni
// * 
// */
//public class CnilLegalMentionsContentService extends SimpleTextContentService {
//
//	// ============================================================================================
//	// CONSTANTS
//	// ============================================================================================
//	private static final String CNIL_LEGAL_MENTIONS_CACHE_KEY = "CNIL_LEGAL_MENTIONS_CACHE_KEY";
//
//	// ============================================================================================
//	// CONSTRUCTOR
//	// ============================================================================================
//	public CnilLegalMentionsContentService() {
//		super("CnilLegalMentionsContentService");
//	}
//
//	// ============================================================================================
//	// METHODS
//	// ============================================================================================
//	@Override
//	public String loadDataFromNetwork(Bundle bundle) throws WebServiceException {
//		CnilLegalMentionsWebService webService = new CnilLegalMentionsWebService(this);
//		return webService.getCnilLegalMentionsText();
//	}
//
//	@Override
//	protected String getCacheKey(Bundle extraBundle) {
//		return CNIL_LEGAL_MENTIONS_CACHE_KEY;
//	}
//}
