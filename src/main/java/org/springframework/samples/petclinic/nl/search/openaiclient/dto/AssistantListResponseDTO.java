package org.springframework.samples.petclinic.nl.search.openaiclient.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AssistantListResponseDTO(String object, List<AssistantResponseDTO> data, String firstId, String lastId,
		boolean hasMore) {
}
