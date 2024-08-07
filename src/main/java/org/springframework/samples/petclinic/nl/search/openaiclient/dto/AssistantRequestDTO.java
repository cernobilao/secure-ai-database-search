package org.springframework.samples.petclinic.nl.search.openaiclient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AssistantRequestDTO(String name, String model, String instructions) {
}
