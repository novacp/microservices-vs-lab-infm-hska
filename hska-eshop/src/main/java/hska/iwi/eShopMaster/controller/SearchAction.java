package hska.iwi.eShopMaster.controller;

import com.opensymphony.xwork2.ActionContext;
import hska.iwi.eShopMaster.model.Category;
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
import org.springframework.web.util.UriComponentsBuilder;

public class SearchAction extends ActionSupport{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6565401833074694229L;
	
	
	private String searchDescription = null;
	private String searchMinPrice;
	private String searchMaxPrice;
	
	private List<Product> products;

	private String searchAPI = "http://zuul:8100/api/catalog/search";

	public String execute() {

		Map<String, Object> session = ActionContext.getContext().getSession();
		if(!session.containsKey("access_token")) return "login";
		User user = (User) session.get("webshop_user");
		if(user == null) return "login";

		OAuthRestTemplate restTemplate = new OAuthRestTemplate();
		restTemplate.authenticate();

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(searchAPI);

		if (searchDescription != null && searchDescription.trim().length() > 0) {
			builder.queryParam("details", searchDescription);
		}

		if (searchMinPrice != null) {
			builder.queryParam("minPrice", searchMinPrice);
		}

		if (searchMaxPrice != null) {
			builder.queryParam("maxPrice", searchMaxPrice);
		}

		HttpEntity<?> request = new HttpEntity(restTemplate.getHeaders());

		ResponseEntity<Product[]> cs = restTemplate.exchange(
				builder.toUriString(),
				HttpMethod.GET,
				request,
				Product[].class);

		if(cs.getStatusCode().equals(HttpStatus.OK))
		{
			// Go and get new Category list
			this.setProducts(Arrays.asList(cs.getBody()));
			return "success";
		}else if (cs.getStatusCode().equals(HttpStatus.ALREADY_REPORTED))
		{
			// Go and get new Category list
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
		

	public String getSearchValue() {
		return searchDescription;
	}


	public void setSearchValue(String searchValue) {
		this.searchDescription = searchValue;
	}


	public String getSearchMinPrice() {
		return searchMinPrice;
	}


	public void setSearchMinPrice(String searchMinPrice) {
		this.searchMinPrice = searchMinPrice;
	}


	public String getSearchMaxPrice() {
		return searchMaxPrice;
	}


	public void setSearchMaxPrice(String searchMaxPrice) {
		this.searchMaxPrice = searchMaxPrice;
	}

}
