package org.springframework.samples.petclinic.nl.search;

import static java.lang.Thread.sleep;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.samples.petclinic.nl.search.exception.AiAssistantConnectionException;
import org.springframework.samples.petclinic.nl.search.openaiclient.AssistantAIClient;
import org.springframework.samples.petclinic.nl.search.openaiclient.dto.AssistantResponseDTO;
import org.springframework.samples.petclinic.nl.search.openaiclient.dto.MessageResponseDTO;
import org.springframework.samples.petclinic.nl.search.openaiclient.dto.MessagesListResponseDTO;
import org.springframework.samples.petclinic.nl.search.openaiclient.dto.RunResponseDTO;
import org.springframework.samples.petclinic.nl.search.openaiclient.dto.ThreadResponseDTO;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

@Component
public class OpenAiAssistant implements AiAssistant {

	private String assistantId;

	long DELAY = 3;

	private AssistantAIClient client;

	public OpenAiAssistant(@Value("${openai.api.key}") String apiKey,
			@Value("${openai.assistants.url}") String assistantsUrl,
			@Value("${openai.threads.url}") String threadsUrl) {

		Properties properties = new Properties();
		properties.setProperty("openai.api.key", apiKey);
		properties.setProperty("openai.assistants.url", assistantsUrl);
		properties.setProperty("openai.threads.url", threadsUrl);
		this.client = new AssistantAIClient(properties);
		String assistantId = createAssistantIfDoesNotExist();
		this.assistantId = assistantId;
	}

	private String createAssistantIfDoesNotExist() {
		try {
			Optional<AssistantResponseDTO> existingAssistant = client.listAssistants()
				.stream()
				.filter(assistant -> "User Query to HQL Translator".equals(assistant.name()))
				.findFirst();
			if (existingAssistant.isPresent()) {
				System.out.println("Found existing assistant with id " + assistantId);
				return existingAssistant.get().id();
			}
			else {
				ClassPathResource resource = new ClassPathResource("nl.search/openai-assistant-instructions.txt");
				String instructions = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
				AssistantResponseDTO assistantResponseDTO = client.createAssistant("NL database search", instructions);
				System.out.println("Created new assistant with id " + assistantId);
				return assistantResponseDTO.id();
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getHql(String userInput) throws Exception {
		if (assistantId == null || assistantId.isEmpty()) {
			throw new AiAssistantConnectionException("Assistant ID is not set");
		}
		ThreadResponseDTO thread = client.createThread();
		if (thread.id() == null) {
			throw new AiAssistantConnectionException("Failed to create thread");
		}
		client.sendMessage(thread.id(), "user", userInput);
		RunResponseDTO run = client.runMessage(thread.id(), assistantId);

		waitUntilRunIsFinished(client, thread, run, DELAY);

		MessagesListResponseDTO allResponses = client.getMessages(thread.id());
		System.out
			.println("These are all the messages and you will be billed by OpenAI for every single one of " + "them:");
		log(allResponses);
		MessageResponseDTO assistantMessage = allResponses.data()
			.stream()
			.filter(message -> "assistant".equals(message.role()))
			.findFirst()
			.get();
		return assistantMessage.content().get(0).text().value();
	}

	private static void waitUntilRunIsFinished(AssistantAIClient client, ThreadResponseDTO thread, RunResponseDTO run,
			long DELAY) throws InterruptedException {
		while (!isRunDone(client, thread.id(), run.id())) {
			superviseWorkInProgress(client, thread);
			sleep(DELAY * 1000);
		}
	}

	private static void superviseWorkInProgress(AssistantAIClient client, ThreadResponseDTO thread) {
		try {
			System.out.println("Checking messages to supervise assistant's work");
			MessagesListResponseDTO messages = client.getMessages(thread.id());
			log(messages);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void log(MessagesListResponseDTO messages) {
		messages.data().forEach(System.out::println);
	}

	private static boolean isRunDone(AssistantAIClient client, String threadId, String runId) {
		RunResponseDTO status;
		try {
			status = client.getRunStatus(threadId, runId);
			System.out.println("Status of your run is currently " + status);
			return isRunStateFinal(status);
		}
		catch (Exception e) {
			System.err.println("Failed to get run state, will retry..." + e);
			e.printStackTrace();
			return false;
		}
	}

	private static boolean isRunStateFinal(RunResponseDTO runResponseDTO) {
		List<String> finalStates = List.of("cancelled", "failed", "completed", "expired");
		String runStatus = Optional.of(runResponseDTO).map(RunResponseDTO::status).orElse("unknown").toLowerCase();
		return finalStates.contains(runStatus);
	}

}
