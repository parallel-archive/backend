package hu.codeandsoda.osa.documentfilter.service;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import hu.codeandsoda.osa.documentfilter.data.DocumentFilterTypeName;

public class DocumentFilterTypeNameSerializer extends StdSerializer<DocumentFilterTypeName> {

    public DocumentFilterTypeNameSerializer() {
        super(DocumentFilterTypeName.class);
    }

    public DocumentFilterTypeNameSerializer(Class t) {
        super(t);
    }

    @Override
    public void serialize(DocumentFilterTypeName documentFilterTypeName, JsonGenerator generator, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        generator.writeStartObject();
        generator.writeFieldName("name");
        generator.writeString(documentFilterTypeName.name());
        generator.writeFieldName("displayName");
        generator.writeString(documentFilterTypeName.getDisplayName());
        generator.writeEndObject();
    }

}
