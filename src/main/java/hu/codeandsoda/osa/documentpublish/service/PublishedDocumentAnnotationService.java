package hu.codeandsoda.osa.documentpublish.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.documentpublish.domain.PublishedDocument;
import hu.codeandsoda.osa.documentpublish.domain.PublishedDocumentAnnotation;
import hu.codeandsoda.osa.documentpublish.repository.PublishedDocumentAnnotationRepository;

@Service
public class PublishedDocumentAnnotationService {

    @Autowired
    private PublishedDocumentAnnotationRepository publishedDocumentAnnotationRepository;

    public PublishedDocumentAnnotation loadByPublishedDocumentAndUser(Long publishedDocumentId, Long userId) {
        PublishedDocumentAnnotation annotation = publishedDocumentAnnotationRepository.findByPublishedDocumentIdAndUserId(publishedDocumentId, userId);
        return annotation;
    }

    public PublishedDocumentAnnotation save(PublishedDocument document, User user, String annotationText) {
        boolean annotationExists = existsByPublishedDocumentIdAndUserId(document.getId(), user.getId());
        PublishedDocumentAnnotation annotation = annotationExists ? loadByPublishedDocumentAndUser(document.getId(), user.getId()) : createEmptyAnnotation(document, user);
        annotation.setAnnotation(annotationText);

        PublishedDocumentAnnotation savedAnnotation = publishedDocumentAnnotationRepository.save(annotation);
        return savedAnnotation;
    }

    public List<PublishedDocumentAnnotation> loadAnnotationsByUser(Long userId) {
        List<PublishedDocumentAnnotation> annotations = publishedDocumentAnnotationRepository.findAllByUserId(userId);
        return annotations;
    }

    public void deleteUserAnnotations(User user) {
        publishedDocumentAnnotationRepository.deleteAllByUser(user);
    }

    private boolean existsByPublishedDocumentIdAndUserId(Long publishedDocumentId, Long userId) {
        boolean annotationExists = publishedDocumentAnnotationRepository.existsByPublishedDocumentIdAndUserId(publishedDocumentId, userId);
        return annotationExists;
    }

    private PublishedDocumentAnnotation createEmptyAnnotation(PublishedDocument document, User user) {
        PublishedDocumentAnnotation annotation = new PublishedDocumentAnnotation();
        annotation.setUser(user);
        annotation.setPublishedDocument(document);
        return annotation;
    }

}
