package iiitb.keyexhangeservice.controller;

import iiitb.keyexhangeservice.dto.PublicKeyExchangeRequest;
import iiitb.keyexhangeservice.entity.Employees;
import iiitb.keyexhangeservice.helper.JWTHelper;
import iiitb.keyexhangeservice.repo.EmployeeRepo;
import iiitb.keyexhangeservice.service.KeyExchangeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/exchange")
@RequiredArgsConstructor
public class KeyExchangeController {
    private static final Logger logger = LoggerFactory.getLogger(KeyExchangeController.class);

    private final KeyExchangeService keyExchangeService;
    private final JWTHelper jwtHelper;
    private final EmployeeRepo employeeRepo;

    @GetMapping
    public ResponseEntity<?> pollForOtherPublicKey(
            @RequestParam("email") String targetEmail,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            // Validate input parameters
            if (targetEmail == null || targetEmail.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Target email parameter is required"));
            }

            String jwt = extractToken(authHeader);
            String currentUserId = jwtHelper.extractUserId(jwt);
            System.out.println("cur email=" + currentUserId);
            // Additional validation
            if (currentUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid token: unable to extract user identity"));
            }
            System.out.println("targetemail=" + targetEmail);
            Optional<Employees> employeeOpt = employeeRepo.findByEmail(targetEmail);
            String key = null;
            if (employeeOpt.isPresent()) {
                Long id = employeeOpt.get().getId();
                System.out.println("Employee ID: " + id);
                key = keyExchangeService.getPublicKey(String.valueOf(id));
                System.out.println("Key: " + key);
            } else {
                System.out.println("Employee not found");

            }
            if (key == null) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            return ResponseEntity.ok(Map.of(
                    "otherPublicKey", key,
                    "sourceId", currentUserId,
                    "targetUser", targetEmail
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (JwtException e) {
            logger.warn("JWT validation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid token: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error in pollForOtherPublicKey", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> exchangePublicKey(
            @RequestBody @Valid PublicKeyExchangeRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String jwt = extractToken(authHeader);
            String myUserEmail = jwtHelper.extractUserId(jwt);
            System.out.println("myUserEmail=" + myUserEmail);
            if (myUserEmail == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid token: unable to extract user identity"));
            }

            // Validate request parameters
            if (request.getUserId() == null || request.getUserId().isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Target user ID is required"));
            }
            if (request.getPublicKey() == null || request.getPublicKey().isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Public key is required"));
            }

            keyExchangeService.storeAndGetPublicKey(myUserEmail, request.getPublicKey());
            String otherPublicKey = keyExchangeService.getPublicKey(request.getUserId());

            if (otherPublicKey == null) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            return ResponseEntity.ok(Map.of(
                    "otherPublicKey", otherPublicKey,
                    "sourceUser", myUserEmail,
                    "targetUser", request.getUserId()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (JwtException e) {
            logger.warn("JWT validation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid token: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error in exchangePublicKey", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error"));
        }
    }

    private String extractToken(String authHeader) throws IllegalArgumentException {
        if (authHeader == null || authHeader.isBlank()) {
            throw new IllegalArgumentException("Authorization header is required");
        }
        if (!authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header must start with 'Bearer '");
        }

        String token = authHeader.substring(7).trim();
        if (token.isEmpty()) {
            throw new IllegalArgumentException("Token cannot be empty");
        }
        return token;
    }
}