package iiitb.messagingservice.controller;

import iiitb.messagingservice.dto.MessageRequest;
import iiitb.messagingservice.entity.Employees;
import iiitb.messagingservice.helper.JWTHelper;
import iiitb.messagingservice.repo.EmployeeRepo;
import iiitb.messagingservice.service.MessageStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
//@RequestMapping("/messages")
@RequiredArgsConstructor
public class ChatController {
    private final MessageStorageService messageService;
    private final JWTHelper jwtHelper;
    private final EmployeeRepo employeeRepo;
    @GetMapping("/messages")
    public ResponseEntity<List<String>> getMessages(@RequestHeader("Authorization") String authHeader) {
        String userId = jwtHelper.extractUserId(authHeader.replace("Bearer ", ""));

        Optional<Employees> employeeOpt = employeeRepo.findById(Long.parseLong(userId));
        String key = null;
        if (employeeOpt.isPresent()) {
            String email = employeeOpt.get().getEmail();
            System.out.println("Employee ID: " + email);
            return ResponseEntity.ok(messageService.getMessagesForUser(email));
        } else {
            System.out.println("Empty");
            return ResponseEntity.notFound().build();
        }

    }

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(
            @RequestBody MessageRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        String sender = jwtHelper.extractUserId(authHeader.replace("Bearer ", ""));
        System.out.println("to:"+request.getTo());
        System.out.println("to:"+request.getMessage());
        messageService.storeMessage(request.getTo(), request.getMessage());
        return ResponseEntity.ok("Message sent");
    }
}
