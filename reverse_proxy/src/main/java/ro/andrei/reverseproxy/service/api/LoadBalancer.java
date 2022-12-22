package ro.andrei.reverseproxy.service.api;

import ro.andrei.reverseproxy.model.HostInfo;

public interface LoadBalancer {

    HostInfo getNextInstance();
}
