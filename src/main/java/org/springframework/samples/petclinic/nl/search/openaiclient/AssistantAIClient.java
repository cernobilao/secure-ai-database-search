package org.springframework.samples.petclinic.nl.search.openaiclient;

import static org.springframework.samples.petclinic.nl.search.openaiclient.model.GPTModel.GPT3_5_TURBO;
import static org.springframework.samples.petclinic.nl.search.openaiclient.model.GPTModel.GPT_4O;
import static org.springframework.samples.petclinic.nl.search.openaiclient.model.GPTModel.GPT_4O_MINI;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Properties;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.samples.petclinic.nl.search.openaiclient.dto.*;
import org.springframework.samples.petclinic.nl.search.openaiclient.model.ListAssistantsParams;

public class AssistantAIClient {

	private final String assistantsUrl;

	private final String threadsUrl;

	private final String apiKey;

	private final HttpClient httpClient;

	private final ObjectMapper objectMapper;

	private static final String SLASH = "/";

	public AssistantAIClient(Properties properties) {
		this.apiKey = properties.getProperty("openai.api.key");
		this.assistantsUrl = properties.getProperty("openai.assistants.url");
		this.threadsUrl = properties.getProperty("openai.threads.url");
		this.httpClient = HttpClient.newHttpClient();
		this.objectMapper = new ObjectMapper();
	}

	private String post(String url, Object body) throws Exception {
		String jsonBody = objectMapper.writeValueAsString(body);

		HttpRequest request = getRequestWithHeaders(url).POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();

		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		System.out.println("POST " + url + " status " + response.statusCode());
		return response.body();
	}

	public AssistantResponseDTO createAssistant(String name, String initialPrompt) throws Exception {
		AssistantRequestDTO dto = new AssistantRequestDTO(name, GPT_4O.getName(), initialPrompt);
		String response = post(assistantsUrl, dto);
		return objectMapper.readValue(response, AssistantResponseDTO.class);
	}

	public List<AssistantResponseDTO> listAssistants() throws Exception {
		return listAssistants(assistantsUrl);
	}

	public List<AssistantResponseDTO> listAssistants(ListAssistantsParams listAssistantsParams) throws Exception {
		StringBuilder urlBuilder = new StringBuilder(assistantsUrl).append("?");
		listAssistantsParams.order().ifPresent(order -> urlBuilder.append("order=").append(order).append("&"));
		listAssistantsParams.limit().ifPresent(limit -> urlBuilder.append("limit=").append(limit).append("&"));
		listAssistantsParams.after().ifPresent(after -> urlBuilder.append("after=").append(after).append("&"));
		listAssistantsParams.before().ifPresent(before -> urlBuilder.append("before=").append(before));
		String url = urlBuilder.toString();
		return listAssistants(url);
	}

	private List<AssistantResponseDTO> listAssistants(String url) throws IOException, InterruptedException {
		HttpRequest request = getRequestWithHeaders(url).GET().build();
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		AssistantListResponseDTO assistantsList = objectMapper.readValue(response.body(),
				AssistantListResponseDTO.class);
		return assistantsList.data();
	}

	public ThreadResponseDTO createThread() throws Exception {
		String response = post(threadsUrl, "");
		return objectMapper.readValue(response, ThreadResponseDTO.class);
	}

	public void sendMessage(String threadId, String role, String content) throws Exception {
		String url = threadsUrl + SLASH + threadId + "/messages";
		MessageDTO dto = new MessageDTO(role, content);

		String response = post(url, dto);
		objectMapper.readValue(response, MessageResponseDTO.class);
	}

	public RunResponseDTO runMessage(String threadId, String assistantId) throws Exception {
		String url = threadsUrl + SLASH + threadId + "/runs";
		RunRequestDTO dto = new RunRequestDTO(assistantId);

		String response = post(url, dto);
		return objectMapper.readValue(response, RunResponseDTO.class);
	}

	public RunResponseDTO getRunStatus(String threadId, String runId) throws Exception {
		String url = threadsUrl + SLASH + threadId + "/runs/" + runId;

		HttpRequest request = getRequestWithHeaders(url).GET().build();
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

		return objectMapper.readValue(response.body(), RunResponseDTO.class);
	}

	private HttpRequest.Builder getRequestWithHeaders(String url) {
		return HttpRequest.newBuilder()
			.uri(URI.create(url))
			.header("Authorization", "Bearer " + apiKey)
			.header("OpenAI-Beta", "assistants=v2")
			.header("Content-Type", "application/json");
	}

	public MessagesListResponseDTO getMessages(String threadId) throws Exception {
		String url = threadsUrl + SLASH + threadId + "/messages";

		HttpRequest request = getRequestWithHeaders(url).GET().build();
		System.out.println("Threads link for manual verification of requests: " + url);
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

		return objectMapper.readValue(response.body(), MessagesListResponseDTO.class);
		// Assuming the response is a JSON array of MessageResponseDTO
		// return objectMapper.readValue(response.body(), new
		// TypeReference<List<MessageResponseDTO>>() {});
	}

}
