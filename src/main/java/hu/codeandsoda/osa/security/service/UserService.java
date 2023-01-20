package hu.codeandsoda.osa.security.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import hu.codeandsoda.osa.account.user.data.UserAccountData;
import hu.codeandsoda.osa.account.user.data.UserCollectionData;
import hu.codeandsoda.osa.account.user.data.UserDisplayNameData;
import hu.codeandsoda.osa.account.user.data.UserDocumentData;
import hu.codeandsoda.osa.account.user.data.UserImageData;
import hu.codeandsoda.osa.account.user.data.UserMenuData;
import hu.codeandsoda.osa.account.user.data.UserPublishedDocumentAnnotationData;
import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.account.user.exception.UserDeletionException;
import hu.codeandsoda.osa.account.user.repository.UserRepository;
import hu.codeandsoda.osa.collection.sevice.CollectionService;
import hu.codeandsoda.osa.document.service.DocumentService;
import hu.codeandsoda.osa.documentpublish.service.PublishedDocumentAnnotationService;
import hu.codeandsoda.osa.documentpublish.service.PublishedDocumentService;
import hu.codeandsoda.osa.media.service.MediaService;
import hu.codeandsoda.osa.myshoebox.service.MyShoeBoxService;
import hu.codeandsoda.osa.ocr.service.OcrRateLimitService;
import hu.codeandsoda.osa.registration.service.RegistrationTokenService;
import hu.codeandsoda.osa.resetpassword.service.PasswordResetTokenService;
import hu.codeandsoda.osa.util.ErrorCode;

@Service
public class UserService implements UserDetailsService {

    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private PublishedDocumentService publishedDocumentService;

    @Autowired
    private PublishedDocumentAnnotationService publishedDocumentAnnotationService;

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @Autowired
    private RegistrationTokenService registrationTokenService;

    @Autowired
    private OcrRateLimitService ocrRateLimitService;

    @Autowired
    private MyShoeBoxService myShoeBoxService;

    @Value("${osa.user.anonymous.id}")
    private Long anonymousUserId;

    @Value("${osa.user.password.length.min}")
    private int passwordLengthMin;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(email);
        }
        return user;
    }

    public boolean userExists(String email) {
        User user = findByEmail(email);
        return null != user;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User enableUser(User user) {
        user.setEnabled(true);
        return save(user);
    }

    public User findByMyShoeBoxId(Long id) {
        return userRepository.findByMyShoeBoxId(id);
    }

    public User loadById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        User user = optionalUser.isPresent() ? optionalUser.get() : null;
        return user;
    }

    @Transactional
    public void deleteUser(User user, Errors errors) throws UserDeletionException {
        Long userId = user.getId();

        assignPublishedDocumentsToAnonymousUser(user, errors);

        mediaService.deleteUserImages(user);

        documentService.deleteUserDocuments(user);
        
        collectionService.deleteUserCollections(user);

        publishedDocumentAnnotationService.deleteUserAnnotations(user);

        ocrRateLimitService.deleteByUser(user);
        passwordResetTokenService.deleteByUser(user);
        registrationTokenService.deleteByUser(user);

        userRepository.deleteById(userId);
    }

    public UserAccountData collectUserAccountData(User user) {
        Long id = user.getId();

        List<UserImageData> images = mediaService.collectUserImages(user);

        List<UserDocumentData> documents = documentService.collectUserDocuments(user);

        List<String> publishedDocuments = publishedDocumentService.collectUserPublishedDocumentUrls(user);

        List<UserCollectionData> collections = collectionService.collectUserCollections(user);

        List<UserPublishedDocumentAnnotationData> annotations = publishedDocumentService.collectUserPublishedDocumentAnnotations(user);

        UserAccountData userAccountData = new UserAccountData.UserAccountDataBuilder().setId(id).setEmail(user.getEmail()).setName(user.getUsername())
                .setDisplayName(user.getDisplayName()).setImages(images)
                .setDocuments(documents).setPublishedDocuments(publishedDocuments).setCollections(collections).setAnnotations(annotations).build();
        return userAccountData;
    }

    public UserMenuData loadUserMenuData() {
        UserMenuData userMenuData = null;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (null != authentication && null != authentication.getPrincipal() && authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            Long userId = user.getId();

            int myShoeBoxSize = myShoeBoxService.loadUsersMyShoeBoxSize(user);
            int documentsSize = documentService.loadUsersDocumentsSize(userId);

            userMenuData = new UserMenuData.UserMenuDataBuilder().setMyShoeBoxSize(myShoeBoxSize).setDocumentsSize(documentsSize).build();
        }

        return userMenuData;
    }

    public boolean isPasswordLengthValid(String password) {
        return password.length() >= passwordLengthMin;
    }

    public UserDisplayNameData loadUserDisplayNameData(Long userId) {
        User user = loadById(userId);
        UserDisplayNameData userDisplayNameData = new UserDisplayNameData.UserDisplayNameDataBuilder().setEmail(user.getEmail()).setDisplayName(user.getDisplayName())
                .setPublicEmail(user.isPublicEmail())
                .build();
        return userDisplayNameData;
    }

    public void setUserEmailStatus(Long userId, boolean publicEmail) {
        User user = loadById(userId);
        user.setPublicEmail(publicEmail);
        save(user);
    }

    private void assignPublishedDocumentsToAnonymousUser(User user, Errors errors) throws UserDeletionException {
        Long userId = user.getId();
        User anonymousUser = loadAnonymousUser(errors);
        if (null == anonymousUser) {
            logger.error("action=deleteUser, status=error, errorCode=" + ErrorCode.ANONYMOUS_USER_NOT_FOUND);

            String errorMessage = "Could not delete user";
            errors.reject(ErrorCode.USER_DELETE_ERROR.toString(), errorMessage);
            throw new UserDeletionException(errorMessage, errors.getAllErrors());
        }

        publishedDocumentService.modifyPublishedDocumentUsers(userId, anonymousUser);
    }

    private User loadAnonymousUser(Errors errors) throws UserDeletionException {
        Optional<User> optionalUser = userRepository.findById(anonymousUserId);
        User user = optionalUser.isPresent() ? optionalUser.get() : null;
        return user;
    }

}
