package org.springframework.samples.petclinic.nl.search;

public interface AiAssistant {

	String getHql(String userInput) throws Exception;

	String getHql(String userInput, String threadId) throws Exception;

	String createThread() throws Exception;

}
