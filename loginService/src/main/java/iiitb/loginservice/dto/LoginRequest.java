package iiitb.loginservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record LoginRequest(

        @NotNull(message = "email name cannot be blank")
        @NotEmpty(message = "email name cannot be blank")
        @NotBlank(message = "email name cannot be blank")
        @JsonProperty("email")
        String email,

        @NotNull(message = "title name cannot be blank")
        @NotEmpty(message = "title name cannot be blank")
        @NotBlank(message = "title name cannot be blank")
        @JsonProperty("password")
        String password
){}
