package hska.iwi.eShopMaster.controller;

import com.opensymphony.xwork2.ActionContext;
import hska.iwi.eShopMaster.model.Category;
import hska.iwi.eShopMaster.model.User;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.ActionSupport;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class InitCategorySiteAction extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1108136421569378914L;

	private String pageToGoTo;
	private User user;

	private List<Category> categories;

	private final String categoriesAPI = "http://zuul:8100/api/catalog/categories";
	private RestTemplate restTemplate;

	public String execute() throws Exception {

		Map<String, Object> session = ActionContext.getContext().getSession();

		// check that user is logged in and admin, if not, redirect to login page
		if(!session.containsKey("access_token")) return "login";
		User user = (User) session.get("webshop_user");
		if(user == null || !user.isAdmin()) return "login";

		OAuthRestTemplate restTemplate = new OAuthRestTemplate();
		restTemplate.authenticate();

		HttpEntity request2 = new HttpEntity(restTemplate.getHeaders());

		ResponseEntity<Category[]> cs = restTemplate.exchange(
				categoriesAPI,
				HttpMethod.GET,
				request2,
				Category[].class);

		if(cs.getStatusCode().equals(HttpStatus.OK))
		{
			// Go and get new Category list
			this.setCategories(Arrays.asList(cs.getBody()));

			if(pageToGoTo != null){
				if(pageToGoTo.equals("c")){
					return "successC";
				}
				else if(pageToGoTo.equals("p")){
					return "successP";
				}
			}

			return "input";
		}else if (cs.getStatusCode().equals(HttpStatus.ALREADY_REPORTED))
		{
			// Go and get new Category list
			this.setCategories(Arrays.asList(cs.getBody()));

			if(pageToGoTo != null){
				if(pageToGoTo.equals("c")){
					return "successC";
				}
				else if(pageToGoTo.equals("p")){
					return "successP";
				}
			}

			addActionError("Leider haben wir Probleme, eine Liste aktualisierter Kategorien abzurufen. Der Support wurde informiert, versuchen Sie es später erneut.");

			return "success";
		}else{
			addActionError("Ein Fehler ist aufgetreten, bitte kontaktieren Sie den Support.");
			return "input";
		}
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	public String getPageToGoTo() {
		return pageToGoTo;
	}

	public void setPageToGoTo(String pageToGoTo) {
		this.pageToGoTo = pageToGoTo;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
