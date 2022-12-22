package ro.andrei.reverseproxy.interceptor;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import ro.andrei.reverseproxy.service.api.HostService;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
public class ForwardRequestInterceptorTest {

    private ForwardRequestInterceptor interceptor;

    @BeforeEach
    public void beforeEach() {
        HostService hostService = Mockito.mock(HostService.class);
        this.interceptor = new ForwardRequestInterceptor(hostService);
    }

    @Test
    public void testHeadersAreMappedCorrectly() {
        // arrange
        var header1 = "header1";
        var header2 = "header2";

        var value1 = "test1";
        var value2 = "test2";

        var request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeaderNames())
                .thenReturn(Collections.enumeration(List.of(header1, header2)));
        Mockito.when(request.getHeaders(header1))
                .thenReturn(Collections.enumeration(List.of(value1)));
        Mockito.when(request.getHeaders(header2))
                .thenReturn(Collections.enumeration(List.of(value2)));

        // act
        var headers = (HttpHeaders) ReflectionTestUtils.invokeMethod(interceptor, "mapHeaders", request);

        // assert
        Assertions.assertNotNull(headers);
        Assertions.assertEquals(2, headers.entrySet().size());
        Assertions.assertEquals(value1, headers.get(header1).get(0));
        Assertions.assertEquals(value2, headers.get(header2).get(0));
    }

    @Test
    public void testPayloadIsCreatedCorrectly() throws IOException {
        // arrange
        var payload = "hello world";
        var request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getInputStream())
                .thenReturn(new ServletInputStream() {
                    @Override
                    public byte[] readAllBytes() throws IOException {
                        return payload.getBytes();
                    }

                    @Override
                    public boolean isFinished() {
                        return false;
                    }

                    @Override
                    public boolean isReady() {
                        return false;
                    }

                    @Override
                    public void setReadListener(ReadListener readListener) {

                    }

                    @Override
                    public int read() throws IOException {
                        return 0;
                    }
                });
        Mockito.when(request.getHeaderNames())
                .thenReturn(Collections.enumeration(List.of()));

        // act
        var entity = (HttpEntity<?>) ReflectionTestUtils
                .invokeMethod(interceptor, "createPayload", request);

        // assert
        Assertions.assertNotNull(entity);
        Assertions.assertTrue(entity.hasBody());
        Assertions.assertArrayEquals(payload.getBytes(), (byte[]) entity.getBody());
    }

    @Test
    public void testResponseIsExtractedCorrectly() throws IOException {
        // arrange
        var expectedResponse = "hello world";
        var expectedStatusCode = HttpStatusCode.valueOf(200);
        var expectedHeaderName = "test1";
        var expectedHeaderValue = "value1";

        var response = Mockito.mock(HttpServletResponse.class);
        var receivedResponse = Mockito.mock(ResponseEntity.class);

        var stringWriter = new StringWriter();
        var writer = new PrintWriter(stringWriter);
        var statusCodeList = new ArrayList<Integer>();

        Mockito.when(receivedResponse.hasBody())
                .thenReturn(true);
        Mockito.when(receivedResponse.getBody())
                .thenReturn(expectedResponse.getBytes());

        Mockito.when(response.getWriter())
                .thenReturn(writer);

        Mockito.when(receivedResponse.getStatusCode())
                .thenReturn(expectedStatusCode);

        Mockito.doAnswer(invocationOnMock -> {
            statusCodeList.add(invocationOnMock.getArgument(0));

            return null;
        }).when(response).setStatus(anyInt());

        var headers = new HttpHeaders();
        headers.put(expectedHeaderName, List.of(expectedHeaderValue));
        Mockito.when(receivedResponse.getHeaders())
                .thenReturn(headers);

        var receivedHeadersMap = new HashMap<String, List<String>>();
        Mockito.doAnswer(invocationOnMock -> {
            var key = (String) invocationOnMock.getArgument(0);
            var value = (String) invocationOnMock.getArgument(1);

            var list = receivedHeadersMap.computeIfAbsent(key, (k) -> new ArrayList<>());
            list.add(value);

            return null;
        }).when(response).addHeader(anyString(), anyString());

        // act
        ReflectionTestUtils
                .invokeMethod(interceptor, "convertResponse", response, receivedResponse);

        // arrange
        // check body
        Assertions.assertEquals(expectedResponse, stringWriter.getBuffer().toString());
        writer.close();
        stringWriter.close();

        // check status code
        Assertions.assertEquals(1, statusCodeList.size());
        Assertions.assertEquals(expectedStatusCode.value(), statusCodeList.get(0));

        // check headers
        Assertions.assertEquals(1, receivedHeadersMap.keySet().size());
        Assertions.assertNotNull(receivedHeadersMap.get(expectedHeaderName));
        Assertions.assertEquals(1, receivedHeadersMap.get(expectedHeaderName).size());
        Assertions.assertEquals(expectedHeaderValue, receivedHeadersMap.get(expectedHeaderName).get(0));
    }
}
