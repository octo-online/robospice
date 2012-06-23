package com.octo.android.rest.client.contentmanager;


import com.google.inject.Inject;
import com.google.inject.Singleton;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;


/**
 * Class used to manage content received from web service. <br>
 * This class extends {@link AbstractContentManager} (technical) and manage only functional request
 * 
 * @author jva
 * 
 */
@Singleton
public class ContentManager extends CommonContentManager {

	// ============================================================================================
	// CONSTANTS
	// ============================================================================================

	private static final int SERVICE_TYPE_CREDIT_CATALOG = 1;
	private static final int SERVICE_TYPE_SAVING_CATALOG = 2;
	private static final int SERVICE_TYPE_INSURANCE_CATALOG = 3;
	private static final int SERVICE_TYPE_REQUEST_STATUS = 4;
	private static final int SERVICE_TYPE_HOMEPAGE_BANNER = 5;
	private static final int SERVICE_TYPE_CREDIT_SIMULATOR_PROJECTS = 6;
	private static final int SERVICE_TYPE_CALL_CENTER_AND_CALL_BACK_TIMES = 7;
	private static final int SERVICE_TYPE_PRODUCT_CATALOG_OFFER_DETAIL = 8;
	private static final int SERVICE_TYPE_SAVING_CALCULATOR = 9;
	private static final int SERVICE_TYPE_GEOLOC_TYPES = 10;
	private static final int SERVICE_TYPE_SAVING_CAPACITY = 11;
	private static final int SERVICE_TYPE_SAVING_CAPACITY_SEND_MAIL = 12;
	private static final int SERVICE_TYPE_SAVING_SIMULATOR_PRODUCTS = 13;
	private static final int SERVICE_TYPE_SAVING_SIMULATOR = 14;
	private static final int SERVICE_TYPE_SAVING_SIMULATOR_SEND_MAIL = 15;
	private static final int SERVICE_TYPE_GEOLOC_SEARCH_CITIES = 16;
	private static final int SERVICE_TYPE_PROJECT_SUBSCRIPTION = 17;
	private static final int SERVICE_TYPE_GEOLOC_SEARCH_POIS = 18;
	private static final int SERVICE_TYPE_CREDIT_DISCLAIMER = 19;
	private static final int SERVICE_TYPE_CNIL_LEGAL_MENTIONS = 20;
	private static final int SERVICE_TYPE_REGISTER_NOTIFICATION = 21;
	private static final int SERVICE_TYPE_UNREGISTER_NOTIFICATION = 22;
	private static final int SERVICE_TYPE_OPTIN_OPTOUT = 23;

	// ============================================================================================
	// CONSTRUCTOR
	// ============================================================================================

	/**
	 * Creates a ContentManager.
	 * 
	 * @param context
	 *            the context in which the content manager operates.
	 */
	@Inject
	public ContentManager(Application application) {
		super(application);
	}

}
