package de.hska.iwi.vslab.user.security;

import java.util.HashMap;
import java.util.Map;

import de.hska.iwi.vslab.user.model.User;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

public class CustomTokenEnhancer implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {

        final Map<String, Object> additionalInfo = new HashMap<>();

        User p  = (User) authentication.getPrincipal();

        additionalInfo.put("firstname", p.getFirstname());
        additionalInfo.put("lastname", p.getLastname());
        additionalInfo.put("username", p.getUsername());
        additionalInfo.put("admin", p.getAdmin());

        additionalInfo.put("iss", "http://nextlevel123.xyz");
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        return accessToken;
    }

}