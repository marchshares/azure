package com.bars.orders;

import com.google.common.collect.Maps;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import org.mockito.stubbing.Answer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TestHelper {

    public static void setTestProperties() {
        try {
            try (FileInputStream logFIS = new FileInputStream("src/test/resources/logging.properties")) {
                LogManager.getLogManager().readConfiguration(logFIS);
            }

            try (FileInputStream propFIS = new FileInputStream("src/test/resources/app.properties")) {
                Properties properties = new Properties(System.getProperties());
                properties.load(propFIS);

                System.setProperties(properties);
            }

        } catch (IOException e) {
            //ignore
        }
    }

    public static HttpRequestMessage<Optional<String>> invokeRequest(String funcBody) {
        HttpRequestMessage<Optional<String>> request = mock(HttpRequestMessage.class);

        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("name", "Azure");
        when(request.getQueryParameters()).thenReturn(queryParams);

        doAnswer((Answer<HttpResponseMessage.Builder>) invocation -> {
            HttpStatus status = (HttpStatus) invocation.getArguments()[0];
            return new HttpResponseMessageMock.HttpResponseMessageBuilderMock().status(status);
        }).when(request).createResponseBuilder(any(HttpStatus.class));

        final Optional<String> queryBody = Optional.of(funcBody);
        when(request.getBody()).thenReturn(queryBody);

        when(request.getHeaders()).thenReturn(Maps.newHashMap());

        return request;
    }

    public static ExecutionContext invokeContext() {
        ExecutionContext context = mock(ExecutionContext.class);
        doReturn(Logger.getGlobal()).when(context).getLogger();
        return context;
    }
}
