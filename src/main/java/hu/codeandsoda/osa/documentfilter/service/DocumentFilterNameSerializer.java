package hu.codeandsoda.osa.documentfilter.service;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import hu.codeandsoda.osa.documentfilter.data.DocumentFilterName;

public class DocumentFilterNameSerializer extends StdSerializer<DocumentFilterName> {

    public DocumentFilterNameSerializer() {
        super(DocumentFilterName.class);
    }

    public DocumentFilterNameSerializer(Class t) {
        super(t);
    }

    @Override
    public void serialize(DocumentFilterName documentFilterName, JsonGenerator generator, SerializerProvider provider) throws IOException, JsonProcessingException {
        generator.writeStartObject();
        generator.writeFieldName("name");
        generator.writeString(documentFilterName.name());
        generator.writeFieldName("displayName");
        generator.writeString(documentFilterName.getDisplayName());
        generator.writeEndObject();
    }

}
