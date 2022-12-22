package ro.andrei.reverseproxy.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ro.andrei.reverseproxy.error.InstanceNotHealthyException;
import ro.andrei.reverseproxy.interceptor.LoggingRestInterceptor;
import ro.andrei.reverseproxy.interceptor.RetryAwareRestTemplateInterceptor;
import ro.andrei.reverseproxy.model.HealthCheck;
import ro.andrei.reverseproxy.model.HostInfo;
import ro.andrei.reverseproxy.service.impl.RandomLoadBalancer;
import ro.andrei.reverseproxy.service.impl.RoundRobinLoadBalancer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class ServiceConfigurer {

    private static final Logger log = LoggerFactory.getLogger(ServiceConfigurer.class);


    private final ProxyConfiguration proxyConfiguration;
    private final ScheduledExecutorService executorService;

    public ServiceConfigurer(ProxyConfiguration proxyConfiguration) {
        this.proxyConfiguration = proxyConfiguration;
        this.executorService = Executors.newScheduledThreadPool(1);
    }

    @PostConstruct
    private void configureServices() {
        var services = proxyConfiguration.getServices();

        // set load balancer
        for (var service : services) {
            var loadBalancer = switch (service.getLbPolicy()) {
                case RANDOM -> new RandomLoadBalancer(service.getHosts());
                case ROUND_ROBIN -> new RoundRobinLoadBalancer(service.getHosts());
            };

            service.setLoadBalancer(loadBalancer);
            service.setRestTemplate(configureRestTemplate(service.getTimeout(), service.getRetries()));

            //sanitize addresses
            service.getHosts().forEach(this::sanitizeUrl);
        }

        executorService.scheduleAtFixedRate(this::runTcpHealthCheck, 60, 60, TimeUnit.SECONDS);
    }

    private void runTcpHealthCheck() {
        var services = proxyConfiguration.getServices();
        for (var service : services) {
            if (!service.isTcpHealthCheck()) {
                continue;
            }

            var restTemplate = service.getRestTemplate();
            for (var instance : service.getHosts()) {
                var uri = String.format("%s://%s:%d/healthCheck",
                        instance.isSecure() ? "https" : "http", instance.getAddress(), instance.getPort());
                var method = HttpMethod.GET;

                var healthCheckResponse = restTemplate.exchange(uri, method, null, HealthCheck.class);
                if (!healthCheckResponse.hasBody()) {
                    throw new InstanceNotHealthyException(service.getName(), instance);
                }

                var healthCheck = healthCheckResponse.getBody();
                if (healthCheck.isHealthy()) {
                    log.info("Service {} with instance {}:{} is healthy", service.getName(),
                            instance.getAddress(), instance.getPort());
                } else {
                    log.error("Service {} with instance {}:{} is not healthy", service.getName(),
                            instance.getAddress(), instance.getPort());
                }
            }

        }
    }

    private void sanitizeUrl(HostInfo hostInfo) {
        try {
            var url = new URL(hostInfo.getAddress());
            hostInfo.setAddress(url.getHost());
        } catch (MalformedURLException ignored) {
            return;
        }
    }

    private RestTemplate configureRestTemplate(int timeout, int retries) {
        var factory = new SimpleClientHttpRequestFactory();
        if (timeout != 0) {
            factory.setConnectTimeout(timeout);
            factory.setReadTimeout(timeout);
        }

        var restTemplate = new RestTemplate(factory);
        restTemplate.getInterceptors().add(new LoggingRestInterceptor());
        if (retries != 0) {
            restTemplate.getInterceptors().add(new RetryAwareRestTemplateInterceptor(retries));
        }

        return restTemplate;
    }
}
