package kr.giljabi.gateway.service;

import kr.giljabi.gateway.model.UserInfo;
import kr.giljabi.gateway.response.TokenResponse;
import kr.giljabi.gateway.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ControllerTokenService {
    private final JwtProvider jwtProvider;

    public TokenResponse generate(UserInfo userInfo) {
        String token = jwtProvider.generateJwt(userInfo);
        String tokenExpiration = String.valueOf(jwtProvider.getAccessTokenExpiration());

        TokenResponse response = TokenResponse.builder()
                .tokenType("bearer")
                .accessToken(token)
                .expiresIn(tokenExpiration)
                .build();

        return response;
    }

}
