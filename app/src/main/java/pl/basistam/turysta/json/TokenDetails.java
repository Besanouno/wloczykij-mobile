package pl.basistam.turysta.json;

import lombok.Data;

@Data
public class TokenDetails {
    private String accessToken;
    private String tokenType;
    private String refreshToken;
    private long expiresIn;
    private String scope;
    private String jti;
}
