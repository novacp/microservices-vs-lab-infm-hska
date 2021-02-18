package hska.iwi.eShopMaster.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hska.iwi.eShopMaster.model.Role;
import hska.iwi.eShopMaster.model.User;
import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class LoginAction extends ActionSupport {

	private String username = null;
	private String password = null;

	private String tokenAPI = "http://zuul:8100/api/oauth/token";
	private RestTemplate restTemplate;

	@Override
	public String execute() throws Exception {

		Map<String, Object> session = ActionContext.getContext().getSession();
		this.restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
		map.add("username", getUsername());
		map.add("password", getPassword());
		map.add("client_id", "client1");
		map.add("client_secret", "secret");
		map.add("grant_type", "password");

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

		ResponseEntity<String> response = restTemplate.postForEntity(
				tokenAPI, request , String.class
		);

		if(response.getStatusCode().equals(HttpStatus.OK))
		{
			JsonObject jobj = new Gson().fromJson(response.getBody(), JsonObject.class);

			String accessToken = jobj.get("access_token").toString();
			String refreshToken = jobj.get("refresh_token").toString();
			String firstname =  jobj.get("firstname").toString();
			String lastname =  jobj.get("lastname").toString();
			Boolean a = jobj.get("admin").getAsBoolean();

			User user = new User();
			user.setUsername(getUsername());
			user.setFirstname(firstname);
			user.setLastname(lastname);
			user.setAdmin(a);

			Role roleObj = new Role();
			roleObj.setLevel(a.equals(Boolean.TRUE) ? 0 : 1);
			roleObj.setTyp(a.equals(Boolean.TRUE) ? "admin" : "user");
			user.setRole(roleObj);

			session.clear();
			session.put("webshop_user", user);
			session.put("message", "");
			session.put("access_token", accessToken.substring( 1, accessToken.length() - 1 ));
			session.put("refresh_token", refreshToken.substring( 1, refreshToken.length() - 1 ));

			return "success";

		}else{

			if(response.getStatusCode().equals(HttpStatus.BAD_REQUEST))
			{
				addActionError("Ungültige Anmeldedaten");
			}else{
				addActionError("Ein Fehler ist aufgetreten, bitte kontaktieren Sie den Support.");
			}

			return "input";
		}
	}
	
	@Override
	public void validate() {
		if (getUsername().length() == 0) {
			addActionError(getText("error.username.required"));
		}
		if (getPassword().length() == 0) {
			addActionError(getText("error.password.required"));
		}
	}

	public String getUsername() {
		return (this.username);
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return (this.password);
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
