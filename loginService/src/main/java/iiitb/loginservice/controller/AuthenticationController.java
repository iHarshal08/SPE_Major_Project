package iiitb.loginservice.controller;

import iiitb.loginservice.dto.LoginRequest;
import iiitb.loginservice.dto.TokenResponse;
import iiitb.loginservice.service.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(loginService.login(request));
    }

//    @PostMapping("/create")
//    public ResponseEntity<String> createAdmin(@RequestBody @Valid LoginRequest request) {
//
//        return ResponseEntity.ok(loginService.createUser(request));
//    }
}
