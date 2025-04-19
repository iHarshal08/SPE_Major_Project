package iiitb.loginservice.service;

import iiitb.loginservice.dto.LoginRequest;
//import com.prashantjain.esdtestingprogram.exception.CustomerNotFoundException;
//import iiitb.chat.entity.Credentials;
import iiitb.loginservice.dto.TokenResponse;
import iiitb.loginservice.helper.EncryptionService;
import iiitb.loginservice.helper.JWTHelper;
//import iiitb.chat.mapper.CredentialsMapper;
//import iiitb.chat.repo.CredentialsRepo;
import iiitb.loginservice.repo.EmployeeRepo;
import iiitb.loginservice.entity.Employees;
import iiitb.loginservice.repo.EmployeeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class LoginService {
    //private final CredentialsRepo credentialsRepo;
    //private final CredentialsMapper credentialsMapper;
    private final EncryptionService encryptionService;
    private final JWTHelper jwtHelper;
    private final EmployeeRepo employeeRepo;

    public TokenResponse login(LoginRequest request) {
        Employees employees = employeeRepo.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Email does not exist"));

//        System.out.println("Stored hash: " + employees.getPassword());
//        System.out.println("Raw password from request: " + request.password());
//        System.out.println(new BCryptPasswordEncoder().encode("password"));
//         Validate password (don't re-encode here)
        if (!encryptionService.validates(request.password(), employees.getPassword())) {
            throw new IllegalArgumentException("Wrong Password or Email");
        }

        String token = jwtHelper.generateToken(
                employees.getId(),
                employees.getEmail()
        );

        return new TokenResponse(token);
    }
}
