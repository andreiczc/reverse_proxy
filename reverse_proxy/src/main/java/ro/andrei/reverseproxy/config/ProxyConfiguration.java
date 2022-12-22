package ro.andrei.reverseproxy.config;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import ro.andrei.reverseproxy.model.ServiceInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Configuration
@Validated
@ConfigurationProperties(prefix = "proxy")
public class ProxyConfiguration {

    @NotNull
    private List<ServiceInfo> services;

    @Bean
    public Map<String, ServiceInfo> registeredHosts() {
        var result = new HashMap<String, ServiceInfo>();

        for(var service : this.services) {
            result.put(service.getDomain(), service);
        }

        return result;
    }
}