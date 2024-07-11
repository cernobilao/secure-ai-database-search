package org.springframework.samples.petclinic.nl.search;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.config.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class NlSearchController {

	@Autowired
	private NlSearchWithHibernate nlSearchWithHibernate;

	@ModelAttribute("nlSearch")
	public NlSearch addModel(NlSearch nlSearch) {
		if (nlSearch == null) {
			nlSearch = new NlSearch();
		}
		Boolean openAiAssistantIsOk = OpenAiAssistant.isOk();
		if (!openAiAssistantIsOk) {
			nlSearch.setAssistantInfoMessage(
					"OpenAI assistant is not available. Make sure the Open API key is configured. You can test only the "
							+ "authorized access to pets.");
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth.getPrincipal().getClass().equals(CustomUserDetails.class)) {
			nlSearch.setLoggedInUserInfoMessage("You are logged as an owner. You can see only your pet.");
		}
		else {
			nlSearch.setLoggedInUserInfoMessage("You are logged as Admin. You can see all pets.");
		}
		return nlSearch;
	}

	@GetMapping("/nl-search/find")
	public String initNlSearchForm() {
		return "nl.search/nlSearch";
	}

	@GetMapping("/nl-search")
	public String processNlSearchForm(@RequestParam(defaultValue = "1") int page, NlSearch nlSearch, Model model) {
		nlSearch = nlSearchWithHibernate.getResults(nlSearch);

		if (nlSearch.getErrorMessage() == null) {
			List<String> columnNamesFromHql = getColumnNamesFromHql(nlSearch.getAiResponse());
			nlSearch.setColumnNames(columnNamesFromHql);
		}
		return addPaginationModel(page, model);
	}

	private String addPaginationModel(int page, Model model) {
		model.addAttribute("currentPage", page);
		return "nl.search/nlSearchList";
	}

	private List<String> getColumnNamesFromHql(String hql) {
		Pattern pattern = Pattern.compile("SELECT\\s+(.*?)\\s+FROM", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(hql);
		if (matcher.find()) {
			String columns = matcher.group(1);
			return Arrays.asList(columns.split(",\\s*"));
		}
		return null;
	}

}
