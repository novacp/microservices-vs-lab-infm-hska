package hska.iwi.eShopMaster.controller;

import java.io.IOException;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class CustomResponseErrorHandler implements ResponseErrorHandler {

	public boolean hasError(ClientHttpResponse response) throws IOException {
		return false;
	}

	public void handleError(ClientHttpResponse response) throws IOException {
		System.err.println("error");
		System.err.println(response.getStatusText());
		System.err.println(response.getBody());
	}

}
