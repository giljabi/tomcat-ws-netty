package kr.giljabi.gateway.service;

import kr.giljabi.gateway.model.UserInfo;
import kr.giljabi.gateway.repository.UserInfoRepository;
import kr.giljabi.gateway.request.TerminalTokenRequest;
import kr.giljabi.gateway.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserInfoService {
    private final UserInfoRepository userInfoRepository;
    private final ControllerTokenService tokenService;

    @Autowired
    public final PasswordEncoder passwordEncoder;

/*
    public Optional<CommonResponse> findByUserIdAndPassword(BoothTokenRequest request)  {
        log.info("userId={}", request.toString());
        String password = passwordEncoder.encode(request.getPassword());
        Optional<UserInfo> userInfo = userInfoRepository.findByUserIdAndPassword(request.getUserId(), "");
        log.info("userInfo={}", userInfo.toString());
        if(userInfo.isPresent())
            return Optional.of(new CommonResponse(userInfo));
        else
            return Optional.empty();
    }
*/

    public TokenResponse findByUserId(TerminalTokenRequest request)  {
        log.info("userId={}", request.getUserId());

        //사용자 유무
        UserInfo userInfo = userInfoRepository.findByUserId(request.getUserId());
        if(userInfo == null) {
            return null;
        }
        //비밀번호 확인
        if(passwordEncoder.matches(request.getPassword(), userInfo.getPassword())) {
            return tokenService.generate(userInfo);
        } else {
            return null;
        }
    }

}
