package hu.codeandsoda.osa.pdf.service;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import hu.codeandsoda.osa.document.domain.DocumentImage;
import hu.codeandsoda.osa.media.exception.MediaLoadingException;
import hu.codeandsoda.osa.media.service.MediaService;
import hu.codeandsoda.osa.pdf.exception.CreatePDFException;
import hu.codeandsoda.osa.prometheus.service.PrometheusReporterService;
import hu.codeandsoda.osa.util.ErrorCode;

@Service
public class PdfService {

    private static Logger logger = LoggerFactory.getLogger(PdfService.class);

    @Autowired
    private MediaService mediaService;

    @Autowired
    private PrometheusReporterService prometheusReporterService;

    public ByteArrayOutputStream createPDFFromImages(Long publishedDocumentId, List<DocumentImage> images, Errors errors) throws CreatePDFException, MediaLoadingException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (PDDocument pdDocument = new PDDocument()) {
            for (DocumentImage image : images) {
                BufferedImage bufferedImage = mediaService.loadBufferedImageFromAWS(image.getUrl(), errors);
                PDPage pdPage = createEmptyPdPage(bufferedImage);
                
                scaleAndDrawPdImageToPdPage(pdDocument, pdPage, bufferedImage);

                pdDocument.addPage(pdPage);
                String logMessage = MessageFormat.format("action=addDocumentImageToPdf, status=success, publishedDocumentId={0}, imageId={1}", publishedDocumentId, image.getId());
                logger.info(logMessage);
            }
            pdDocument.save(os);
            String logMessage = MessageFormat.format("action=createPdfFromDocumentImages, status=success, publishedDocumentId={0}", publishedDocumentId);
            logger.info(logMessage);
        } catch (IOException e) {
            String logMessage = MessageFormat.format("action=createPdfFromDocumentImages, status=error, publishedDocumentId={0}, error={1}", publishedDocumentId, e.getMessage());
            logger.error(logMessage, e);

            prometheusReporterService.sendGeneratePdfError(e.getMessage());

            errors.reject(ErrorCode.CREATE_PDF_ERROR.toString(), "Could not create PDF from document images.");
            throw new CreatePDFException("Could not load media.", errors.getAllErrors());
        }
        return os;
    }

    private PDPage createEmptyPdPage(BufferedImage bufferedImage) {
        PDRectangle pdRectangle = createPdRectangle(bufferedImage);
        PDPage pdPage = new PDPage(pdRectangle);
        return pdPage;
    }

    private PDRectangle createPdRectangle(BufferedImage bufferedImage) {
        boolean portraitPage = bufferedImage.getHeight() > bufferedImage.getWidth();
        PDRectangle pdRectangle = portraitPage ? PDRectangle.A4 : new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth());
        return pdRectangle;
    }

    private void scaleAndDrawPdImageToPdPage(PDDocument pdDocument, PDPage pdPage, BufferedImage bufferedImage) throws IOException {
        PDRectangle pdRectangle = pdPage.getMediaBox();
        PDImageXObject pdImage = JPEGFactory.createFromImage(pdDocument, bufferedImage);

        // https://stackoverflow.com/questions/23223716/scaled-image-blurry-in-pdfbox
        Dimension pdImageScaledDimension = getPdImageScaledDimension(new Dimension(pdImage.getWidth(), pdImage.getHeight()),
                new Dimension((int) pdRectangle.getWidth(), (int) pdRectangle.getHeight()));

        try (PDPageContentStream contentStream = new PDPageContentStream(pdDocument, pdPage)) {
            float scaledImageWidth = (float) pdImageScaledDimension.getWidth();
            float startX = (pdRectangle.getWidth() - scaledImageWidth) / 2;
            
            float scaledImageHeight = (float) pdImageScaledDimension.getHeight();
            float startY = (pdRectangle.getHeight() - scaledImageHeight) / 2;
            contentStream.drawImage(pdImage, startX, startY, pdImageScaledDimension.width, pdImageScaledDimension.height);
        }
    }

    private Dimension getPdImageScaledDimension(Dimension imgSize, Dimension boundary) {
        int original_width = imgSize.width;
        int original_height = imgSize.height;
        int bound_width = boundary.width;
        int bound_height = boundary.height;
        int new_width = original_width;
        int new_height = original_height;

        // first check if we need to scale width
        if (original_width > bound_width) {
            // scale width to fit
            new_width = bound_width;
            // scale height to maintain aspect ratio
            new_height = (new_width * original_height) / original_width;
        }

        // then check if we need to scale even with the new height
        if (new_height > bound_height) {
            // scale height to fit instead
            new_height = bound_height;
            // scale width to maintain aspect ratio
            new_width = (new_height * original_width) / original_height;
        }

        return new Dimension(new_width, new_height);
    }

}
