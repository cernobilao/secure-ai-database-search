package org.springframework.samples.petclinic.nl.search.session;

import java.util.Map;
import java.util.Properties;

import jakarta.persistence.EntityManager;

class SessionUtil {

	private SessionUtil() {
	}

	static Properties getPropertiesUsingCurrentEntityManager(EntityManager entityManager) {
		Map<String, Object> properties = entityManager.getEntityManagerFactory().getProperties();
		Properties hibernateProperties = new Properties();
		hibernateProperties.putAll(properties);
		return hibernateProperties;
	}

}
