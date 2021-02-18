package de.hska.iwi.vslab.gateway.security;

import com.google.common.collect.ImmutableList;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.*;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {


    private JwtAccessTokenConverter jwtAccessTokenConverter;
    private TokenStore tokenStore;


    @Override
    public void configure(final HttpSecurity http) throws Exception {

        // @formatter:off
        http.cors().and().csrf().disable().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .antMatchers("/api/oauth/token").permitAll()
                .antMatchers("/api/oauth/authorize").permitAll()
                .antMatchers("/api/oauth/**").permitAll()
                .antMatchers("/actuator/**").permitAll()
                .antMatchers("/api/actuator/**").permitAll()
                .antMatchers("/api/hystrix/**").permitAll()
                .antMatchers("/hystrix/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/users").permitAll()
                .anyRequest().authenticated();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));

        configuration.setAllowedMethods(ImmutableList.of("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(ImmutableList.of("Origin", "X-Requested-With", "Content-Type", "Authorization", "Cache-Control", "Accept"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
    public DefaultTokenServices tokenServices(final TokenStore tokenStore) {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(tokenStore);
        return tokenServices;
    }

    @Bean
    public TokenStore tokenStore() {
        if (tokenStore == null) {
            tokenStore = new JwtTokenStore(jwtAccessTokenConverter());
        }
        return tokenStore;
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        if (jwtAccessTokenConverter != null) {
            return jwtAccessTokenConverter;
        }

        jwtAccessTokenConverter = new JwtAccessTokenConverter();

        jwtAccessTokenConverter.setJwtClaimsSetVerifier(jwtClaimsSetVerifier());

        Resource resource = new ClassPathResource("public.txt");
        String publicKey = null;
        try {
            publicKey = IOUtils.toString(resource.getInputStream());
        }
        catch (final IOException e) { throw new RuntimeException(e);
        }
        jwtAccessTokenConverter.setVerifierKey(publicKey);

        return jwtAccessTokenConverter;
    }


    @Bean
    public JwtClaimsSetVerifier jwtClaimsSetVerifier() {
        return new DelegatingJwtClaimsSetVerifier(Arrays.asList(issuerClaimVerifier(), customJwtClaimVerifier()));
    }

    @Bean
    public JwtClaimsSetVerifier issuerClaimVerifier() {
        try {
            return new IssuerClaimVerifier(new URL("http://nextlevel123.xyz"));
        } catch (final MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public JwtClaimsSetVerifier customJwtClaimVerifier() {
        return new CustomClaimVerifier();
    }


    @Override
    public void configure(final ResourceServerSecurityConfigurer resources) {

        resources.tokenStore(tokenStore);
    }

}