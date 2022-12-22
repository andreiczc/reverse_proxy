package ro.andrei.reverseproxy.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.client.RestTemplate;
import ro.andrei.reverseproxy.service.api.LoadBalancer;

import java.util.List;

@Data
@NoArgsConstructor
public class ServiceInfo {

    @NotBlank
    private String name;

    @NotBlank
    private String domain;

    private LoadBalancingPolicy lbPolicy = LoadBalancingPolicy.RANDOM;

    private boolean tcpHealthCheck = false;

    @Min(0)
    @Max(5)
    private int retries = 0;

    @Min(0)
    @Max(60000)
    private int timeout = 0;

    @NotEmpty
    private List<HostInfo> hosts;

    private LoadBalancer loadBalancer;

    private RestTemplate restTemplate;
}
