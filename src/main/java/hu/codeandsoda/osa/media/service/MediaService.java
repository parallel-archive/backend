package hu.codeandsoda.osa.media.service;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.MultiObjectDeleteException;
import com.amazonaws.services.s3.model.S3Object;

import hu.codeandsoda.osa.account.user.data.UserImageData;
import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.general.data.ResponseId;
import hu.codeandsoda.osa.general.data.ResponseMessage;
import hu.codeandsoda.osa.general.data.ResponseMessageScope;
import hu.codeandsoda.osa.jms.service.image.DeleteImageMessageService;
import hu.codeandsoda.osa.media.exception.MediaCopyException;
import hu.codeandsoda.osa.media.exception.MediaLoadingException;
import hu.codeandsoda.osa.media.exception.MediaUploadException;
import hu.codeandsoda.osa.myshoebox.data.DeleteImagesData;
import hu.codeandsoda.osa.myshoebox.data.ImageData;
import hu.codeandsoda.osa.myshoebox.data.ImageRequestData;
import hu.codeandsoda.osa.myshoebox.data.Rotation;
import hu.codeandsoda.osa.myshoebox.domain.Image;
import hu.codeandsoda.osa.myshoebox.domain.MyShoeBox;
import hu.codeandsoda.osa.myshoebox.service.ImageService;
import hu.codeandsoda.osa.prometheus.service.PrometheusReporterService;
import hu.codeandsoda.osa.security.service.UserService;
import hu.codeandsoda.osa.util.ErrorCode;
import hu.codeandsoda.osa.util.ResourceHandler;

@Service
public class MediaService {

    public static final List<String> VALID_EXTENSIONS = new ArrayList<>(Arrays.asList("image/png", "image/jpeg"));
    private static final String COULD_NOT_UPLOAD_MEDIA_ERROR_MESSAGE = "Could not upload media.";

    private static final int DEFAULT_ROTATION = 0;
    private static final String THUMBNAIL_KEY_PREFIX = "thumbnail_";
    private static final String ACTIVE_KEY_PREFIX = "active_";

    private static final String JPEG_EXTENSION = ".jpg";
    private static final String PNG_EXTENSION = ".png";

    private static final String EDIT_IMAGE_ACTION = "editImage";
    private static final String UPLOAD_MEDIA_TO_AWS_S3_ACTION = "uploadMediaToAwsS3";

    private static Logger logger = LoggerFactory.getLogger(MediaService.class);

    @Autowired
    private AwsS3Service awsS3Service;

    @Autowired
    private ThumbnailService thumbnailService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;

    @Autowired
    private DeleteImageMessageService deleteImageMessageService;

    @Autowired
    private PrometheusReporterService prometheusReporterService;

    @Autowired
    private MultipartFileService multipartFileService;

    @Autowired
    private ResourceHandler resourceHandler;

    public ImageData uploadImageWithThumbnail(MultipartFile file, Errors errors) throws MediaUploadException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = user.getId();
        String fileName = file.getOriginalFilename();
        List<ResponseMessage> messages = new ArrayList<>();

        Image image = createEmptyImage(user.getMyShoeBox(), fileName);
        String imageKey = uploadImage(file, image, userId, errors);
        
        createAndUploadThumbnail(file, image, imageKey, userId, errors);
        
        ImageData savedImageData = imageService.save(image);

        handleMissingThumbnailWarning(savedImageData, userId, fileName, messages);
        savedImageData.setMessages(messages);

        String logMessage = new StringBuilder().append("action=uploadMediaToAwsS3, status=success, userId=").append(userId).append(", media=").append(file.getOriginalFilename())
                .toString();
        logger.info(logMessage);

