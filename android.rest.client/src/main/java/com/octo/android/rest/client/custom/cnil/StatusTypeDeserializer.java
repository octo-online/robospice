package com.octo.android.rest.client.custom.cnil;

import java.io.IOException;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

public class StatusTypeDeserializer extends JsonDeserializer<ClientStatusType> {

	@Override
	public ClientStatusType deserialize(JsonParser parser, DeserializationContext ctx) throws IOException, JsonProcessingException {
		return ClientStatusType.fromString(parser.getText());
	}

}
