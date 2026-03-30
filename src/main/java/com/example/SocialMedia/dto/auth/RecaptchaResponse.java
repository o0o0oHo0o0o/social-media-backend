package com.example.SocialMedia.dto.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecaptchaResponse {
    private boolean success;

    @JsonProperty("challenge_ts")
    private String challenge_ts;

    private String hostname;

    @JsonProperty("error-codes")
    private List<String> errorCodes;
}
