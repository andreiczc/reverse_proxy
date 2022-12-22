package ro.andrei.reverseproxy.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HealthCheck {
    private boolean healthy;
}
