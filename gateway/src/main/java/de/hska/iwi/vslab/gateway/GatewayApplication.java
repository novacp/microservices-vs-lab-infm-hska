package de.hska.iwi.vslab.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@SpringBootApplication
@EnableZuulProxy
@EnableDiscoveryClient

// The Zuul proxy uses Ribbon for load balancing and the requests execute in the Hystrix command.
// As a result, failures in the Zuul route appear in a Hystrix matrix.
@EnableHystrixDashboard
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

}