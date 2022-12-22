package ro.andrei.reverseproxy.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.Date;

public class LoggingRestInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LoggingRestInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        log.info("Attempting call to /{} {}",
                request.getMethod(), request.getURI());

        var startTime = new Date();
        var result = execution.execute(request, body);
        var endTime = new Date();

        log.info("Call to /{} {} has status {}. Request took {} ms",
                request.getMethod(), request.getURI(), result.getStatusCode(),
                endTime.getTime() - startTime.getTime());

        return result;
    }
}
