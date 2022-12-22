package ro.andrei.reverseproxy.service.impl;

import ro.andrei.reverseproxy.model.HostInfo;
import ro.andrei.reverseproxy.service.api.LoadBalancer;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class RoundRobinLoadBalancer implements LoadBalancer {

    private final Queue<HostInfo> instances;

    public RoundRobinLoadBalancer(List<HostInfo> instances) {
        this.instances = instances
                .stream()
                .collect(ArrayDeque::new,
                        Queue::offer,
                        Queue::addAll);
    }

    @Override
    public HostInfo getNextInstance() {
        var next = instances.poll();
        instances.offer(next);

        return next;
    }
}
