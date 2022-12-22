package ro.andrei.reverseproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;

@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class})
public class ReverseProxyApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReverseProxyApplication.class, args);
    }

}
