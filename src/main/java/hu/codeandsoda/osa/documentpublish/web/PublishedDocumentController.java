package hu.codeandsoda.osa.documentpublish.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import hu.codeandsoda.osa.account.user.data.UserMenuData;
import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.collection.data.CollectionsData;
import hu.codeandsoda.osa.collection.sevice.CollectionService;
import hu.codeandsoda.osa.document.service.DocumentTagService;
import hu.codeandsoda.osa.documentfilter.data.DocumentFilterName;
import hu.codeandsoda.osa.documentfilter.data.DocumentFilterTypesData;
import hu.codeandsoda.osa.documentfilter.service.DocumentFilterService;
import hu.codeandsoda.osa.documentpublish.data.PublicArchivePageData;
import hu.codeandsoda.osa.documentpublish.data.PublicArchivePageFilteringRequest;
import hu.codeandsoda.osa.documentpublish.data.PublishedDocumentAnnotationRequestData;
import hu.codeandsoda.osa.documentpublish.data.PublishedDocumentData;
import hu.codeandsoda.osa.documentpublish.data.PublishedDocumentsData;
import hu.codeandsoda.osa.documentpublish.service.PublishedDocumentService;
import hu.codeandsoda.osa.documentpublish.validator.PublishedDocumentHashValidator;
import hu.codeandsoda.osa.pagination.data.PaginationRequest;
import hu.codeandsoda.osa.security.service.UserService;
import hu.codeandsoda.osa.sort.data.SortingRequest;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
public class PublishedDocumentController {

    public static final String PUBLISHED_DOCUMENTS_URL ="/";
    public static final String PUBLISHED_DOCUMENT_URL = "/publication/{hash}";
    
    private static final String PUBLISHED_DOCUMENTS_TEMPLATE = "public_archives";
    private static final String PUBLISHED_DOCUMENT_TEMPLATE = "public_document";

    private static final String LOAD_PUBLISHED_DOCUMENTS_NOTES = "Returned model attributes: DocumentFilterTypesData as documentFilterTypesData, PublishedDocumentsData as publishedDocuments, CollectionsData as collectionsData, String List as activeTags, String as searchTerm. See attributes under 4. Definitions.";
    private static final String LOAD_PUBLISHED_DOCUMENT_NOTES = "Returned model attributes: PublishedDocumentData as publishedDocument, CollectionsData as collectionsData.. See attributes under 4. Definitions.";

    @Autowired
    private PublishedDocumentService publishedDocumentService;

    @Autowired
    private DocumentFilterService documentFilterService;

    @Autowired
    private DocumentTagService documentTagService;

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private UserService userService;

    @Autowired
    private PublishedDocumentHashValidator publishedDocumentHashValidator;

