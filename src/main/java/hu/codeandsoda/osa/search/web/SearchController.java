package hu.codeandsoda.osa.search.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import hu.codeandsoda.osa.search.data.DocumentTagSearchResult;
import hu.codeandsoda.osa.search.service.DocumentTagIndexService;
import io.swagger.annotations.ApiOperation;

@RestController
public class SearchController {

    public static final String TAG_AUTOSUGGEST_URL = "/api/search/tag/{tagName}";

    @Autowired
    private DocumentTagIndexService documentTagIndexService;

    @ApiOperation(value = "Document Tag autosuggest", notes = "Document tag autosuggest expects at least 3 characters.")
    @GetMapping(TAG_AUTOSUGGEST_URL)
    public DocumentTagSearchResult autosuggestTag(@PathVariable String tagName) {
        return documentTagIndexService.autoSuggest(tagName);
    }

}
