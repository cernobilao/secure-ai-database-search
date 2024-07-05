package org.springframework.samples.petclinic.nl.search;

import org.hibernate.Session;
import org.hibernate.query.SelectionQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.nl.search.session.OnTheFlySecureSessionBuilder;
import org.springframework.stereotype.Component;

@Component
public class NlSearchWithHibernate {

	@Autowired
	private OnTheFlySecureSessionBuilder onTheFlySecureSessionBuilder;

	public NlSearch getResults(NlSearch nlSearch) {
		String hqlFromAssistant = "SELECT p.name FROM Pet p";
		nlSearch.setAiResponse(hqlFromAssistant);

		Session session = onTheFlySecureSessionBuilder.getSession();
		SelectionQuery<Object[]> selectionQuery = session.createSelectionQuery(hqlFromAssistant, Object[].class);
		nlSearch.setSearchResult(selectionQuery.getResultList());
		return nlSearch;
	}

}
