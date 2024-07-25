package org.springframework.samples.petclinic.nl.search.openaiclient.model;

public enum ListOrder {

	ASC("asc"), DESC("desc");

	private final String value;

	ListOrder(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}

}
