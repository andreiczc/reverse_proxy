package ro.andrei.reverseproxy.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.andrei.reverseproxy.error.UnregisteredHostException;
import ro.andrei.reverseproxy.service.api.HostService;
import ro.andrei.reverseproxy.util.Constants;

@ExtendWith(MockitoExtension.class)
public class RequestValidatorInterceptorTest {

    private static final String HOST_VALUE = "test1";

    private RequestValidatorInterceptor interceptor;
    private HttpServletRequest request;
    private HostService hostService;

    @BeforeEach
    public void beforeEach() {
        this.hostService = Mockito.mock(HostService.class);
        this.interceptor = new RequestValidatorInterceptor(hostService);

        this.request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader(Constants.HOST_HEADER))
                .thenReturn(HOST_VALUE);
    }

    @Test
    public void testHostIsValid() throws UnregisteredHostException {
        // arrange
        Mockito.when(hostService.isHostValid(HOST_VALUE))
                .thenReturn(true);

        // act
        var returnValue = interceptor.preHandle(request, null, null);

        // assert
        Assertions.assertTrue(returnValue);
    }

    @Test
    public void testExceptionIsThrownWhenHostIsInvalid() {
        Mockito.when(hostService.isHostValid(HOST_VALUE))
                .thenReturn(false);

        Assertions.assertThrows(UnregisteredHostException.class, () -> {
            interceptor.preHandle(request, null, null);
        });
    }
}
