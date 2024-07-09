package org.springframework.samples.petclinic.nl.search;

import static java.lang.Thread.sleep;

import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.samples.petclinic.nl.search.exception.AiAssistantConnectionException;
import org.springframework.samples.petclinic.nl.search.openaiclient.AssistantAIClient;
import org.springframework.samples.petclinic.nl.search.openaiclient.dto.MessageResponseDTO;
import org.springframework.samples.petclinic.nl.search.openaiclient.dto.MessagesListResponseDTO;
import org.springframework.samples.petclinic.nl.search.openaiclient.dto.RunResponseDTO;
import org.springframework.samples.petclinic.nl.search.openaiclient.dto.ThreadResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class OpenAiAssistant implements AiAssistant {

	@Value("${openai.assistantId}")
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
	}

	@Override
	public String getHql(String userInput) throws Exception {
		if (assistantId == null || assistantId.isEmpty()) {
			throw new AiAssistantConnectionException("Assistant ID is not set");
		}
		ThreadResponseDTO thread = client.createThread();
		;
		if (thread.id() == null) {
			throw new AiAssistantConnectionException("Failed to create thread");
		}
		client.sendMessage(thread.id(), "user", userInput);
		RunResponseDTO run = client.runMessage(thread.id(), assistantId);

		waitUntilRunIsFinished(client, thread, run, DELAY);

		MessagesListResponseDTO allResponses = client.getMessages(thread.id());
		System.out.println("These are all the messages and you will be billed by OpenAI for every single one of them:");
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
