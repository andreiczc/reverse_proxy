package ro.andrei.reverseproxy.service.api;

import org.springframework.web.client.RestTemplate;
import ro.andrei.reverseproxy.model.HostInfo;

public interface HostService {

    boolean isHostValid(String host);

    HostInfo getHostInstance(String host);

    RestTemplate getRestTemplate(String host);

}
