package org.springframework.samples.petclinic.nl.search.openaiclient.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MessagesListResponseDTO(String object, List<MessageResponseDTO> data,
		@JsonProperty("first_id") String firstId, @JsonProperty("last_id") String lastId, @JsonProperty("has_more")

		boolean hasMore) {
}
