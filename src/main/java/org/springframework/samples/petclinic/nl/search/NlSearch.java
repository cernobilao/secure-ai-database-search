package org.springframework.samples.petclinic.nl.search;

import java.util.List;

public class NlSearch {

	String assistantInfoMessage;

	String loggedInUserInfoMessage;

	String userQuery;

	List<Object[]> searchResult;

	String aiResponse;

	List<String> columnNames;

	String errorMessage;

	public String getUserQuery() {
		return userQuery;
	}

	public void setUserQuery(String userQuery) {
		this.userQuery = userQuery;
	}

	public List<Object[]> getSearchResult() {
		return searchResult;
	}

	public void setSearchResult(List<Object[]> searchResult) {
		this.searchResult = searchResult;
	}

	public String getAiResponse() {
		return aiResponse;
	}

	public void setAiResponse(String aiResponse) {
		this.aiResponse = aiResponse;
	}

	public List<String> getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getAssistantInfoMessage() {
		return assistantInfoMessage;
	}

	public void setAssistantInfoMessage(String assistantInfoMessage) {
		this.assistantInfoMessage = assistantInfoMessage;
	}

	public String getLoggedInUserInfoMessage() {
		return loggedInUserInfoMessage;
	}

	public void setLoggedInUserInfoMessage(String loggedInUserInfoMessage) {
		this.loggedInUserInfoMessage = loggedInUserInfoMessage;
	}

}
