package iiitb.messagingservice.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MessageStorageService {
    private final Map<String, List<String>> userMessages = new ConcurrentHashMap<>();

    public void storeMessage(String userEmail, String encryptedMessage) {
        userMessages.computeIfAbsent(userEmail, k -> new ArrayList<>()).add(encryptedMessage);
    }

    public List<String> getMessagesForUser(String userEmail) {
        return userMessages.getOrDefault(userEmail, Collections.emptyList());
    }
}
