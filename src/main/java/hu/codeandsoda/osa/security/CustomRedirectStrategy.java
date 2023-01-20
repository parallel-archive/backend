package hu.codeandsoda.osa.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.stereotype.Component;

@Component
public class CustomRedirectStrategy extends DefaultRedirectStrategy {

    private static final String APP_OSA_PREFIX = "/app";

    @Value("${osa.appBaseUrl}")
    private String appBaseUrl;

    @Override
    protected String calculateRedirectUrl(String contextPath, String url) {
        String redirectUrl = url;
        if (url.startsWith(APP_OSA_PREFIX)) {
            redirectUrl = url.replace(APP_OSA_PREFIX, appBaseUrl);
        }
        return super.calculateRedirectUrl(contextPath, redirectUrl);
    }

}
