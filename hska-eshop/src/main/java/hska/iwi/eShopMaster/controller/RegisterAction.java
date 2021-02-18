package hska.iwi.eShopMaster.controller;

import hska.iwi.eShopMaster.model.Product;
import hska.iwi.eShopMaster.model.User;

import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class RegisterAction extends ActionSupport {

    /**
     *
     */
    private static final long serialVersionUID = 3655805600703279195L;
    private String username;
    private String password1;
    private String password2;
    private String firstname;
    private String lastname;
    
    private boolean role = false;

    private String productsAPI = "http://zuul:8100/api/users";
    
    @Override
    public String execute() throws Exception {

        OAuthRestTemplate restTemplate = new OAuthRestTemplate();

        User user = new User();
        user.setUsername(getUsername());
        user.setPassword(getPassword1());
        user.setFirstname(getFirstname());
        user.setLastname(getLastname());

        HttpEntity<?> request = new HttpEntity(user, restTemplate.getHeaders());

        ResponseEntity<Product> ur = restTemplate.postForEntity(productsAPI, request , Product.class);
        if(ur.getStatusCode().equals(HttpStatus.CREATED))
        {
            addActionMessage("user registered, please login");
            addActionError("user registered, please login");
            return "success";

        }else{

            if(ur.getStatusCode().equals(HttpStatus.CONFLICT))
            {
                addActionError(getText("error.username.alreadyInUse"));
            }else{
                addActionError("Ein Fehler ist aufgetreten, bitte kontaktieren Sie den Support.");
            }
            return "input";
        }
    }
    
	@Override
	public void validate() {
		if (getFirstname().length() == 0) {
			addActionError(getText("error.firstname.required"));
		}
		if (getLastname().length() == 0) {
			addActionError(getText("error.lastname.required"));
		}
		if (getUsername().length() == 0) {
			addActionError(getText("error.username.required"));
		}
		if (getPassword1().length() == 0) {
			addActionError(getText("error.password.required"));
		}
		if (getPassword2().length() == 0) {
			addActionError(getText("error.password.required"));
		}
		
		if (!getPassword1().equals(getPassword2())) {
			addActionError(getText("error.password.notEqual"));
		}
	}

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getUsername() {
        return (this.username);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword1() {
        return (this.password1);
    }

    public void setPassword1(String password) {
        this.password1 = password;
    }
    
    public String getPassword2() {
        return (this.password2);
    }

    public void setPassword2(String password) {
        this.password2 = password;
    }
    
    public boolean getRole() {
        return (this.role);
    }
    
    public void setRole(boolean role) {
        this.role = role;
    }

}