        return savedImageData;
    }

    private void createAndUploadThumbnail(MultipartFile file, Image image, String imageKey, Long userId, Errors errors) throws MediaUploadException {
        try (InputStream thumbnail = thumbnailService.generateThumbnail(file, userId, errors)) {
            String thumbnailKey = new StringBuilder().append(userId).append("/").append(THUMBNAIL_KEY_PREFIX).append(imageKey).append(".")
                    .append(ThumbnailService.THUMBNAIL_EXTENSION).toString();
            if (null != thumbnail) {
                String thumbnailUrl = awsS3Service.upload(thumbnail, thumbnailKey, MediaType.IMAGE_PNG_VALUE, userId, errors);
                image.setThumbnailUrl(thumbnailUrl);
            }
        } catch (IOException e) {
            String logMessage = MessageFormat.format("action=generateThumbnail, status=error, userId={0}, key={1}, error={2}", userId, imageKey, e.getMessage());
            logger.error(logMessage, e);

            prometheusReporterService.sendGenerateThumbnailError(e.getMessage());
        }
    }

    public String uploadInputStream(InputStream inputStream, String key, String contentType, Long userId, Errors errors) throws MediaUploadException {
        String url = awsS3Service.upload(inputStream, key, contentType, userId, errors);
        return url;
    }

    public ImageData editImage(ImageRequestData imageRequestData, Errors errors) throws MediaUploadException, MediaLoadingException {
        Long id = imageRequestData.getId();
        Image image = imageService.loadById(id);
        Long userId = userService.findByMyShoeBoxId(image.getMyShoeBox().getId()).getId();

        ZonedDateTime now = ZonedDateTime.now();
        image.setModifiedAt(now);
        

        String originalName = image.getName();
        String newName = imageRequestData.getName();
        if (StringUtils.hasText(newName) && !newName.equals(originalName)) {
            image.setName(newName);
        }

        Integer originalRotation = image.getRotation();
        Integer newRotation = null == imageRequestData.getRotation() ? originalRotation : convertRotationValue(imageRequestData.getRotation());
        if (!newRotation.equals(originalRotation)) {
            rotateImage(image, newRotation, userId, errors);
        }

        ImageData savedImage = imageService.save(image);
        return savedImage;
    }

    public String uploadDocumentImage(String imageUrl, String imageName, Long documentId, Errors errors) throws MediaCopyException {
        String imageKey = extractS3ObjectKeyFromUrl(imageUrl);

        String formattedImageName = formatImageName(imageName);
        String extension = extractImageExtensionFromKey(imageKey);
        String documentImageKey = new StringBuilder().append("documents/").append(documentId).append("/").append(formattedImageName).append("_").append(getCurrentTime())
                .append(".").append(extension).toString();

        String url = awsS3Service.copyImage(imageKey, documentImageKey, errors);
        return url;
    }

    public String uploadPdfThumbnail(String imageUrl, String pdfThumbnailName, Errors errors) throws MediaCopyException {
        String imageKey = extractS3ObjectKeyFromUrl(imageUrl);

        String imageExtension = extractImageExtensionFromKey(imageKey);
        String pdfThumbnailKey = new StringBuilder().append(pdfThumbnailName).append(".").append(imageExtension).toString();
        String url = awsS3Service.copyImage(imageKey, pdfThumbnailKey, errors);
        return url;
    }

    public void deleteImages(List<Long> imageIds, User user) {
        Long userId = user.getId();
        Long myShoeBoxId = user.getMyShoeBox().getId();
        List<String> urls = collectImageUrls(imageIds);

        imageService.deleteAllById(imageIds);
        deleteImageMessageService.addToDeleteImageQueue(new DeleteImagesData.DeleteImagesDataBuilder().setUrls(urls).build());

        String deleteLogMessage = MessageFormat.format("action=deleteImageFromDatabase, status=success, userId={0}, myShoeBoxId={1}, size={2}", userId, myShoeBoxId,
                imageIds.size());
        logger.info(deleteLogMessage);
    }

    public void deleteImageUrls(DeleteImagesData deleteImagesData) {
        List<String> imageUrls = deleteImagesData.getUrls();
        List<String> imageKeys = imageUrls.stream().map(u -> extractS3ObjectKeyFromUrl(u)).collect(Collectors.toList());

        try {
            awsS3Service.deleteImages(imageKeys);
        } catch (MultiObjectDeleteException e) {
            Map<String, String> deleteImageErrors = awsS3Service.collectDeleteImageErrors(e.getErrors());
            for (String imageKey : imageKeys) {
                if (deleteImageErrors.containsKey(imageKey)) {
                    String errorMessage = deleteImageErrors.get(imageKey);
                    String errorLog = MessageFormat.format("scheduledTask=deleteImageUrls, action=deleteImageUrlFromS3Bucket, status=error, url={0} , error={1}", imageKey,
                            errorMessage);
                    logger.error(errorLog, e);
                } else {
                    String successLog = MessageFormat.format("scheduledTask=deleteImageUrls, action=deleteImageUrlFromS3Bucket, status=success, url={0}", imageKey);
                    logger.info(successLog);
                }
            }
        }
    }

    public BufferedImage loadBufferedImageFromAWS(String url, Errors errors) throws MediaLoadingException {
        BufferedImage bufferedImage = null;
        try (S3Object s3Object = loadS3ObjectFromAWS(url, errors); InputStream inputStream = s3Object.getObjectContent().getDelegateStream()) {
            bufferedImage = readInputStream(inputStream, url, errors);
        } catch (IOException e) {
            String logMessage = MessageFormat.format("action=loadBufferedImageFromAwsS3, status=warning, error=Could not close S3Object or InputStream resources., key={0}", url);
            logger.warn(logMessage, e);
        }

        return bufferedImage;
    }

    public S3Object loadS3ObjectFromAWS(String url, Errors errors) throws MediaLoadingException {
        String key = extractS3ObjectKeyFromUrl(url);
        S3Object s3Object = awsS3Service.loadImage(key, errors);
        return s3Object;
    }

    public String extractS3ObjectKeyFromUrl(String url) {
        String key = url.split(".com/")[1];
        return key;
    }

    public void deleteUserImages(User user) {
        Long myShoeBoxId = user.getMyShoeBox().getId();
        List<Long> imageIds = imageService.loadImageIdsByMyShoeBoxId(myShoeBoxId);
        deleteImages(imageIds, user);
    }

    public List<UserImageData> collectUserImages(User user) {
        Long myShoeBoxId = user.getMyShoeBox().getId();
        List<UserImageData> images = imageService.loadUserImagesByMyShoeBoxId(myShoeBoxId);
        return images;
    }

    private Image createEmptyImage(MyShoeBox myShoeBox, String name) {
        ZonedDateTime now = ZonedDateTime.now();

        Image image = new Image();
        image.setMyShoeBox(myShoeBox);
        image.setName(name);
        image.setUploadedAt(now);
        image.setModifiedAt(now);
        image.setRotation(DEFAULT_ROTATION);
        return image;
    }

    private String uploadImage(MultipartFile file, Image image, Long userId, Errors errors) throws MediaUploadException {
        String imageKey = createImageKeyFromFile(file, userId, errors);
        String extension = getFileExtension(file, userId, errors);

        String imageKeyWithFolder = new StringBuilder().append(userId).append("/").append(imageKey).append(extension).toString();

        String contentType = getContentType(extension);

        try (InputStream inputStream = multipartFileService.getFileInputStream(file, userId, errors)) {
            String url = awsS3Service.upload(inputStream, imageKeyWithFolder, contentType, userId, errors);
            image.setUrl(url);
        } catch (IOException e) {
            String logMessage = MessageFormat.format("action=uploadMediaToAwsS3, status=warning, error=Could not close InputStream resource., imageKey={0}, errorMessage={1}",
                    imageKey,
                    e.getMessage());
            logger.warn(logMessage, e);
        }
        return imageKey;
    }

    private void handleMissingThumbnailWarning(ImageData savedImageData, Long userId, String fileName, List<ResponseMessage> messages) {
        if (!StringUtils.hasText(savedImageData.getThumbnailUrl())) {
            ResponseMessage message = new ResponseMessage.ResponseMessageBuilder().setId(ResponseId.THUMBNAIL_URL).setMessage("Could not generate thumbnail.")
                    .setResponseMessageScope(ResponseMessageScope.WARNING).build();
            messages.add(message);

            String thumbnailLogMessage = new StringBuilder().append("action=uploadThumbnailToAwsS3, status=error, userId=").append(userId).append(", media=")
                    .append(fileName).toString();
            logger.info(thumbnailLogMessage);
        }
    }

    private String getFileExtension(MultipartFile file, Long userId, Errors errors) throws MediaUploadException {
        String contentType = multipartFileService.getFileContentType(file, userId, errors);
        String extension = contentType.equals("image/png") ? PNG_EXTENSION : JPEG_EXTENSION;
        return extension;
    }

    private String getContentType(String extension) {
        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if (PNG_EXTENSION.equals(extension)) {
            contentType = MediaType.IMAGE_PNG_VALUE;
        } else if (JPEG_EXTENSION.equals(extension)) {
            contentType = MediaType.IMAGE_JPEG_VALUE;
        }
        return contentType;
    }

    private void rotateImage(Image image, Integer rotation, Long userId, Errors errors) throws MediaLoadingException, MediaUploadException {
        try (S3Object imageObject = loadS3ObjectFromAWS(image.getUrl(), errors)) {
            String activeUrl = rotateAndUploadImage(imageObject, rotation, image.getActiveUrl(), userId, image.getId(), errors);
            image.setActiveUrl(activeUrl);
        } catch (IOException e) {
            String logMessage = new StringBuilder().append("action=editImage, status=error, userId=").append(userId).append(", url=").append(image.getActiveUrl())
                    .append(", error=").append(e.getMessage()).toString();
            logger.error(logMessage, e);
            errors.reject(ErrorCode.AWS_S3_CONNECTION_ERROR.toString(), "Error during closing S3 connection.");
            throw new MediaUploadException("Could not close S3 connecion.", errors.getAllErrors());
        }

        if (StringUtils.hasText(image.getThumbnailUrl())) {
            try (S3Object thumbnail = loadS3ObjectFromAWS(image.getThumbnailUrl(), errors)) {
                String activeThumbnailUrl = rotateAndUploadImage(thumbnail, rotation, image.getActiveThumbnailUrl(), userId, image.getId(), errors);
                image.setActiveThumbnailUrl(activeThumbnailUrl);
            } catch (IOException e) {
                String logMessage = new StringBuilder().append("action=editImage, status=error, userId=").append(userId).append(", url=")
                        .append(image.getActiveThumbnailUrl())
                        .append(", error=").append(e.getMessage()).toString();
                logger.error(logMessage, e);
                errors.reject(ErrorCode.AWS_S3_CONNECTION_ERROR.toString(), "Error during closing S3 connection.");
                throw new MediaUploadException("Could not close S3 connecion.", errors.getAllErrors());
            }
        }
        image.setRotation(rotation);
    }

    private String extractImageExtensionFromKey(String image) {
        String extension = image.endsWith("png") ? "png" : "jpg";
        return extension;
    }

    private List<String> collectImageUrls(List<Long> imageIds) {
        List<String> urls = new ArrayList<>();
        for (Long imageId : imageIds) {
            Image image = imageService.loadById(imageId);

            String url = image.getUrl();
            urls.add(url);
            logger.info(MessageFormat.format("action=addImageUrlToDeleteImageQueue, status=success, url={0}", url));

            String thumbnailUrl = image.getThumbnailUrl();
            if (StringUtils.hasText(thumbnailUrl)) {
                urls.add(thumbnailUrl);
                logger.info(MessageFormat.format("action=addImageUrlToDeleteImageQueue, status=success, url={0}", thumbnailUrl));
            }

            String activeUrl = image.getActiveUrl();
            if (StringUtils.hasText(activeUrl)) {
                urls.add(activeUrl);
                logger.info(MessageFormat.format("action=addImageUrlToDeleteImageQueue, status=success, url={0}", activeUrl));
            }

            String activeThumbnailUrl = image.getActiveThumbnailUrl();
            if (StringUtils.hasText(activeThumbnailUrl)) {
                urls.add(activeThumbnailUrl);
                logger.info(MessageFormat.format("action=addImageUrlToDeleteImageQueue, status=success, url={0}", activeThumbnailUrl));
            }
        }
        return urls;
    }

    private BufferedImage readInputStream(InputStream inputStream, String url, Errors errors) throws MediaLoadingException {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(inputStream);
        } catch (IOException e) {
            String logMessage = new StringBuilder().append("action=loadBufferedImageFromAwsS3, status=error, key=").append(url).append(", error=").append(e.getMessage())
                    .toString();
            logger.error(logMessage, e);
            errors.reject(ErrorCode.MEDIA_LOADING_ERROR.toString(), "Could not load buffered image from AWS.");
            throw new MediaLoadingException("Could not load media.", errors.getAllErrors());
        }
        return bufferedImage;
    }

    private String createImageKeyFromFile(MultipartFile file, Long userId, Errors errors) throws MediaUploadException {
        String fileName = file.getOriginalFilename();
        
        String imageKey = null;
        try {
            String formattedFileName = formatImageName(fileName);

            byte[] bytes = file.getBytes();
            String hashedImageContent = hashImageContent(bytes);

            long timestamp = getCurrentTime();

            imageKey = new StringBuilder().append(hashedImageContent).append("_").append(timestamp).append("_").append(formattedFileName).toString();
        } catch (NoSuchAlgorithmException e) {
            String errorMessage = "Could not create hashed file name.";
            handleMediaUploadError(e, userId, fileName, errorMessage, errors);
        } catch (IOException e) {
            String errorMessage = "Error during loading file.";
            handleMediaUploadError(e, userId, fileName, errorMessage, errors);
        }

        return imageKey;
    }

    private String formatImageName(String imageName) {
        String formattedImageName = imageName.replaceAll("[^a-zA-Z0-9._]", "");
        return formattedImageName;
    }

    private String hashImageContent(byte[] bytes) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = digest.digest(bytes);
        String encodedHashAsString = DatatypeConverter.printHexBinary(encodedhash);
        return encodedHashAsString;
    }

    private long getCurrentTime() {
        return new Date().getTime();
    }

    private int convertRotationValue(Integer rotation) {
        Integer value = DEFAULT_ROTATION;
        for (Rotation validRotation : Rotation.values()) {
            if (rotation % validRotation.getRotationValue() == 0) {
                value = validRotation.equals(Rotation.ORIGINAL) ? 0 : validRotation.getRotationValue();
                break;
            }
        }
        return value;
    }

    private String rotateAndUploadImage(S3Object image, int rotation, String activeUrl, Long userId, Long imageId, Errors errors) throws MediaUploadException {
        String url = null;
        InputStream inputStream = image.getObjectContent().getDelegateStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        InputStream rotatedImageInputStream = new ByteArrayInputStream(new byte[0]);
        try {
            String originalKey = image.getKey();
            String extension = extractImageExtensionFromKey(originalKey);
            String contentType = getContentType(extension);

            String rotatedImageKey = null;
            if (!StringUtils.hasText(activeUrl)) {
                rotatedImageKey = addPrefixToKey(originalKey, ACTIVE_KEY_PREFIX);
            } else {
                rotatedImageKey = extractS3ObjectKeyFromUrl(activeUrl);
            }

            BufferedImage bufferedImage = ImageIO.read(inputStream);

            BufferedImage rotatedImage = rotateImage(bufferedImage, rotation);
            ImageIO.write(rotatedImage, extension, outputStream);

            rotatedImageInputStream = new ByteArrayInputStream(outputStream.toByteArray());
            url = awsS3Service.upload(rotatedImageInputStream, rotatedImageKey, contentType, userId, errors);

        } catch (IOException e) {
            String logMessage = MessageFormat.format("action={0}, status=error, editedField=rotation, userId={1}, imageId={2}, error={3}", EDIT_IMAGE_ACTION, userId, imageId,
                    e.getMessage());
            logger.error(logMessage, e);
            errors.reject(ErrorCode.EDIT_IMAGE_NAME_ERROR.toString(), "Could not upload edited image. Edited field: name.");
            throw new MediaUploadException(COULD_NOT_UPLOAD_MEDIA_ERROR_MESSAGE, errors.getAllErrors());
        } finally {
            String actionParameters = MessageFormat.format("userId={0}, imageId={1}", userId, imageId);
            resourceHandler.closeInputStream(inputStream, EDIT_IMAGE_ACTION, actionParameters);
            resourceHandler.closeOutputStream(outputStream, EDIT_IMAGE_ACTION, actionParameters);
            resourceHandler.closeInputStream(rotatedImageInputStream, EDIT_IMAGE_ACTION, actionParameters);
        }
        return url;
    }

    private BufferedImage rotateImage(BufferedImage originalImage, int rotation) {

        // Getting Dimensions of image
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        double rotationRad = Math.toRadians(rotation);

        // Creating a new buffered image
        int newHeight = (int) (originalWidth * Math.abs(Math.sin(rotationRad)) + originalHeight * Math.abs(Math.cos(rotationRad)));
        int newWidth = (int) (originalHeight * Math.abs(Math.sin(rotationRad)) + originalWidth * Math.abs(Math.cos(rotationRad)));
        BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());

        // creating Graphics in buffered image
        Graphics2D g2 = rotatedImage.createGraphics();

        // Rotating image by degrees using toradians()
        // method
        // and setting new dimension t it
        g2.fillRect(0, 0, newWidth, newHeight); // fill entire area
        g2.translate(newWidth / 2, newHeight / 2);
        g2.rotate(rotationRad);
        g2.translate(-originalWidth / 2, -originalHeight / 2);
        g2.drawImage(originalImage, null, 0, 0);

        // release used resources before g is garbage-collected
        g2.dispose();

        // Return rotated buffer image
        return rotatedImage;
    }

    private String addPrefixToKey(String key, String prefix) {
        String[] keyParts = key.split("/");
        String activeKey = new StringBuilder().append(keyParts[0]).append("/").append(prefix).append(keyParts[1]).toString();
        return activeKey;
    }
    
    private void handleMediaUploadError(Exception exception, Long userId, String fileName, String errorMessage, Errors errors) throws MediaUploadException {
         String logMessage = createUploadMediaErrorLogMessage(userId, fileName, exception.getMessage());
         logger.error(logMessage, exception);
         errors.reject(ErrorCode.MEDIA_UPLOAD_ERROR.toString(), errorMessage);
         throw new MediaUploadException(COULD_NOT_UPLOAD_MEDIA_ERROR_MESSAGE, errors.getAllErrors());
    }

    private String createUploadMediaErrorLogMessage(Long userId, String fileName, String exceptionMessage) {
        String logMessage = MessageFormat.format("action=uploadMediaToAwsS3, status=error, userId={0}, media={1}, error={2}", userId, fileName, exceptionMessage);
        return logMessage;
    }

}
