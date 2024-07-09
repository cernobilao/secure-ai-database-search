package org.springframework.samples.petclinic.nl.search.session.orm.mapping;

import org.hibernate.cfg.Configuration;
import org.springframework.samples.petclinic.config.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class MappingConfigurationBuilder {

	public abstract Configuration build();

	public static MappingConfigurationBuilder getBuilderByLoggedUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth.getPrincipal().getClass().equals(CustomUserDetails.class)) {
			CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
			return new ResrictedUser(principal);
		}
		else {
			return new AdminUser();
		}
	}

}
