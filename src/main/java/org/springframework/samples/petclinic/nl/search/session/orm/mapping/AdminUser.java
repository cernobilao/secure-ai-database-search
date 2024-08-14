package org.springframework.samples.petclinic.nl.search.session.orm.mapping;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.owner.Visit;

import jakarta.persistence.EntityManager;

class AdminUser extends SessionByUserBuilder {

	@Override
	public Session build(EntityManager entityManager) {
		Configuration configuration = new Configuration().addAnnotatedClass(Owner.class)
			.addAnnotatedClass(Visit.class)
			.addAnnotatedClass(PetType.class)
			.addAnnotatedClass(Pet.class);
		configuration.setProperties(getPropertiesUsingCurrentEntityManager(entityManager));

		SessionFactory customSessionFactory = configuration.buildSessionFactory();
		Session session = customSessionFactory.openSession();
		return session;
	}

}
