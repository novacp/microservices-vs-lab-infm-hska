package hska.iwi.eShopMaster.controller;

import com.opensymphony.xwork2.ActionContext;
import hska.iwi.eShopMaster.model.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.ActionSupport;
import org.springframework.http.*;

public class AddProductAction extends ActionSupport {

	private static final long serialVersionUID = 39979991339088L;

	private String name = null;
	private String price = null;
	private Long categoryId = 0L;
	private String details = null;

	private List<Category> categories;

	private final String productsAPI = "http://zuul:8100/api/catalog/products";
	private final String categoriesAPI = "http://zuul:8100/api/catalog/categories";

	public String execute() {

		Map<String, Object> session = ActionContext.getContext().getSession();

		// check that user is logged in and admin, if not, redirect to login page
		if(!session.containsKey("access_token")) return "login";
		User user = (User) session.get("webshop_user");
		if(user == null || !user.isAdmin()) return "login";

		// Prepare request to api
		OAuthRestTemplate restTemplate = new OAuthRestTemplate();
		restTemplate.authenticate();

		Product product = new Product();
		product.setCategoryId(getCategoryId());
		product.setName(name);
		product.setPrice(Double.parseDouble(price));
		product.setDetails(details);

		HttpEntity<?> request = new HttpEntity(product, restTemplate.getHeaders());

		ResponseEntity<Product> pr = restTemplate.postForEntity(productsAPI, request , Product.class);
		if(pr.getStatusCode().equals(HttpStatus.CREATED))
		{
			// reload all categories. Careful, this might fail due to service unavailable.
			ResponseEntity<Category[]> newCategories = this.getNewCategories();

			if(newCategories.getStatusCode().equals(HttpStatus.OK))
			{
				// Go and get new Category list
				this.setCategories(Arrays.asList(newCategories.getBody()));
				return "success";

			}else if (newCategories.getStatusCode().equals(HttpStatus.ALREADY_REPORTED))
			{
				// Go and get new Category list
				this.setCategories(Arrays.asList(newCategories.getBody()));
				addActionError("Das Produkt wurde hinzugefügt, leider haben wir Probleme, eine Liste aktualisierter Kategorien abzurufen. Der Support wurde informiert, versuchen Sie es später erneut.");

				return "success";
			}else{
				// General error
				addActionError("Das Produkt wurde hinzugefügt, leider haben wir Probleme, eine Liste aller Kategorien abzurufen. Der Support wurde informiert, versuchen Sie es später erneut.");

				return "success";
			}
		}else if(pr.getStatusCode().equals(HttpStatus.NOT_FOUND)){

			addActionError("Diese Kategorie existiert nicht.");
			return "input";
		}else{
			addActionError("Ein Fehler ist aufgetreten, das Produkt wurde nicht hinzugefügt. Der Support wurde informiert, versuchen Sie es später erneut.");
			return "input";
		}
	}

	private ResponseEntity<Category[]> getNewCategories()
	{
		OAuthRestTemplate restTemplate = new OAuthRestTemplate();
		restTemplate.authenticate();

		HttpEntity request2 = new HttpEntity(restTemplate.getHeaders());

		ResponseEntity<Category[]> cs = restTemplate.exchange(
				categoriesAPI,
				HttpMethod.GET,
				request2,
				Category[].class);

		return cs;
	}

	@Override
	public void validate() {

		if (getName() == null || getName().length() == 0) {
			addActionError(getText("error.product.name.required"));
		}

		// Validate price:

		if (String.valueOf(getPrice()).length() > 0) {
			if (!getPrice().matches("[0-9]+(.[0-9][0-9]?)?")
					|| Double.parseDouble(getPrice()) < 0.0) {
				addActionError(getText("error.product.price.regex"));
			}
		} else {
			addActionError(getText("error.product.price.required"));
		}

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}
}