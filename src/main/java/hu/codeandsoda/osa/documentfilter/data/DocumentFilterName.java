package hu.codeandsoda.osa.documentfilter.data;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import hu.codeandsoda.osa.documentfilter.service.DocumentFilterNameSerializer;

@JsonSerialize(using = DocumentFilterNameSerializer.class)
public enum DocumentFilterName {

    // DocumentFilterType.TYPE
    TEXT("Text"),
    IMAGE("Image"),

    // DocumentFilterType.LANGUAGE
    HUNGARIAN("Hungarian"),
    ENGLISH("English"),
    ROMANIAN("Romanian"),
    RUSSIAN("Russian"),
    POLISH("Polish"),
    CZECH("Czech"),
    GERMAN("German"),
    FRENCH("French"),
    BULGARIAN("Bulgarian"),
    PORTUGUESE_BRAZIL("Portuguese, Brazil"),
    ALBANIAN("Albanian"),
    SERBIAN("Serbian"),
    SERBO_CROATIAN("Serbo-Croatian"),
    SLOVAK("Slovak"),
    
    // DocumentFilterType.COUNTRY
    RUSSIAN_FEDERATION("Russian Federation"),
    ROMANIA("Romania"),
    HUNGARY("Hungary"),
    BELGIUM("Belgium"),
    POLAND("Poland"),
    UNITED_STATES("United States"),
    CZECH_REPUBLIC("Czech Republic"),
    GERMANY("Germany"),
    SLOVAKIA_SLOVAK_REPUBLIC("Slovakia (Slovak Republic)"),
    AUSTRIA("Austria"),
    TAJIKISTAN("Tajikistan"),
    BOSNIA_AND_HERZEGOVINA("Bosnia and Herzegovina"),
    BULGARIA("Bulgaria"),
    SERBIA("Serbia"),
    UNITED_KINGDOM("United Kingdom"),
    AFGHANISTAN("Afghanistan"),
    FRANCE("France"),
    ALBANIA("Albania"),
    CROATIA("Croatia"),
    LAO_PEOPLES_DEMOCRATIC_REPUBLIC("Lao People's Democratic Republic"),
    CAMBODIA("Cambodia"),
    VIETNAM("Vietnam"),
    ITALY("Italy"),
    KAZAKHSTAN("Kazakhstan"),
    TURKMENISTAN("Turkmenistan"),
    UZBEKISTAN("Uzbekistan"),
    AZERBAIJAN("Azerbaijan"),
    GREECE("Greece"),
    KYRGYZSTAN("Kyrgyzstan"),
    MACEDONIA_THE_FORMER_YUGOSLAV_REPUBLIC_OF("Macedonia, The Former Yugoslav Republic of"),
    BRAZIL("Brazil"),
    CUBA("Cuba"),
    EGYPT("Egypt"),
    SLOVENIA("Slovenia"),
    SWITZERLAND("Switzerland"),
    TURKEY("Turkey"),
    ARMENIA("Armenia"),
    ESTONIA("Estonia"),
    ISRAEL("Israel"),
    LATVIA("Latvia"),
    LITHUANIA("Lithuania"),
    SOUTH_AFRICA("South Africa"),
    THAILAND("Thailand"),
    AMERICAN_SAMOA("American Samoa"),
    BELARUS("Belarus"),
    BOLIVIA("Bolivia"),
    CANADA("Canada"),
    CHAD("Chad"),
    COLOMBIA("Colombia"),
    DOMINICAN_REPUBLIC("Dominican Republic"),
    ETHIOPIA("Ethiopia"),
    FINLAND("Finland"),
    GEORGIA("Georgia"),
    GUATEMALA("Guatemala"),
    INDONESIA("Indonesia"),
    IRAN_ISLAMIC_REPUBLIC_OF("Iran (Islamic Republic of)"),
    IRAQ("Iraq"),
    JAPAN("Japan"),
    LIBYAN_ARAB_JAMAHIRIYA("Libyan Arab Jamahiriya"),
    LUXEMBOURG("Luxembourg"),
    MOLDOVA_REPUBLIC_OF("Moldova, Republic of"),
    NETHERLANDS("Netherlands"),
    NORWAY("Norway"),
    PANAMA("Panama"),
    PERU("Peru"),
    PUERTO_RICO("Puerto Rico"),
    SWEDEN("Sweden"),
    UKRAINE("Ukraine"),
    VENEZUELA("Venezuela"),
    ZIMBABWE("Zimbabwe");

    private String displayName;

    private DocumentFilterName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
