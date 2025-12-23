package org.springframework.samples.petclinic.nl.search;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.conversations.Conversation;
import com.openai.models.conversations.ConversationCreateParams;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.ResponseInputItem;
import com.openai.models.responses.ResponseInputText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.samples.petclinic.nl.search.exception.AiAssistantConnectionException;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class OpenAiAssistant implements AiAssistant {

	private final OpenAIClient client;

	private final String instructions;

	private static Boolean isOk;

	private static Logger logger = LoggerFactory.getLogger(OpenAiAssistant.class);

	public OpenAiAssistant(@Value("${openai.api.key}") String apiKey) {

		ClassPathResource resource = new ClassPathResource("nl.search/openai-assistant-instructions.txt");
		try {
			instructions = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}

		this.client = OpenAIOkHttpClient.builder().apiKey(apiKey).build();

		isOk = testConnectionIsOk();
	}

	public Boolean isOk() {
		return isOk;
	}

	private Boolean testConnectionIsOk() {
		try {
			Response response = this.client.responses()
				.create(ResponseCreateParams.builder()
					.model("gpt-4.1-mini") // small + cheap + widely available
					.input("Say OK")
					.build());

			String output = response.toString();
			logger.info("API connection successful! Assistant says: " + output);

		}
		catch (Exception e) {
			logger.warn("API connection failed ❌");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public String getHql(String userInput, String conversationId) throws Exception {
		if (conversationId == null || conversationId.isEmpty()) {
			return getHql(userInput);
		}
		return getNewHqlFromConversation(userInput, conversationId);
	}

	@Override
	public String getHql(String userInput) throws Exception {
		if (!isOk()) {
			logger.info("OpenAI Assistant is available.");
			return "SELECT p.name, p.birthDate, p.type FROM Pet p";
		}
		String conversationId = createConversation();
		if (conversationId == null) {
			logger.error("Failed to create conversation");
			throw new AiAssistantConnectionException("Failed to create conversation");
		}
		return getNewHqlFromConversation(userInput, conversationId);
	}

	@Override
	public String createConversation() throws Exception {
		Conversation conversation = client.conversations()
			.create(ConversationCreateParams.builder()
				.addItem(ResponseInputItem.Message.builder()
					.addContent(ResponseInputText.builder().text(instructions).build())
					.role(ResponseInputItem.Message.Role.SYSTEM)
					.build())
				.build());
		return conversation.id();
	}

	private String getNewHqlFromConversation(String userInput, String conversationId) throws Exception {
		Response response = client.responses()
			.create(ResponseCreateParams.builder()
				.conversation(conversationId)
				.input(userInput)
				.model("gpt-4o")
				.build());

		String output = response.output().get(0).message().get().content().get(0).asOutputText().text();
		return output;
	}

}
