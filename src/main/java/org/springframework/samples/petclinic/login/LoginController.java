package org.springframework.samples.petclinic.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.config.WebSecurityConfig;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@GetMapping("/login-owner")
	public String loginOwner(HttpServletRequest request) {
		try {
			UsernamePasswordAuthenticationToken authToken =
				new UsernamePasswordAuthenticationToken(WebSecurityConfig.USER_NAME, WebSecurityConfig.PASSWORD);
			Authentication authentication = authenticationManager.authenticate(authToken);
			if (authentication.isAuthenticated()) {
				SecurityContextHolder.getContext().setAuthentication(authentication);
				HttpSession session = request.getSession(true);
				session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
					SecurityContextHolder.getContext());

				return "redirect:/nl-search";
			}
		} catch (AuthenticationException e) {
			return "redirect:/oups";
		}

		return "redirect:/oups";
	}
}
