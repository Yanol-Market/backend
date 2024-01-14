package site.goldenticket.domain.security.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.common.response.CommonResponse;
import site.goldenticket.common.security.authentication.dto.LoginRequest;
import site.goldenticket.domain.security.dto.YanoljaUserResponse;
import site.goldenticket.domain.security.service.SecurityService;

@RestController
@RequiredArgsConstructor
public class SecurityController {

    private final SecurityService securityService;

    @PostMapping("/yanolja-login")
    public ResponseEntity<CommonResponse<?>> yanoljaLogin(@RequestBody LoginRequest loginRequest) {
        YanoljaUserResponse yanoljaUserResponse = securityService.fetchYanoljaUser(loginRequest);
        try {
            return ResponseEntity.ok(CommonResponse.ok(securityService.generateToken(yanoljaUserResponse.id())));
        } catch (CustomException e) {
            return ResponseEntity.ok(CommonResponse.fail(yanoljaUserResponse));
        }
    }
}
