package site.goldenticket.domain.security.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import site.goldenticket.common.response.CommonResponse;
import site.goldenticket.common.security.authentication.dto.AuthenticationToken;
import site.goldenticket.common.security.authentication.dto.LoginRequest;
import site.goldenticket.domain.security.dto.ReissueRequest;
import site.goldenticket.domain.security.dto.YanoljaLoginResponse;
import site.goldenticket.domain.security.service.SecurityService;

@RestController
@RequiredArgsConstructor
public class SecurityController {

    private final SecurityService securityService;

    @PostMapping("/reissue")
    public ResponseEntity<CommonResponse<AuthenticationToken>> reissue(@RequestBody ReissueRequest reissueRequest) {
        AuthenticationToken token = securityService.reissue(reissueRequest);
        return ResponseEntity.ok(CommonResponse.ok(token));
    }

    @PostMapping("/yanolja-login")
    public ResponseEntity<CommonResponse<YanoljaLoginResponse>> yanoljaLogin(@RequestBody LoginRequest loginRequest) {
        YanoljaLoginResponse response = securityService.yanoljaLogin(loginRequest);
        return ResponseEntity.ok(CommonResponse.ok(response));
    }
}
