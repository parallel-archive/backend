package hu.codeandsoda.osa.jms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@EnableJms
@ImportResource("classpath:activemq.xml")
@Configuration
public class ActiveMQConfig {

    public static final String EMAIL_QUEUE = "email-queue";
    public static final String DELETE_IMAGE_QUEUE = "delete-image-queue";

    public static final String GENERATE_OCR_QUEUE = "generate-ocr-queue";
    public static final String PUBLISH_DOCUMENT_GENERATE_HASH_QUEUE = "publish-document-generate-hash-queue";
    public static final String PUBLISH_DOCUMENT_GENERATE_PDF_QUEUE = "publish-document-generate-pdf-queue";
    public static final String PUBLISH_DOCUMENT_UPLOAD_TO_IPFS_QUEUE = "publish-document-upload-to-ipfs-queue";
    public static final String PUBLISH_DOCUMENT_CREATE_PDF_THUMBNAIL_QUEUE = "publish-document-create-pdf-thumbnail-queue";
    public static final String PUBLISH_DOCUMENT_DELETE_ORIGINAL_DOCUMENT_QUEUE = "publish-document-delete-original-document-queue";
    public static final String PUBLISH_DOCUMENT_INDEX_QUEUE = "publish-document-index-queue";
    public static final String PUBLISH_DOCUMENT_QUEUE = "publish-document-queue";

    @Bean
    public JmsListenerContainerFactory<?> queueListenerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setMessageConverter(messageConverter());
        return factory;
    }

    @Bean
    public MessageConverter messageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }

}
