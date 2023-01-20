package hu.codeandsoda.osa.documentpublish.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.documentpublish.data.DocumentPublishProgressResponse;
import hu.codeandsoda.osa.documentpublish.service.DocumentPublishProgressService;
import io.swagger.annotations.ApiOperation;

@RestController
public class DocumentPublishProgressController {

    public static final String DOCUMENT_PUBLISH_IN_PROGRESS = "/api/publish/inprogress";

    @Autowired
    private DocumentPublishProgressService documentPublishProgressService;

    @ApiOperation(value = "Load if User has a Document publish in progress")
    @GetMapping(DOCUMENT_PUBLISH_IN_PROGRESS)
    public DocumentPublishProgressResponse loadUserDocumentPublishProgressStatus(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        DocumentPublishProgressResponse response = documentPublishProgressService.loadUserDocumentPublishProgressStatus(user.getId());
        return response;
    }

}
