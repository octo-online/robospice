package com.octo.android.rest.client.webservice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import roboguice.RoboGuice;
import android.content.Context;

import com.google.inject.Inject;

import com.octo.android.rest.client.utils.EnvironmentConfigService;

/**
 * Call a given web service using spring android.
 * 
 * @author mwa
 * 
 */
public class WebService {
	// ============================================================================================
	// CONSTANTS
	// ============================================================================================
	/** Timeout when calling a web service (in ms). */
	private static final int WEBSERVICES_TIMEOUT = 30000;

	/**
	 * It also provides a list of urls to initate the various REST requests.
	 * 
	 * @author mwa
	 * 
	 */
	public static interface Urls {
		public static final String PRODUCT_CATALOG_CREDIT = "catalog/credit";
		public static final String PRODUCT_CATALOG_SAVING = "catalog/epargne";
		public static final String PRODUCT_CATALOG_INSURANCE = "catalog/assurance";
		public static final String PRODUCT_CATALOG_OFFER_DETAIL = "catalog/produit?code=%s";
		public static final String HOMEPAGE_BANNER = "catalog/homepage";

		public static final String REQUEST_STATUS = "prospect/creditRequestStatus?ref=%s&birthDate=%s&appCode=%s";

		public static final String CREDIT_PROJECT_SIMULATOR = "simulator/credits/projectlist";

		public static final String CALL_CENTER_TIMES = "contact/horaires";

		public static final String CREDIT_DISCLAIMER = "infos/creditdisclaimer";
		public static final String CNIL_LEGAL_MENTIONS = "infos/cnil";

		public static final String SAVING_CALCULATOR_CAPACITY = "calc/savingCapacity";
		public static final String SAVING_CALCULATOR_CAPACITY_SEND_MAIL = "calc/savingCapacity/mail";
		public static final String SAVING_CALCULATOR = "calc/savings";

		public static final String SAVING_PRODUCT_SIMULATOR = "simulator/savings/products";
		public static final String SAVING_SIMULATOR = "simulator/savings/json";
		public static final String SAVING_SIMULATOR_SEND_MAIL = "simulator/savings/json/mail";

		public static final String GEOLOC_TYPES = "geoloc/types";
		public static final String GEOLOC_FIND_CITY = "geoloc/findCities?nameAndCode=%s";
		public static final String GEOLOC_SEARCH = "geoloc/byPostalCodeCity";
		public static final String GEOLOC_POSITION_SEARCH = "geoloc/byPosition";

		public static final String NOTIFS_REGISTER = "notifications/register";
		public static final String NOTIFS_UPDATE = "notifications/update";
		public static final String NOTIFS_UNREGISTER = "notifications/unregister";

		public static final String MAIL_SENDER = "mail/generic";
		// TODO ask front services to rename service
		public static final String PROJECT_SUBSCRIPTION = "prospect/creditSubscription";
		public static final String OPTIN_SUBSCRIPTION = "prospect/optinSubscription";
	}

	// ============================================================================================
	// ATTRIBUTES
	// ============================================================================================

	protected Context mContext;
	protected RestTemplate mRestTemplate;
	private final String mBaseUrl;

	// ============================================================================================
	// CONSTRUCTOR
	// ============================================================================================
	@Inject
	public WebService(Context context) {
		mContext = context;
		EnvironmentConfigService environmentConfig = RoboGuice.getInjector(context).getInstance(EnvironmentConfigService.class);
		mBaseUrl = environmentConfig.getWebServiceUrl();

		mRestTemplate = new RestTemplate();

		// set timeout for requests
		HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		httpRequestFactory.setReadTimeout(WEBSERVICES_TIMEOUT);
		httpRequestFactory.setConnectTimeout(WEBSERVICES_TIMEOUT);
		mRestTemplate.setRequestFactory(httpRequestFactory);

		// web services support json responses
		final MappingJacksonHttpMessageConverter jsonConverter = new MappingJacksonHttpMessageConverter();
		final List<MediaType> supportedMediaTypes = new ArrayList<MediaType>();
		supportedMediaTypes.add(MediaType.APPLICATION_JSON);
		jsonConverter.setSupportedMediaTypes(supportedMediaTypes);

		FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
		StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();

		final List<HttpMessageConverter<?>> listHttpMessageConverters = mRestTemplate.getMessageConverters();
		listHttpMessageConverters.add(jsonConverter);
		listHttpMessageConverters.add(formHttpMessageConverter);
		listHttpMessageConverters.add(stringHttpMessageConverter);
		mRestTemplate.setMessageConverters(listHttpMessageConverters);
	}

	// ============================================================================================
	// METHODS
	// ============================================================================================

	protected String getBaseUrl() {
		return mBaseUrl;
	}

	protected RestTemplate getRestTemplate() {
		return mRestTemplate;
	}

}
