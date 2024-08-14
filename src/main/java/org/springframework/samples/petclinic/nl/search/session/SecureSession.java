package org.springframework.samples.petclinic.nl.search.session;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.config.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class SecureSession {

	@Autowired
	private AdminUserSession adminUser;

	@Autowired
	private RestrictedUserSession restrictedUser;

	@PersistenceContext
	private EntityManager entityManager;

	public Session getOpenSession() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth.getPrincipal().getClass().equals(CustomUserDetails.class)) {
			CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
			return restrictedUser.open(principal);
		}
		else {
			return adminUser.open();
		}
	}

}
