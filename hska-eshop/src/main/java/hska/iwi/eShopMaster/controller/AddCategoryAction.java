package hska.iwi.eShopMaster.controller;

import com.opensymphony.xwork2.ActionContext;
import hska.iwi.eShopMaster.model.Category;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.ActionSupport;
import hska.iwi.eShopMaster.model.User;
import org.springframework.http.*;

public class AddCategoryAction extends ActionSupport {

	private static final long serialVersionUID = -6704600867133294378L;
	
	private String newCatName = null;
	
	private List<Category> categories;
	
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

		// create category
		Category category = new Category();
		category.setName(getNewCatName());
		HttpEntity<?> request = new HttpEntity(category, restTemplate.getHeaders());

		ResponseEntity<Category> rc = restTemplate.postForEntity(categoriesAPI, request, Category.class);

		// category created
		if(rc.getStatusCode().equals(HttpStatus.CREATED))
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
				addActionError("Die Kategorie wurde hinzugefügt, leider haben wir Probleme, eine Liste aktualisierter Kategorien abzurufen. Der Support wurde informiert, versuchen Sie es später erneut.");

				return "success";
			}else{
				// General error
				addActionError("Die Kategorie wurde hinzugefügt, leider haben wir Probleme, eine Liste aller Kategorien abzurufen. Der Support wurde informiert, versuchen Sie es später erneut.");

				return "success";
			}

		}else{
			addActionError("Ein Fehler ist aufgetreten, die Kategorie wurde nicht hinzugefügt. Der Support wurde informiert, versuchen Sie es später erneut.");
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
	public void validate(){
		if (getNewCatName().length() == 0) {
			addActionError(getText("error.catname.required"));
		}
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}
	
	public String getNewCatName() {
		return newCatName;
	}

	public void setNewCatName(String newCatName) {
		this.newCatName = newCatName;
	}
}
