package org.springframework.samples.petclinic.nl.search;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
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
		return nlSearch == null ? new NlSearch() : nlSearch;
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
