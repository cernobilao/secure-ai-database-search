package org.springframework.samples.petclinic.nl.search.session.orm.mapping;

import org.hibernate.cfg.Configuration;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.owner.Visit;

class AdminUser extends MappingConfigurationBuilder {

	@Override
	public Configuration build() {
		Configuration configuration = new Configuration().addAnnotatedClass(Owner.class)
			.addAnnotatedClass(Visit.class)
			.addAnnotatedClass(PetType.class)
			.addAnnotatedClass(Pet.class);
		return configuration;
	}

}
