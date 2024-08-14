package org.springframework.samples.petclinic.nl.search.session;

import org.hibernate.Session;
import org.springframework.samples.petclinic.nl.search.session.orm.mapping.SessionByUserBuilder;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class SecureSessionBuilder {

	@PersistenceContext
	private EntityManager entityManager;

	public Session getSession() {
		return SessionByUserBuilder.getBuilderByLoggedUser().build(entityManager);
	}

}
