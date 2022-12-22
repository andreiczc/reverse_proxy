package ro.andrei.reverseproxy.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import ro.andrei.reverseproxy.util.Constants;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
public class LoggingInterceptorTest {

    private LoggingInterceptor interceptor;

    @BeforeEach
    public void beforeEach() {
        this.interceptor = new LoggingInterceptor();
    }

    @Test
    public void testCorrectLogMessageIsGenerated(CapturedOutput output) throws Exception {
        // arrange
        var request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getMethod())
                .thenReturn("GET");
        Mockito.when(request.getRequestURI())
                .thenReturn("/test");
        Mockito.when(request.getHeader(Constants.HOST_HEADER))
                .thenReturn("testHost");

        // act
        interceptor.preHandle(request, null, null);

        // assert
        Assertions.assertTrue(output.getOut().contains("Incoming request: /GET /test. Host testHost"));
    }

}
