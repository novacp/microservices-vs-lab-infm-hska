package de.hska.iwi.vslab.user.security;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.*;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailService userDetailsService;
    private JwtAccessTokenConverter jwtAccessTokenConverter;
    private TokenStore tokenStore;


    @Autowired
    public AuthorizationServerConfiguration(final BCryptPasswordEncoder passwordEncoder,
                                            final AuthenticationManager authenticationManager,
                                            final UserDetailService userDetailsService) {
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public TokenStore tokenStore() {
        if (tokenStore == null) {
            tokenStore = new JwtTokenStore(jwtAccessTokenConverter());
        }
        return tokenStore;
    }

    @Bean
    public DefaultTokenServices tokenServices(final TokenStore tokenStore,
                                              final ClientDetailsService clientDetailsService) {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setTokenStore(tokenStore);
        tokenServices.setClientDetailsService(clientDetailsService);
        tokenServices.setAuthenticationManager(this.authenticationManager);
        return tokenServices;
    }


    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        if (jwtAccessTokenConverter != null) {
            return jwtAccessTokenConverter;
        }

        jwtAccessTokenConverter = new JwtAccessTokenConverter();

        final KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(
                new ClassPathResource("mytest.jks"), "mypass".toCharArray()
        );
        jwtAccessTokenConverter.setKeyPair(keyStoreKeyFactory.getKeyPair("mytest"));
        jwtAccessTokenConverter.setJwtClaimsSetVerifier(jwtClaimsSetVerifier());

        Resource resource = new ClassPathResource("public.txt");
        String publicKey;
        try {
            publicKey = IOUtils.toString(resource.getInputStream());
        }
        catch (final IOException e) { throw new RuntimeException(e);
        }
        jwtAccessTokenConverter.setVerifierKey(publicKey);

        return jwtAccessTokenConverter;

    }

    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {// @formatter:off
        clients.inMemory()
            .withClient("client1")
            .secret(passwordEncoder.encode("secret"))
            .authorizedGrantTypes("password", "refresh_token", "client_credentials")
            .scopes("read", "write")
            .accessTokenValiditySeconds(86400) // 1 day
            .refreshTokenValiditySeconds(2592000) // 30 days
        ;
    } // @formatter:on

    /*
    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
        clients.jdbc(this.dataSource).passwordEncoder(this.passwordEncoder);
    }*/

    @Override
    public void configure(final AuthorizationServerEndpointsConfigurer endpoints) {
        final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), jwtAccessTokenConverter()));

        endpoints.authenticationManager(this.authenticationManager)
                // already on tokenEnhancer: .accessTokenConverter(jwtAccessTokenConverter())
                .tokenEnhancer(tokenEnhancerChain)
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST)
                .userDetailsService(this.userDetailsService)
                .tokenStore(tokenStore());
    }


    @Override
    // secure oauth endpoints. All other endpoints are secured through WebSecurityConfiguration
    // /oauth/check_token and /oauth/token_key
    public void configure(final AuthorizationServerSecurityConfigurer oauthServer) {
        oauthServer.passwordEncoder(this.passwordEncoder).tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()").allowFormAuthenticationForClients();
    }

   /* @Bean
    public DataSourceInitializer dataSourceInitializer() {
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();

        if(Arrays.stream(env.getActiveProfiles()).anyMatch(p -> p.equalsIgnoreCase("dev")))
        {
            resourceDatabasePopulator.addScript(new ClassPathResource("/schema_dev.sql"));
        }else{
            resourceDatabasePopulator.addScript(new ClassPathResource("/schema_prod.sql"));
        }

        resourceDatabasePopulator.addScript(new ClassPathResource("/data_oauth.sql"));

        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(this.dataSource);
        dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);
        return dataSourceInitializer;
    }
*/
    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new CustomTokenEnhancer();
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

}
