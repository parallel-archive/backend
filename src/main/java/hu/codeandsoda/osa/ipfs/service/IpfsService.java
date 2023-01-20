package hu.codeandsoda.osa.ipfs.service;

import java.io.IOException;
import java.text.MessageFormat;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.client.RestTemplate;

import com.amazonaws.services.s3.model.S3Object;

import hu.codeandsoda.osa.ipfs.data.IpfsUploadResponseData;
import hu.codeandsoda.osa.ipfs.exception.IpfsUploadException;
import hu.codeandsoda.osa.media.exception.MediaLoadingException;
import hu.codeandsoda.osa.media.service.MediaService;
import hu.codeandsoda.osa.prometheus.service.PrometheusReporterService;
import hu.codeandsoda.osa.util.ErrorCode;

@Service
public class IpfsService {

    private static Logger logger = LoggerFactory.getLogger(IpfsService.class);

    @Autowired
    private MediaService mediaService;

    @Autowired
    private PrometheusReporterService prometheusReporterService;

    @Value("${osa.ipfs.url.upload}")
    private String ipfsUploadUrl;

    @Value("${osa.ipfs.api-password}")
    private String ipfsApiPassword;

    public String upload(String pdfUrl, String documentHash, Long publishedDocumentId, Errors errors) throws MediaLoadingException, IpfsUploadException {
        String logStart = MessageFormat.format("STARTED PDF upload to IPFS, publishedDocumentId={0}, pdfUrl={1}", publishedDocumentId, pdfUrl);
        logger.info(logStart);

        String ipfsContentId = null;
        try (S3Object s3Object = mediaService.loadS3ObjectFromAWS(pdfUrl, errors);) {
            String fileName = MessageFormat.format("{0}.pdf", documentHash);
            ResponseEntity<IpfsUploadResponseData> response = uploadPdfToIpfs(s3Object, fileName, publishedDocumentId, errors);

            ipfsContentId = loadIpfsContentIdFromResponse(response, publishedDocumentId, errors);
        } catch (IOException e) {
            String logMessage = MessageFormat.format("action=uploadPdfToIpfs, status=warning, error=Could not close pdf S3Object resource., publishedDocumentId={0}",
                    publishedDocumentId);
            logger.warn(logMessage, e);
        }

        String logEnd = MessageFormat.format("ENDED PDF upload to IPFS, publishedDocumentId={0}, pdfUrl={1}", publishedDocumentId, pdfUrl);
        logger.info(logEnd);

        return ipfsContentId;
    }

    private String loadIpfsContentIdFromResponse(ResponseEntity<IpfsUploadResponseData> response, Long publishedDocumentId, Errors errors) throws IpfsUploadException {
        if (null == response.getBody() || !StringUtils.hasText(response.getBody().getCid())) {
            handleMissingIpfsResponse(publishedDocumentId, errors);
        }
        String ipfsContentId = response.getBody().getCid();
        return ipfsContentId;
    }

    private ResponseEntity<IpfsUploadResponseData> uploadPdfToIpfs(S3Object s3Object, String fileName, Long publishedDocumentId, Errors errors) throws IpfsUploadException {
        ResponseEntity<IpfsUploadResponseData> response = new ResponseEntity<>(HttpStatus.OK);
        try {
            HttpHeaders headers = constructRequestHeaders();
            MultiValueMap<String, Object> body = constructRequestBody(s3Object, fileName);
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();
            response = restTemplate.postForEntity(ipfsUploadUrl, request, IpfsUploadResponseData.class);
        } catch (Exception e) {
            handleUploadPdfToIpfsError(e, publishedDocumentId, errors);

            prometheusReporterService.sendUploadPdfToIpfsError(e.getMessage());

            throw new IpfsUploadException("Could not upload pdf to IPFS.", errors.getAllErrors());
        }

        return response;
    }

    private HttpHeaders constructRequestHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", ipfsApiPassword);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return headers;
    }

    private MultiValueMap<String, Object> constructRequestBody(S3Object s3Object, String fileName) throws IOException {
        byte[] bytes = IOUtils.toByteArray(s3Object.getObjectContent());

        LinkedMultiValueMap<String, String> pdfHeaderMap = new LinkedMultiValueMap<>();
        pdfHeaderMap.add("Content-disposition", "form-data; name=\"file\"; filename=\"" + fileName + "\"");
        pdfHeaderMap.add("Content-type", "application/pdf");

        HttpEntity<byte[]> file = new HttpEntity<byte[]>(bytes, pdfHeaderMap);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file);

        return body;
    }

    private void handleUploadPdfToIpfsError(Exception exception, Long publishedDocumentId, Errors errors) {
        String error = exception.getMessage();
        String logMessage = MessageFormat.format("action=uploadPdfToIpfs, status=error, publishedDocumentId={0}, error={1}", publishedDocumentId, error);
        logger.error(logMessage, exception);

        errors.reject(ErrorCode.IPFS_UPLOAD_ERROR.toString(), "Could not upload document pdf to IPFS.");
    }

    private void handleMissingIpfsResponse(Long publishedDocumentId, Errors errors) throws IpfsUploadException {
        String logMessage = MessageFormat.format("action=uploadPdfToIpfs, status=error, error=IPFS response is empty or contains no content ID., publishedDocumentId={0}",
                publishedDocumentId);
        logger.error(logMessage);

        errors.reject(ErrorCode.IPFS_UPLOAD_ERROR.toString(), "Could not upload document pdf to IPFS.");
        throw new IpfsUploadException("Could not upload pdf to IPFS.", errors.getAllErrors());
    }

}
