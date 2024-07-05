package org.springframework.samples.petclinic.config;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails  implements UserDetails {

	public CustomUserDetails(final UserDetails user) {
		this.userDetails = user;
	}

	private UserDetails userDetails;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return userDetails.getAuthorities();
	}

	@Override
	public String getPassword() {
		return userDetails.getPassword();
	}

	@Override
	public String getUsername() {
		return userDetails.getUsername();
	}

	public String getId() {
		return "1";
	}
}
