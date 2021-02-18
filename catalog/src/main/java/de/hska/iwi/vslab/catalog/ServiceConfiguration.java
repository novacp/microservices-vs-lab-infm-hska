package de.hska.iwi.vslab.catalog;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class ServiceConfiguration {

    @Value("${service.categories}")
    private String categoriesCoreServiceUrl;

    @Value("${service.products}")
    private String productsCoreServiceUrl;

    @Value("${service.redis}")
    private String redis;
}