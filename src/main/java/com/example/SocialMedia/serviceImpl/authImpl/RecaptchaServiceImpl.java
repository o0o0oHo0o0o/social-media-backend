package com.example.SocialMedia.serviceImpl.authImpl;

import com.example.SocialMedia.dto.auth.RecaptchaResponse;
import com.example.SocialMedia.service.auth.RecaptchaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

@Service
public class RecaptchaServiceImpl implements RecaptchaService {

    private final RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(RecaptchaServiceImpl.class);

    @Value("${recaptcha.secret-key}")
    private String recaptchaSecretKey;

    @Value("${recaptcha.verify-url}")
    private String recaptchaVerifyUrl;

    public RecaptchaServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean verifyRecaptcha(String recaptchaToken) {
        if(recaptchaToken == null || recaptchaToken.isEmpty()) {
            return false;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("secret", recaptchaSecretKey);
        requestBody.add("response", recaptchaToken);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);
        try {
            RecaptchaResponse response = restTemplate.postForObject(
                    recaptchaVerifyUrl,
                    requestEntity,
                    RecaptchaResponse.class
            );
            return response != null && response.isSuccess();
        } catch (Exception e) {
            logger.error("Lỗi khi gọi API reCAPTCHA: {}", e.getMessage());
            return false;
        }
    }


}
