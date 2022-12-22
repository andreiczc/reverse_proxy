package ro.andrei.reverseproxy.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import ro.andrei.reverseproxy.error.UnregisteredHostException;
import ro.andrei.reverseproxy.service.api.HostService;
import ro.andrei.reverseproxy.util.Constants;

@Component
public class RequestValidatorInterceptor implements HandlerInterceptor {

    private final HostService hostService;

    public RequestValidatorInterceptor(HostService hostService) {
        this.hostService = hostService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws UnregisteredHostException {
        var header = request.getHeader(Constants.HOST_HEADER);
        var valid = hostService.isHostValid(header);
        if(!valid) {
            throw new UnregisteredHostException();
        }

        return true;
    }
}
