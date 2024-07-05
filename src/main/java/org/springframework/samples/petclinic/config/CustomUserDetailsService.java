package org.springframework.samples.petclinic.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailsService implements UserDetailsService {

	private CustomUserDetails customUser;

	public CustomUserDetailsService(CustomUserDetails customUser) {
		this.customUser = customUser;
	}

	@Override
	public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return customUser;
	}
}
