package iiitb.keyexhangeservice.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class KeyExchangeService {
    private final Map<String, String> userPublicKeys = new ConcurrentHashMap<>();

    public String storeAndGetPublicKey(String userId, String publicKey) {
        userPublicKeys.put(userId, publicKey);
        System.out.println("📦 Key stored: " + userId + " => " + publicKey);
        System.out.println("🧾 Current map contents:");
        userPublicKeys.forEach((k, v) -> System.out.println(" - " + k + " => " + v));
        return publicKey;
    }

    public String getPublicKey(String userId) {
        return userPublicKeys.get(userId);
    }
}
