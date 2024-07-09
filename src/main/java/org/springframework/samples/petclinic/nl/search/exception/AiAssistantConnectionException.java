package org.springframework.samples.petclinic.nl.search.exception;

public class AiAssistantConnectionException extends Exception {

	public AiAssistantConnectionException(String assistantIdIsNotSet) {
		super(assistantIdIsNotSet);
	}

}
