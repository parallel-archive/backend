package hu.codeandsoda.osa.collection.web;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import hu.codeandsoda.osa.account.user.data.UserMenuData;
import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.collection.data.CollectionData;
import hu.codeandsoda.osa.collection.data.CollectionRequestData;
import hu.codeandsoda.osa.collection.data.CollectionsData;
import hu.codeandsoda.osa.collection.domain.Collection;
import hu.codeandsoda.osa.collection.sevice.CollectionDataService;
import hu.codeandsoda.osa.collection.sevice.CollectionService;
import hu.codeandsoda.osa.collection.validator.CollectionRequestDataValidator;
import hu.codeandsoda.osa.documentpublish.data.PublishedDocumentsData;
import hu.codeandsoda.osa.pagination.data.PaginationRequest;
import hu.codeandsoda.osa.security.service.UserService;
import hu.codeandsoda.osa.sort.data.PublishedDocumentSortByField;
import hu.codeandsoda.osa.sort.data.SortingRequest;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
public class CollectionController {

    public static final String COLLECTION_URL = "/privatecollections";
    public static final String LOAD_COLLECTION_URL = "/privatecollections/{id}";
    public static final String PUBLISHED_DOCUMENT_COLLECTION_URL = "/privatecollections/mydocuments";

    private static final String COLLECTIONS_TEMPLATE = "collections";

    private static final String COLLECTION_PAGE_NOTES = "Returned model attributes: CollectionData as collectionData , CollectionsData as collectionsData, PublishedDocumentsData as collectionDocuments. See attributes under 4. Definitions.";

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private CollectionDataService collectionDataService;

    @Autowired
    private UserService userService;

    @Autowired
    private CollectionRequestDataValidator collectionRequestDataValidator;

    @ApiOperation(value = "Load collection by ID", notes = COLLECTION_PAGE_NOTES)
    @ApiResponses({ @ApiResponse(code = 200, message = COLLECTION_PAGE_NOTES, response = CollectionsData.class),
            @ApiResponse(code = 200, message = COLLECTION_PAGE_NOTES, response = PublishedDocumentsData.class),
            @ApiResponse(code = 200, message = COLLECTION_PAGE_NOTES, response = CollectionData.class) })
    @GetMapping(LOAD_COLLECTION_URL)
    public ModelAndView loadCollection(Authentication authentication, @PathVariable Long id, @RequestParam(defaultValue = "DESC") String sort,
            @RequestParam(defaultValue = "DATE") String sortBy, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) List<String> type, @RequestParam(required = false) List<String> language, @RequestParam(required = false) List<String> country) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();

        ModelAndView mav = loadCollectionsTemplate(userId);
        
        Collection collection = collectionService.loadCollectionByIdAndUserId(id, userId);

        CollectionData collectionData = null != collection ? collectionDataService.constructCollectionData(collection) : null;
        mav.addObject("collectionData", collectionData);

        SortingRequest sortingRequest = new SortingRequest.SortingRequestBuilder().setSort(sort).setSortBy(sortBy).build();
        PaginationRequest paginationRequest = new PaginationRequest.PaginationRequestBuilder().setSize(size).setPage(page).build();
        PublishedDocumentsData publishedDocumentsData = collectionService.loadCollectionDocuments(collection, sortingRequest, paginationRequest);
        mav.addObject("collectionDocuments", publishedDocumentsData);

        return mav;
    }
    
    @ApiOperation(value = "Load user's published documents as collection", notes = COLLECTION_PAGE_NOTES)
    @ApiResponses({ @ApiResponse(code = 200, message = COLLECTION_PAGE_NOTES, response = CollectionsData.class),
            @ApiResponse(code = 200, message = COLLECTION_PAGE_NOTES, response = PublishedDocumentsData.class),
            @ApiResponse(code = 200, message = COLLECTION_PAGE_NOTES, response = CollectionData.class) })
    @GetMapping(PUBLISHED_DOCUMENT_COLLECTION_URL)
    public ModelAndView loadUsersPublishedDocumentsCollection(Authentication authentication, @RequestParam(defaultValue = "DESC") String sort,
            @RequestParam(defaultValue = "DATE") String sortBy,
            @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "0") int page, @RequestParam(required = false) List<String> type,
            @RequestParam(required = false) List<String> language, @RequestParam(required = false) List<String> country,
            @RequestParam(required = false, defaultValue = "false") boolean publishing) {
        User user = (User) authentication.getPrincipal();

        ModelAndView mav = loadCollectionsTemplate(user.getId());
        
        // Displays User's published documents list by default, which is a virtual collection
        mav.addObject("collectionData", null);

        SortingRequest sortingRequest = new SortingRequest.SortingRequestBuilder().setSort(sort).setSortBy(sortBy).build();
        PaginationRequest paginationRequest = new PaginationRequest.PaginationRequestBuilder().setSize(size).setPage(page).build();
        PublishedDocumentsData publishedDocumentsData = collectionService.loadUsersPublishedDocuments(user, sortingRequest, paginationRequest);
        mav.addObject("collectionDocuments", publishedDocumentsData);

        return mav;
    }

    @ApiOperation(value = "Create collection", notes = COLLECTION_PAGE_NOTES)
    @ApiResponses({ @ApiResponse(code = 200, message = COLLECTION_PAGE_NOTES, response = CollectionsData.class),
            @ApiResponse(code = 200, message = COLLECTION_PAGE_NOTES, response = PublishedDocumentsData.class),
            @ApiResponse(code = 200, message = COLLECTION_PAGE_NOTES, response = CollectionData.class) })
    @PostMapping(COLLECTION_URL)
    public ModelAndView createCollection(Authentication authentication, @Valid @ModelAttribute CollectionRequestData collectionRequestData, Errors errors) {
        User user = (User) authentication.getPrincipal();
        if (!errors.hasErrors()) {
            collectionService.createCollection(collectionRequestData, user);
        }
        
        ModelAndView mav = loadCollectionsTemplate(user.getId());

        // Displays User's published documents list by default, which is a virtual collection
        mav.addObject("collectionData", null);

        SortingRequest sortingRequest = new SortingRequest.SortingRequestBuilder().setSort("DESC").setSortBy(PublishedDocumentSortByField.DATE.name()).build();
        PaginationRequest paginationRequest = new PaginationRequest.PaginationRequestBuilder().setSize(10).setPage(0).build();
        PublishedDocumentsData publishedDocumentsData = collectionService.loadUsersPublishedDocuments(user, sortingRequest, paginationRequest);
        mav.addObject("collectionDocuments", publishedDocumentsData);

        return mav;
    }

    @InitBinder("collectionRequestData")
    public void setupCollectionRequestDataDataBinder(WebDataBinder binder) {
        binder.addValidators(collectionRequestDataValidator);
    }

    private ModelAndView loadCollectionsTemplate(Long userId) {
        ModelAndView mav = new ModelAndView(COLLECTIONS_TEMPLATE);

        CollectionsData collectionsData = collectionService.loadCollectionsDataWithPublishedDocumentCollectionByUser(userId);
        mav.addObject("collectionsData", collectionsData);

        UserMenuData userMenuData = userService.loadUserMenuData();
        if (null != userMenuData) {
            mav.addObject("userMenuData", userMenuData);
        }

        return mav;
    }

}
