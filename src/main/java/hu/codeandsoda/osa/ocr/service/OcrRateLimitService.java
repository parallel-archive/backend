package hu.codeandsoda.osa.ocr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.ocr.domain.OcrRateLimit;
import hu.codeandsoda.osa.ocr.repository.OcrRateLimitRepository;

@Service
public class OcrRateLimitService {

    @Autowired
    private OcrRateLimitRepository ocrRateLimitRepository;

    public OcrRateLimit loadByUser(User user) {
        OcrRateLimit ocrRateLimit = ocrRateLimitRepository.findByUser(user);
        if (null == ocrRateLimit) {
            ocrRateLimit = createInitialOcrRateLimit(user);
        }
        return ocrRateLimit;
    }

    public OcrRateLimit save(OcrRateLimit ocrRateLimit) {
        return ocrRateLimitRepository.save(ocrRateLimit);
    }

    public void deleteByUser(User user) {
        ocrRateLimitRepository.deleteByUser(user);
    }

    private OcrRateLimit createInitialOcrRateLimit(User user) {
        OcrRateLimit ocrRateLimit = new OcrRateLimit(user, 0);
        OcrRateLimit savedOcrRateLimit = save(ocrRateLimit);
        return savedOcrRateLimit;
    }

}
