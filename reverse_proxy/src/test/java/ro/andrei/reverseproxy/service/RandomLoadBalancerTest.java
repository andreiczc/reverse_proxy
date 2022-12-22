package ro.andrei.reverseproxy.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.andrei.reverseproxy.model.HostInfo;
import ro.andrei.reverseproxy.service.impl.RandomLoadBalancer;

import java.util.List;

public class RandomLoadBalancerTest {

    private RandomLoadBalancer randomLoadBalancer;
    private final List<HostInfo> instances = List.of(new HostInfo("test1", 9000, false),
            new HostInfo("test2", 9001, false));

    @BeforeEach
    public void beforeEach() {
        this.randomLoadBalancer = new RandomLoadBalancer(instances);
    }

    @Test
    public void whenCallingGetNextInstanceAnInstanceIsReturned() {
        var instance = randomLoadBalancer.getNextInstance();

        Assertions.assertNotNull(instance);
    }
}
