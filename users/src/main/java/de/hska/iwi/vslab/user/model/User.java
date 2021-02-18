package de.hska.iwi.vslab.user.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Data
public class User implements UserDetails {

	@Id
	@Column(name = "username", nullable = false)
	private String username;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "admin", nullable = false)
	private boolean admin;

	@Column(name = "lastname", nullable = false)
	private String lastname;

	@Column(name = "firstname", nullable = false)
	private String firstname;


	public User() {

	}

	public User(String username, String firstname, String lastname, String password, boolean admin) {
		this.password = password;
		this.lastname = lastname;
		this.firstname = firstname;
		this.username = username;
		this.admin = admin;
	}

	public String getUsername() {
		return username;
	}

	public String getFirstname() {
		return firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public boolean getAdmin() {
		return admin;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> grantedAuthority = new ArrayList<>();
		if(this.getAdmin()){
			grantedAuthority.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		}
		grantedAuthority.add(new SimpleGrantedAuthority("ROLE_USER"));
		return grantedAuthority;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
