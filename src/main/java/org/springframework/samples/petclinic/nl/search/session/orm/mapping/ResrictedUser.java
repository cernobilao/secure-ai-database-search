package org.springframework.samples.petclinic.nl.search.session.orm.mapping;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.samples.petclinic.config.CustomUserDetails;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.owner.Visit;

import jakarta.persistence.EntityManager;

class ResrictedUser extends SessionByUserBuilder {

	ResrictedUser(CustomUserDetails principal) {
		this.principal = principal;
	}

	private final CustomUserDetails principal;

	private static final String PET_ORM_MAPPING_XML = readOrmMappingXml("db/nl.search.mapping/pet.hbm.xml");

	@Override
	public Session build(EntityManager entityManager) {
		InputStream petMappingInputStream = new ByteArrayInputStream(
				PET_ORM_MAPPING_XML.getBytes(StandardCharsets.UTF_8));
		Configuration configuration = new Configuration().addInputStream(petMappingInputStream)
			.addAnnotatedClass(Visit.class)
			.addAnnotatedClass(PetType.class)
			.addAnnotatedClass(Owner.class);

		configuration.setProperties(getPropertiesUsingCurrentEntityManager(entityManager));

		SessionFactory customSessionFactory = configuration.buildSessionFactory();
		Session session = customSessionFactory.openSession();
		session.enableFilter("ownerFilter").setParameter("ownerId", principal.getId());
		return session;
	}

	private static String readOrmMappingXml(String fileName) {
		try (InputStream inputStream = ResrictedUser.class.getClassLoader().getResourceAsStream(fileName)) {
			if (inputStream == null) {
				throw new RuntimeException("Resource file not found: " + fileName);
			}

			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
				return reader.lines().collect(Collectors.joining(System.lineSeparator()));
			}
		}
		catch (Exception e) {
			throw new RuntimeException("Error reading resource file: " + fileName, e);
		}
	}

}
