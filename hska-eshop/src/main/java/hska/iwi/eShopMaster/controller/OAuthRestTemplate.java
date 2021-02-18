package hska.iwi.eShopMaster.controller;

import com.opensymphony.xwork2.ActionContext;
import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Getter
public class OAuthRestTemplate extends RestTemplate {

    HttpHeaders headers = new HttpHeaders();

    public OAuthRestTemplate() {
        this.setErrorHandler(new CustomResponseErrorHandler());
        this.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    public OAuthRestTemplate(ClientHttpRequestFactory requestFactory) {
        super(requestFactory);
    }

    public OAuthRestTemplate(List<HttpMessageConverter<?>> messageConverters) {
        super(messageConverters);
    }

    public void authenticate()
    {
        Map<String, Object> session = ActionContext.getContext().getSession();
        String accessToken = session.get("access_token").toString();

        headers.add("Authorization", "Bearer " + accessToken);
    }
}
