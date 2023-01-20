package hu.codeandsoda.osa.jms.service.image;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import hu.codeandsoda.osa.jms.config.ActiveMQConfig;
import hu.codeandsoda.osa.myshoebox.data.DeleteImagesData;

@Service
public class DeleteImageMessageService {

    private static Logger logger = LoggerFactory.getLogger(DeleteImageMessageService.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    public void addToDeleteImageQueue(DeleteImagesData deleteImageData) {
        jmsTemplate.convertAndSend(ActiveMQConfig.DELETE_IMAGE_QUEUE, deleteImageData);

        String logMessage = MessageFormat.format("action=addToQueue, status=success, queue={0}, message=imageUrls, size={1}", ActiveMQConfig.DELETE_IMAGE_QUEUE,
                deleteImageData.getUrls().size());
        logger.info(logMessage);
    }

}
