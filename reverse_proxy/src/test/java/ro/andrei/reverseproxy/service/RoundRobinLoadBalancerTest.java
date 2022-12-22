package ro.andrei.reverseproxy.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.andrei.reverseproxy.model.HostInfo;
import ro.andrei.reverseproxy.service.impl.RoundRobinLoadBalancer;

import java.util.List;

public class RoundRobinLoadBalancerTest {

    private RoundRobinLoadBalancer roundRobinLoadBalancer;
    private final List<HostInfo> instances = List.of(new HostInfo("test1", 9000, false),
            new HostInfo("test2", 9001, false));

    @BeforeEach
    public void beforeEach() {
        this.roundRobinLoadBalancer = new RoundRobinLoadBalancer(instances);
    }

    @Test
    public void whenCallingGetNextInstanceAnInstanceIsReturned() {
        var instance = roundRobinLoadBalancer.getNextInstance();

        Assertions.assertNotNull(instance);
    }

    @Test
    public void whenCallingGetNextInstanceADifferentInstanceIsReturned() {
        var instance = roundRobinLoadBalancer.getNextInstance();

        Assertions.assertNotSame(instance, roundRobinLoadBalancer.getNextInstance());
    }

}
