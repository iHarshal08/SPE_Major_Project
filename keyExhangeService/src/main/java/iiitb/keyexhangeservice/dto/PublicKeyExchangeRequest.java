package iiitb.keyexhangeservice.dto;
import lombok.Data;

@Data
public class PublicKeyExchangeRequest {
    private String userId;
    private String publicKey;
}
