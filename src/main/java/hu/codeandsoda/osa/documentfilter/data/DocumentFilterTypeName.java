package hu.codeandsoda.osa.documentfilter.data;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import hu.codeandsoda.osa.documentfilter.service.DocumentFilterTypeNameSerializer;

@JsonSerialize(using = DocumentFilterTypeNameSerializer.class)
public enum DocumentFilterTypeName {

    TYPE("Type"),
    LANGUAGE("Language"),
    COUNTRY("Country");

    private String displayName;

    private DocumentFilterTypeName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
