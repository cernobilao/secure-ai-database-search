package org.springframework.samples.petclinic.nl.search;

import org.hibernate.Session;
import org.hibernate.query.SelectionQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.nl.search.exception.AiAssistantConnectionException;
import org.springframework.samples.petclinic.nl.search.session.SecureSession;
import org.springframework.stereotype.Component;

@Component
public class NlSearchWithHibernate {

	@Autowired
	private SecureSession secureSession;

	@Autowired
	AiAssistant aiAssistant;

	public NlSearch getResults(NlSearch nlSearch) {
		String hqlFromAssistant = null;
		try {
			nlSearch.getConversationHistory().add(nlSearch.getUserQuery());
			if (nlSearch.getConversationId() == null && aiAssistant.isOk()) {
				String conversationId = aiAssistant.createConversation();
				nlSearch.setConversationId(conversationId);
			}
			hqlFromAssistant = aiAssistant.getHql(nlSearch.getUserQuery(), nlSearch.getConversationId());

		}
		catch (AiAssistantConnectionException e) {
			nlSearch.setErrorMessage(
					"Error connecting to AI assistant. Have you added the open AI key and other configuration to "
							+ "application.properties? Error: " + e.getMessage());
			return nlSearch;
		}
		catch (Exception e) {
			nlSearch.setErrorMessage(e.getMessage());
			return nlSearch;
		}

		nlSearch.getConversationHistory().add(hqlFromAssistant);
		nlSearch.setAiResponse(hqlFromAssistant);

		if (hqlFromAssistant != null && hqlFromAssistant.trim().toLowerCase().startsWith("select ")) {
			try {
				Session session = secureSession.getOpenSession();
				SelectionQuery<Object[]> selectionQuery = session.createSelectionQuery(hqlFromAssistant,
						Object[].class);
				nlSearch.setSearchResult(selectionQuery.getResultList());
				session.close();
			}
			catch (Exception e) {
				nlSearch.setErrorMessage(e.getMessage());
			}
		}

		return nlSearch;
	}

}
