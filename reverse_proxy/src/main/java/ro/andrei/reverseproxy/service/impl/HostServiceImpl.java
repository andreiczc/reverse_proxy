package ro.andrei.reverseproxy.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ro.andrei.reverseproxy.model.HostInfo;
import ro.andrei.reverseproxy.model.ServiceInfo;
import ro.andrei.reverseproxy.service.api.HostService;

import java.util.Map;

@Service
public class HostServiceImpl implements HostService {

    private final Map<String, ServiceInfo> registeredHosts;

    public HostServiceImpl(Map<String, ServiceInfo> registeredHosts) {
        this.registeredHosts = registeredHosts;
    }

    @Override
    public boolean isHostValid(String host) {
        return registeredHosts.containsKey(host);
    }

    @Override
    public HostInfo getHostInstance(String host) {
        return registeredHosts
                .get(host)
                .getLoadBalancer()
                .getNextInstance();
    }

    @Override
    public RestTemplate getRestTemplate(String host) {
        return registeredHosts
                .get(host)
                .getRestTemplate();
    }

}
