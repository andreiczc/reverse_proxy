package ro.andrei.reverseproxy.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HostInfo {

    @NotBlank
    private String address;

    @Min(1)
    @Max(65535)
    private int port;

    private boolean secure = false;
}
