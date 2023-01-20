package hu.codeandsoda.osa.document.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.codeandsoda.osa.document.domain.DocumentImage;
import hu.codeandsoda.osa.document.repository.DocumentImageRepository;

@Service
public class DocumentImageService {

    @Autowired
    private DocumentImageRepository documentImageRepository;

    public List<DocumentImage> loadByDocumentIdOrderedByIndex(Long documentId) {
        List<DocumentImage> images = documentImageRepository.findAllByDocumentIdOrderByIndex(documentId);
        return images;
    }

    public void deleteAll(List<DocumentImage> documentImages) {
        documentImageRepository.deleteAll(documentImages);
    }

    public DocumentImage save(DocumentImage markedDocumentImage) {
        return documentImageRepository.save(markedDocumentImage);
    }

    public List<DocumentImage> saveAll(List<DocumentImage> documentImages) {
        return (List<DocumentImage>) documentImageRepository.saveAll(documentImages);
    }

    public boolean documentImageExists(Long documentImageId, Long documentId) {
        return documentImageRepository.existsByIdAndDocumentId(documentImageId, documentId);
    }

}
