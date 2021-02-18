package hska.iwi.eShopMaster.controller;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import hska.iwi.eShopMaster.model.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Map;

public class DeleteProductAction extends ActionSupport {

	private static final long serialVersionUID = 3666796923937616729L;

	private Long id;

	private final String productsAPI = "http://zuul:8100/api/catalog/products";

	public String execute() {

		Map<String, Object> session = ActionContext.getContext().getSession();

		// check that user is logged in and admin, if not, redirect to login page
		if(!session.containsKey("access_token")) return "login";
		User user = (User) session.get("webshop_user");
		if(user == null || !user.isAdmin()) return "login";

		OAuthRestTemplate restTemplate = new OAuthRestTemplate();
		restTemplate.authenticate();

		// create category
		HttpEntity<?> request = new HttpEntity(restTemplate.getHeaders());
		ResponseEntity<String> dc = restTemplate.exchange(
				productsAPI + "/" + id,
				HttpMethod.DELETE,
				request,
				String.class);

		if(dc.getStatusCode().equals(HttpStatus.NO_CONTENT))
		{
			return "success";
		}else if(dc.getStatusCode().equals(HttpStatus.NOT_FOUND)){

			addActionError("Das zu löschende Produkt existiert nicht.");
			return "input";
		}else{
			addActionError("Ein Fehler ist aufgetreten, das Produkt wurde nicht gelöscht. Der Support wurde informiert, versuchen Sie es später erneut.");

			return "input";
		}
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
