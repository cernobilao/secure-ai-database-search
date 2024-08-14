package org.springframework.samples.petclinic.nl.search.session.orm.mapping;

import java.util.Map;
import java.util.Properties;

import org.hibernate.Session;
import org.springframework.samples.petclinic.config.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.persistence.EntityManager;

public abstract class SessionByUserBuilder {

	public abstract Session build(EntityManager entityManager);

	public static SessionByUserBuilder getBuilderByLoggedUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth.getPrincipal().getClass().equals(CustomUserDetails.class)) {
			CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
			return new ResrictedUser(principal);
		}
		else {
			return new AdminUser();
		}
	}

	Properties getPropertiesUsingCurrentEntityManager(EntityManager entityManager) {
		Map<String, Object> properties = entityManager.getEntityManagerFactory().getProperties();
		Properties hibernateProperties = new Properties();
		hibernateProperties.putAll(properties);
		return hibernateProperties;
	}

}
