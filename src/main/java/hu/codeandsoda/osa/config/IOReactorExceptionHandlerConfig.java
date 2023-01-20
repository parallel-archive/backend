package hu.codeandsoda.osa.config;

import java.io.IOException;

import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.nio.reactor.IOReactorExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IOReactorExceptionHandlerConfig {

    private static Logger logger = LoggerFactory.getLogger(IOReactorExceptionHandlerConfig.class);

    @Bean
    public DefaultConnectingIOReactor defaultConnectingIOReactor() {
        DefaultConnectingIOReactor ioReactor;
        try {
            ioReactor = new DefaultConnectingIOReactor();
        } catch (IOReactorException e) {
            throw new RuntimeException(e);
        }
        ioReactor.setExceptionHandler(new IOReactorExceptionHandler() {

            @Override
            public boolean handle(IOException e) {
                logger.error("IOException occurs in callback, handled by default exception handler and the I/O reactor will be stopped.", e);
                return true; // Return true to note this exception as handled, it will not be re-thrown
            }

            @Override
            public boolean handle(RuntimeException e) {
                logger.error("RuntimeException occurs in callback, handled by default exception handler and the I/O reactor will be resumed.", e);
                return true; // Return true to note this exception as handled, it will not be re-thrown
            }
        });

        return ioReactor;
    }

}
