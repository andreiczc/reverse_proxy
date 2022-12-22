package ro.andrei.reverseproxy.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class RetryAwareRestTemplateInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RetryAwareRestTemplateInterceptor.class);

    private final Integer noRetries;

    public RetryAwareRestTemplateInterceptor(Integer noRetries) {
        this.noRetries = noRetries;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        var currRetries = noRetries;

        while (currRetries != 0) {
            try {
                log.info("Retries left for /{} {}: {}",
                        request.getMethod(), request.getURI(), currRetries);
                return execution.execute(request, body);
            } catch (IOException e) {
                log.info("Call to /{} {} failed. Waiting 5 seconds before retrying", request.getMethod(), request.getURI());

                if (--currRetries == 0) {
                    throw e;
                }

                attemptSleep();
            }
        }

        return null;
    }

    private void attemptSleep() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ignored) {

        }
    }
}
