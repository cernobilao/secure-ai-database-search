package org.springframework.samples.petclinic.nl.search.openaiclient.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AssistantResponseDTO(String id, String object, @JsonProperty("created_at") long createdAt, String name,
		String description, String model, String instructions, List<String> tools,

		String top_p,

		String temperature,

		@JsonProperty("file_ids") List<String> fileIds,

		String response_format, Map<String, Object> metadata) {
}
