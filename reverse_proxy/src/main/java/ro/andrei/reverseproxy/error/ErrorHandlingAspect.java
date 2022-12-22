package ro.andrei.reverseproxy.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import ro.andrei.reverseproxy.util.Constants;

import java.io.IOException;

@Component
@Aspect
public class ErrorHandlingAspect {

    private static final Logger log = LoggerFactory.getLogger(ErrorHandlingAspect.class);

    @Pointcut("execution(* ro.andrei.reverseproxy.interceptor.*.preHandle(..))")
    public void interceptorPointcut() {

    }

    @Around("interceptorPointcut()")
    public Object aroundInterceptor(ProceedingJoinPoint joinPoint) {
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            handleException(joinPoint, e);

            return false; // stop execution
        }
    }

    private void handleException(ProceedingJoinPoint joinPoint, Throwable e) {
        try {
            var args = joinPoint.getArgs();
            var request = (HttpServletRequest) args[0];
            var response = (HttpServletResponse) args[1];

            logMessage(request, e);

            response.setStatus(500);
            response.getWriter().write(Constants.ERROR_MESSAGE);
        } catch (ClassCastException exception) {
            log.error("Unknown parameter list encountered");
        } catch (IOException ex) {
            log.error("Exception while trying to obtain response writer: {}\n{}",
                    ex.getMessage(),
                    ex.getStackTrace());
        }
    }

    private void logMessage(HttpServletRequest request, Throwable e) {
        if (e instanceof UnregisteredHostException) {
            log.error(Constants.UNREGISTERED_HOST_EXCEPTION_MESSAGE,
                    request.getMethod(), request.getRequestURI());
        } else if (e instanceof RestClientException) {
            log.error(Constants.REST_EXCEPTION_MESSAGE, request.getMethod(), request.getRequestURI());
        } else {
            log.error(Constants.GENERAL_EXCEPTION_MESSAGE,
                    request.getMethod(), request.getRequestURI(), e.getStackTrace());
        }
    }
}
