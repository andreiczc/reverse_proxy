package ro.andrei.reverseproxy.service.impl;

import ro.andrei.reverseproxy.model.HostInfo;
import ro.andrei.reverseproxy.service.api.LoadBalancer;

import java.util.List;

public class RandomLoadBalancer implements LoadBalancer {

    private final List<HostInfo> instances;

    public RandomLoadBalancer(List<HostInfo> instances) {
        this.instances = instances;
    }

    @Override
    public HostInfo getNextInstance() {
        var randomIdx = (int) ((Math.random() * 1000) % instances.size());

        return instances.get(randomIdx);
    }
}
