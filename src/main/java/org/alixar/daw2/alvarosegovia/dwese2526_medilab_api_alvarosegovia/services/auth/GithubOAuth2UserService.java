package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.auth;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class GithubOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String login = oAuth2User.getAttribute("login");
        String email = oAuth2User.getAttribute("email");

        if (email == null || email.isBlank()) {
            email = login + "@users.noreply.github.com";
        }
        String normalizedEmail = email.trim().toLowerCase();

        userService.findOrCreateGithubUser(normalizedEmail, login);

        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("email", normalizedEmail);

        return new DefaultOAuth2User(oAuth2User.getAuthorities(), attributes, "login");
    }
}
