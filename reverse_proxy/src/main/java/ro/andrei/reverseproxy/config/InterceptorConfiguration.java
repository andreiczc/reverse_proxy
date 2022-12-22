package ro.andrei.reverseproxy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ro.andrei.reverseproxy.interceptor.ForwardRequestInterceptor;
import ro.andrei.reverseproxy.interceptor.RequestValidatorInterceptor;
import ro.andrei.reverseproxy.interceptor.LoggingInterceptor;

@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer {

    private final RequestValidatorInterceptor requestValidatorInterceptor;
    private final LoggingInterceptor loggingInterceptor;
    private final ForwardRequestInterceptor forwardRequestInterceptor;

    public InterceptorConfiguration(RequestValidatorInterceptor requestValidatorInterceptor, LoggingInterceptor loggingInterceptor, ForwardRequestInterceptor forwardRequestInterceptor) {
        this.requestValidatorInterceptor = requestValidatorInterceptor;
        this.loggingInterceptor = loggingInterceptor;
        this.forwardRequestInterceptor = forwardRequestInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
                .addInterceptor(requestValidatorInterceptor)
                .order(1);

        registry
                .addInterceptor(loggingInterceptor)
                .order(2);

        registry
                .addInterceptor(forwardRequestInterceptor)
                .order(2);
    }
}
