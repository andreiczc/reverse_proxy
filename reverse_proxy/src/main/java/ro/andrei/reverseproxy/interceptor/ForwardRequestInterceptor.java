package ro.andrei.reverseproxy.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import ro.andrei.reverseproxy.service.api.HostService;
import ro.andrei.reverseproxy.util.Constants;

import java.io.IOException;
import java.util.Collections;


@Component
public class ForwardRequestInterceptor implements HandlerInterceptor {

    private final HostService hostService;

    public ForwardRequestInterceptor(HostService hostService) {
        this.hostService = hostService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        var host = request.getHeader(Constants.HOST_HEADER);
        var instance = hostService.getHostInstance(host);
        var restTemplate = hostService.getRestTemplate(host);

        var uri = String.format("%s://%s:%d%s", instance.isSecure() ? "https" : "http", instance
                        .getAddress(), instance.getPort(), request.getRequestURI());
        var method = HttpMethod.valueOf(request.getMethod());
        var body = createPayload(request);

        var upstreamResponse = restTemplate
                .exchange(uri, method, body, byte[].class);
        convertResponse(response, upstreamResponse);

        return false;
    }

    private void convertResponse(HttpServletResponse response, ResponseEntity<byte[]> receivedResponse) throws IOException {
        response.reset();

        // set body
        if (receivedResponse.hasBody()) {
            var stringResponse = new String(receivedResponse.getBody());
            response.getWriter().write(stringResponse);
        }

        // set status code
        response.setStatus(receivedResponse.getStatusCode().value());

        // set headers
        for (var headerSet : receivedResponse.getHeaders().entrySet()) {
            for (var header : headerSet.getValue()) {
                response.addHeader(headerSet.getKey(), header);
            }
        }
    }

    private HttpEntity<?> createPayload(HttpServletRequest request) throws IOException {
        var content = request.getInputStream().readAllBytes();
        var headers = mapHeaders(request);

        return new HttpEntity<>(content, headers);
    }

    private HttpHeaders mapHeaders(HttpServletRequest request) {
        var headers = new HttpHeaders();
        var headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            var current = headerNames.nextElement();
            headers.addAll(current, Collections.list(request.getHeaders(current)));
        }

        return headers;
    }


}
