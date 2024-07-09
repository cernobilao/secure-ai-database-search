package org.springframework.samples.petclinic.nl.search.openaiclient.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ThreadResponseDTO(String id, String object, @JsonProperty("created_at") long createdAt,
		Map<String, Object> metadata) {
}
