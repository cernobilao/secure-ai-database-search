package org.springframework.samples.petclinic.nl.search.session;

import java.util.Map;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.samples.petclinic.nl.search.session.orm.mapping.MappingConfigurationBuilder;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class OnTheFlySecureSessionBuilder {

	@PersistenceContext
	private EntityManager entityManager;

	public Session getSession() {
		Configuration configuration =  MappingConfigurationBuilder
			.getBuilderByLoggedUser()
			.build();
		configuration.setProperties(getPropertiesUsingCurrentEntityManager());

		SessionFactory customSessionFactory = configuration.buildSessionFactory();
		return customSessionFactory.openSession();
	}

	private Properties getPropertiesUsingCurrentEntityManager() {
		Map<String, Object> properties = entityManager.getEntityManagerFactory().getProperties();
		Properties hibernateProperties = new Properties();
		hibernateProperties.putAll(properties);
		return hibernateProperties;
	}

}
