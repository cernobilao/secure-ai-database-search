package org.springframework.samples.petclinic.nl.search;

public interface AiAssistant {

	Boolean isOk();

	String getHql(String userInput) throws Exception;

	String getHql(String userInput, String conversationId) throws Exception;

	String createConversation() throws Exception;

}
