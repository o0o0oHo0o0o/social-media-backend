package com.example.SocialMedia.config.auth;

import com.example.SocialMedia.constant.AuthProvider;
import com.example.SocialMedia.constant.OtpChannel;
import com.example.SocialMedia.model.coredata_model.User;
import com.example.SocialMedia.service.auth.AuthService;
import com.example.SocialMedia.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Value("${spring.application.frontend}")
    private String FRONTEND_URL;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(OAuth2LoginSuccessHandler.class);
    private final ApplicationContext applicationContext;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException {
        try {
            OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
            String registrationId = authToken.getAuthorizedClientRegistrationId();
            AuthProvider provider = AuthProvider.valueOf(registrationId.toUpperCase());

            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

            logger.info("OAuth2 Provider: {}", registrationId);
            logger.info("OAuth2 Attributes: {}", oAuth2User.getAttributes());

            String email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");

            if (email == null || email.isEmpty()) {
                logger.error("Email not found in OAuth2 response for provider: {}", registrationId);
                response.sendRedirect(FRONTEND_URL + "/login?error=email_not_found");
                return;
            }

            User user = createAndSetInformation(oAuth2User, email, name, provider);
            AuthService authService = applicationContext.getBean(AuthService.class);

            authService.generateAndSetCookie(response, user);

            response.sendRedirect(FRONTEND_URL + "/oauth2/success");
        } catch (Exception e) {
            logger.error("OAuth2 authentication failed", e);
            response.sendRedirect(FRONTEND_URL + "/login?error=auth_failed");
        }
    }

    private User createAndSetInformation(OAuth2User oAuth2User, String email, String name, AuthProvider provider) {
        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }

        String pictureUrl = extractPictureUrl(oAuth2User, provider);
        return createNewOAuth2User(email, name, pictureUrl, provider);
    }

    private String extractPictureUrl(OAuth2User oAuth2User, AuthProvider provider) {
        String pictureUrl = null;

        try {
            if (provider == AuthProvider.GOOGLE) {
                pictureUrl = oAuth2User.getAttribute("picture");
                logger.info("Google picture URL: {}", pictureUrl);

            } else if (provider == AuthProvider.FACEBOOK) {
                Object pictureObj = oAuth2User.getAttribute("picture");

                if (pictureObj instanceof Map<?, ?> pictureMap) {
                    Object dataObj = pictureMap.get("data");

                    if (dataObj instanceof Map<?, ?> dataMap) {
                        Object urlObj = dataMap.get("url");
                        if (urlObj instanceof String url) {
                            pictureUrl = url;
                            logger.info("Facebook picture URL extracted: {}", pictureUrl);
                        }
                    }
                } else if (pictureObj instanceof String) {
                    pictureUrl = (String) pictureObj;
                    logger.info("Facebook picture URL (direct): {}", pictureUrl);
                }

                if (pictureUrl == null) {
                    logger.warn("Cannot extract Facebook picture. Picture object structure: {}", pictureObj);
                }
            }
        } catch (Exception e) {
            logger.error("Error extracting picture URL for provider {}: {}", provider, e.getMessage());
        }

        return pictureUrl;
    }

    private User createNewOAuth2User(String email, String name, String pictureUrl, AuthProvider authProvider) {
        User user = new User();
        user.setEmail(email);
        user.setUserName(userService.generateUniqueUsername(email, OtpChannel.EMAIL));
        user.setFullName(name);
        user.setProfilePictureURL(pictureUrl);
        user.setCreatedLocalDateTime(LocalDateTime.now());
        user.setAuthProvider(authProvider);
        user.setVerified(true);

        logger.info("Creating new OAuth2 user: email={}, provider={}, pictureUrl={}",
                email, authProvider, pictureUrl);

        return userService.saveUser(user);
    }
}