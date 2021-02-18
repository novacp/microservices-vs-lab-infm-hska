package de.hska.iwi.vslab.gateway;

import de.hska.iwi.vslab.gateway.GatewayClientResponse;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import com.netflix.hystrix.exception.HystrixTimeoutException;

@Component
class GatewayServiceFallback implements FallbackProvider {

    private static final String DEFAULT_MESSAGE = "Service not available (Gateway)";

    @Override
    public String getRoute() {
        return "*";
    }

    @Override
    public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
        if (cause instanceof HystrixTimeoutException) {
            return new GatewayClientResponse(HttpStatus.GATEWAY_TIMEOUT, DEFAULT_MESSAGE);
        } else {
            return new GatewayClientResponse(HttpStatus.INTERNAL_SERVER_ERROR, DEFAULT_MESSAGE);
        }
    }

}