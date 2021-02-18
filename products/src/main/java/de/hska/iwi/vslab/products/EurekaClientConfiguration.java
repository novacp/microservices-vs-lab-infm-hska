package de.hska.iwi.vslab.products;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("prod")
@Configuration
@EnableDiscoveryClient
public class EurekaClientConfiguration {
}