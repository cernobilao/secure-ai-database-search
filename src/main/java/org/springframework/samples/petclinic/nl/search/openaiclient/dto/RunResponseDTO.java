package org.springframework.samples.petclinic.nl.search.openaiclient.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RunResponseDTO(String id, String object, @JsonProperty("created_at") long createdAt,
		@JsonProperty("assistant_id") String assistantId, @JsonProperty("thread_id") String threadId,

		String status, @JsonProperty("started_at") Long startedAt, // Using Long to handle
																	// null values
		@JsonProperty("expires_at") Long expiresAt, @JsonProperty("cancelled_at") Long cancelledAt,
		@JsonProperty("failed_at") Long failedAt, @JsonProperty("completed_at") Long completedAt,

		String required_action,

		@JsonProperty("last_error") String lastError, String model, String instructions, List<String> tools,
		@JsonProperty("file_ids") List<String> fileIds,

		String temperature, String top_p, String max_completion_tokens,

		String max_prompt_tokens,

		Map<String, Object> truncation_strategy,

		String incomplete_details,

		Object usage,

		String response_format,

		String tool_choice,

		Map<String, Object> metadata) {
}