    @ApiOperation(value = "Load published documents", notes = LOAD_PUBLISHED_DOCUMENTS_NOTES)
    @ApiResponses({
            @ApiResponse(code = 200, message = LOAD_PUBLISHED_DOCUMENTS_NOTES, response = DocumentFilterTypesData.class),
            @ApiResponse(code = 200, message = LOAD_PUBLISHED_DOCUMENTS_NOTES, response = PublishedDocumentsData.class),
            @ApiResponse(code = 200, message = LOAD_PUBLISHED_DOCUMENTS_NOTES, response = CollectionsData.class) })
    @GetMapping(PUBLISHED_DOCUMENTS_URL)
    public ModelAndView loadPublishedDocuments(Authentication authentication, @RequestParam(defaultValue = "DESC") String sort, @RequestParam(defaultValue = "DATE") String sortBy,
            @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "0") int page, @RequestParam(required = false) List<DocumentFilterName> type,
            @RequestParam(required = false) List<DocumentFilterName> language, @RequestParam(required = false) List<DocumentFilterName> country,
            @RequestParam(required = false) Integer periodFrom, @RequestParam(required = false) Integer periodTo, @RequestParam(required = false) List<String> tag,
            @RequestParam(required = false) String searchTerm) {

        ModelAndView mav = loadTemplate(PUBLISHED_DOCUMENTS_TEMPLATE, authentication);
        mav.addObject("searchTerm", searchTerm);


        PublicArchivePageFilteringRequest publicArchivePageFilteringRequest = new PublicArchivePageFilteringRequest();
        documentFilterService.collectAndValidateFilters(publicArchivePageFilteringRequest, type, language, country, periodFrom, periodTo);
        documentTagService.collectAndValidateActiveTags(publicArchivePageFilteringRequest, tag);

        SortingRequest sortingRequest = new SortingRequest.SortingRequestBuilder().setSort(sort).setSortBy(sortBy).build();
        PaginationRequest paginationRequest = new PaginationRequest.PaginationRequestBuilder().setPage(page).setSize(size).build();
        PublicArchivePageData publicArchivePageData = publishedDocumentService.loadPublicArchivePageData(publicArchivePageFilteringRequest, sortingRequest, paginationRequest,
                searchTerm);
        mav.addObject("publishedDocuments", publicArchivePageData.getPublishedDocuments());
        mav.addObject("documentFilterTypesData", publicArchivePageData.getDocumentFilterTypesData());

        List<String> activeTagNames = documentTagService.collectTagNames(publicArchivePageFilteringRequest.getActiveTags());
        mav.addObject("activeTags", activeTagNames);

        return mav;
    }

    @ApiOperation(value = "Load published document by Hash", notes = LOAD_PUBLISHED_DOCUMENT_NOTES)
    @ApiResponses({ @ApiResponse(code = 200, message = LOAD_PUBLISHED_DOCUMENT_NOTES, response = PublishedDocumentData.class),
            @ApiResponse(code = 200, message = LOAD_PUBLISHED_DOCUMENT_NOTES, response = CollectionsData.class) })
    @GetMapping(PUBLISHED_DOCUMENT_URL)
    public ModelAndView loadPublishedDocument(Authentication authentication, @PathVariable String hash) {
        Errors errors = new BeanPropertyBindingResult(hash, "hash");
        publishedDocumentHashValidator.validate(hash, errors);

        ModelAndView mav = loadTemplate(PUBLISHED_DOCUMENT_TEMPLATE, authentication);

        if (!errors.hasErrors()) {
            User user = loadUser(authentication);
            PublishedDocumentData publishedDocument = publishedDocumentService.loadPublishedDocument(hash, user);
            mav.addObject("publishedDocument", publishedDocument);
        }

        return mav;
    }

    @ApiOperation(value = "Save annotation to published document", notes = LOAD_PUBLISHED_DOCUMENT_NOTES)
    @ApiResponses({ @ApiResponse(code = 200, message = LOAD_PUBLISHED_DOCUMENT_NOTES, response = PublishedDocumentData.class),
            @ApiResponse(code = 200, message = LOAD_PUBLISHED_DOCUMENT_NOTES, response = CollectionsData.class) })
    @PostMapping(PUBLISHED_DOCUMENT_URL)
    public ModelAndView saveAnnotation(Authentication authentication, @PathVariable String hash,
            @ModelAttribute PublishedDocumentAnnotationRequestData publishedDocumentAnnotationRequestData) {
        Errors errors = new BeanPropertyBindingResult(hash, "hash");
        publishedDocumentHashValidator.validate(hash, errors);

        ModelAndView mav = loadTemplate(PUBLISHED_DOCUMENT_TEMPLATE, authentication);

        if (!errors.hasErrors()) {
            User user = loadUser(authentication);
            publishedDocumentService.saveAnnotation(hash, user, publishedDocumentAnnotationRequestData.getAnnotation());

            PublishedDocumentData publishedDocument = publishedDocumentService.loadPublishedDocument(hash, user);
            mav.addObject("publishedDocument", publishedDocument);
        }

        return mav;
    }

    private ModelAndView loadTemplate(String templateName, Authentication authentication) {
        ModelAndView mav = new ModelAndView(templateName);

        UserMenuData userMenuData = userService.loadUserMenuData();
        if (null != userMenuData) {
            mav.addObject("userMenuData", userMenuData);
        }

        addCollectionsDataToModel(authentication, mav);

        return mav;
    }

    private void addCollectionsDataToModel(Authentication authentication, ModelAndView mav) {
        User user = loadUser(authentication);
        if (null != user) {
            Long userId = user.getId();
            CollectionsData collectionsData = collectionService.loadCollectionsDataByUser(userId);
            mav.addObject("collectionsData", collectionsData);
        }
    }

    private User loadUser(Authentication authentication) {
        User user = null != authentication ? (User) authentication.getPrincipal() : null;
        return user;
    }



}
