package hska.iwi.eShopMaster.controller;

import com.opensymphony.xwork2.ActionContext;
import hska.iwi.eShopMaster.model.Product;
import hska.iwi.eShopMaster.model.User;
import com.opensymphony.xwork2.ActionSupport;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Map;

public class ProductDetailsAction extends ActionSupport {
	
	private User user;
	private Long id;
	private String searchValue;
	private Integer searchMinPrice;
	private Integer searchMaxPrice;
	private Product product;

	private static final long serialVersionUID = 7708747680872125699L;

	private String productsAPI = "http://zuul:8100/api/catalog/products";

	public String execute() {

		Map<String, Object> session = ActionContext.getContext().getSession();
		if(!session.containsKey("access_token")) return "login";
		User user = (User) session.get("webshop_user");
		if(user == null) return "login";

		OAuthRestTemplate restTemplate = new OAuthRestTemplate();
		restTemplate.authenticate();

		HttpEntity request = new HttpEntity(restTemplate.getHeaders());

		ResponseEntity<Product> ps = restTemplate.exchange(
				productsAPI + "/" + id,
				HttpMethod.GET,
				request,
				Product.class);

		if(ps.getStatusCode().equals(HttpStatus.OK))
		{
			// Go and get new Category list
			this.product = ps.getBody();
			return "success";
		}else if (ps.getStatusCode().equals(HttpStatus.ALREADY_REPORTED))
		{
			this.product = ps.getBody();

			addActionError("Leider haben wir Probleme, den neusten Stand des Produkts abzurufen. Der Support wurde informiert, versuchen Sie es später erneut.");

			return "success";
		}else{
			addActionError("Ein Fehler ist aufgetreten, bitte kontaktieren Sie den Support.");
			return "input";
		}
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSearchValue() {
		return searchValue;
	}

	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}

	public Integer getSearchMinPrice() {
		return searchMinPrice;
	}

	public void setSearchMinPrice(Integer searchMinPrice) {
		this.searchMinPrice = searchMinPrice;
	}

	public Integer getSearchMaxPrice() {
		return searchMaxPrice;
	}

	public void setSearchMaxPrice(Integer searchMaxPrice) {
		this.searchMaxPrice = searchMaxPrice;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
}
