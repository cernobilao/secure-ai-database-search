package org.springframework.samples.petclinic.nl.search;

import static java.lang.Thread.sleep;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	public static final String ASSISTANT_NAME = "User Query to HQL Translator";

	private static Logger logger = LoggerFactory.getLogger(OpenAiAssistant.class);

	private static String assistantId;

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

	public static Boolean isOk() {
		if (assistantId == null || assistantId.isEmpty()) {
			return false;
		}
		return true;
	}

	private String createAssistantIfDoesNotExist() {
		try {
			Optional<AssistantResponseDTO> existingAssistant = client.listAssistants()
				.stream()
				.filter(assistant -> ASSISTANT_NAME.equals(assistant.name()))
				.findFirst();
			if (existingAssistant.isPresent()) {
				logger.info("Found existing assistant with id " + assistantId);
				return existingAssistant.get().id();
			}
			else {
				ClassPathResource resource = new ClassPathResource("nl.search/openai-assistant-instructions.txt");
				String instructions = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
				AssistantResponseDTO assistantResponseDTO = client.createAssistant(ASSISTANT_NAME, instructions);
				logger.info("Created new assistant with id " + assistantId);
				return assistantResponseDTO.id();
			}
		}
		catch (Exception e) {
			logger.warn(
					"Failed to create Open AI Assistant. Make sure that the API key is set in application.properties.",
					e);
			return null;
		}
	}

	@Override
	public String getHql(String userInput, String threadId) throws Exception {
		if (threadId == null || threadId.isEmpty()) {
			return getHql(userInput);
		}
		return getNewHqlFromConversationThread(userInput, threadId);
	}

	@Override
	public String getHql(String userInput) throws Exception {
		if (!isOk()) {
			logger.info("OpenAI Assistant is available.");
			return "SELECT p.name, p.birthDate, p.type FROM Pet p";
		}
		ThreadResponseDTO thread = client.createThread();
		if (thread.id() == null) {
			logger.error("Failed to create thread");
			throw new AiAssistantConnectionException("Failed to create thread");
		}
		return getNewHqlFromConversationThread(userInput, thread.id());
	}

	@Override
	public String createThread() throws Exception {
		ThreadResponseDTO thread = client.createThread();
		if (thread.id() == null) {
			logger.error("Failed to create thread");
			throw new AiAssistantConnectionException("Failed to create thread");
		}
		return thread.id();
	}

	private String getNewHqlFromConversationThread(String userInput, String threadId) throws Exception {
		client.sendMessage(threadId, "user", userInput);
		RunResponseDTO run = client.runMessage(threadId, assistantId);

		waitUntilRunIsFinished(client, threadId, run, DELAY);

		MessagesListResponseDTO allResponses = client.getMessages(threadId);
		logger.info("These are all the messages and you will be billed by OpenAI for every single one of them:");
		log(allResponses);
		MessageResponseDTO assistantMessage = allResponses.data()
			.stream()
			.filter(message -> "assistant".equals(message.role()))
			.findFirst()
			.get();
		return assistantMessage.content().get(0).text().value();
	}

	private static void waitUntilRunIsFinished(AssistantAIClient client, String threadId, RunResponseDTO run,
			long DELAY) throws InterruptedException {
		while (!isRunDone(client, threadId, run.id())) {
			superviseWorkInProgress(client, threadId);
			sleep(DELAY * 1000);
		}
	}

	private static void superviseWorkInProgress(AssistantAIClient client, String threadId) {
		try {
			logger.info("Checking messages to supervise assistant's work");
			MessagesListResponseDTO messages = client.getMessages(threadId);
			log(messages);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void log(MessagesListResponseDTO messages) {
		messages.data().forEach(message -> logger.info(message.toString()));
	}

	private static boolean isRunDone(AssistantAIClient client, String threadId, String runId) {
		RunResponseDTO status;
		try {
			status = client.getRunStatus(threadId, runId);
			logger.info("Status of your run is currently " + status);
			return isRunStateFinal(status);
		}
		catch (Exception e) {
			logger.error("Failed to get run state, will retry...", e);
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
