package org.springframework.samples.petclinic.nl.search.session;

import static org.springframework.samples.petclinic.nl.search.session.SessionUtil.getPropertiesUsingCurrentEntityManager;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.owner.Visit;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;

@Component
class AdminUserSession {

	private final SessionFactory sessionFactory;

	AdminUserSession(EntityManager entityManager) {
		Configuration configuration = new Configuration().addAnnotatedClass(Owner.class)
			.addAnnotatedClass(Visit.class)
			.addAnnotatedClass(PetType.class)
			.addAnnotatedClass(Pet.class);
		configuration.setProperties(getPropertiesUsingCurrentEntityManager(entityManager));

		this.sessionFactory = configuration.buildSessionFactory();
	}

	public Session open() {
		return this.sessionFactory.openSession();
	}

}
