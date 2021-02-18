package hska.iwi.eShopMaster.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User  {

	private String username;
	private String firstname;
	private String lastname;
	private String password;
	private boolean admin;

	private Role role;

	public User(String username, String firstname, String lastname, String password, boolean admin) {
		this.username = username;
		this.firstname = firstname;
		this.lastname = lastname;
		this.password = password;
		this.admin = admin;
	}
}
