package android.rest.client.request.json;



import org.springframework.web.client.RestTemplate;

import android.content.Context;

import com.octo.android.rest.client.persistence.DataPersistenceManager;
import com.octo.android.rest.client.request.CachedContentRequest;

public abstract class CachedRestContentRequest<RESULT> extends CachedContentRequest<RESULT> {

	private RestTemplateFactory restTemplateFactory;

	public CachedRestContentRequest( Context context, Class<RESULT> clazz, DataPersistenceManager persistenceManager, RestTemplateFactory restTemplateFactory) {
		super(context, clazz, persistenceManager);
		this.restTemplateFactory = restTemplateFactory;
	}

	protected RestTemplate getRestTemplate() {
		return restTemplateFactory.createRestTemplate();
	}

}