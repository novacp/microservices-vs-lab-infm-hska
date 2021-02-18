package hska.iwi.eShopMaster.controller;

import com.opensymphony.xwork2.ActionContext;
import hska.iwi.eShopMaster.model.Product;
import hska.iwi.eShopMaster.model.User;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.ActionSupport;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ListAllProductsAction extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -94109228677381902L;
	
	private List<Product> products;

	private String productsAPI = "http://zuul:8100/api/catalog/products";

	// semantically, this could also be a search query without params.
	public String execute() {

		Map<String, Object> session = ActionContext.getContext().getSession();

		// check that user is logged in and admin, if not, redirect to login page
		if(!session.containsKey("access_token")) return "login";
		User user = (User) session.get("webshop_user");
		if(user == null) return "login";

		OAuthRestTemplate restTemplate = new OAuthRestTemplate();
		restTemplate.authenticate();

		HttpEntity request2 = new HttpEntity(restTemplate.getHeaders());

		ResponseEntity<Product[]> cs = restTemplate.exchange(
				productsAPI,
				HttpMethod.GET,
				request2,
				Product[].class);

		if(cs.getStatusCode().equals(HttpStatus.OK))
		{
			// Go and get new Category list
			this.setProducts(Arrays.asList(cs.getBody()));
			return "success";
		}else if (cs.getStatusCode().equals(HttpStatus.ALREADY_REPORTED))
		{
			this.setProducts(Arrays.asList(cs.getBody()));

			addActionError("Leider haben wir Probleme, eine Liste aktualisierter Produkte abzurufen. Der Support wurde informiert, versuchen Sie es später erneut.");

			return "success";
		}else{
			addActionError("Ein Fehler ist aufgetreten, bitte kontaktieren Sie den Support.");
			return "input";
		}
	}

	
	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}
}
